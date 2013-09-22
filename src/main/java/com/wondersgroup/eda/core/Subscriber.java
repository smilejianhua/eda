/** 
 * Copyright (c) 2012-2015 Wonders Information Co.,Ltd. All Rights Reserved.
 * 5/F 1600 Nanjing RD(W), Shanghai 200040, P.R.C 
 *
 * This software is the confidential and proprietary information of Wonders Group.
 * (Public Service Division / Research & Development Center). You shall not disclose such
 * Confidential Information and shall use it only in accordance with 
 * the terms of the license agreement you entered into with Wonders Group. 
 *
 * Distributable under GNU LGPL license by gun.org
 */

package com.wondersgroup.eda.core;

import com.wondersgroup.eda.util.PushletException;
import com.wondersgroup.eda.util.Rand;
import com.wondersgroup.eda.util.Sys;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;

/**
 * Handles data channel between dispatcher and client.
 *
 * @author Jacky.Li
 */
public class Subscriber implements Protocol, ConfigDefs {
	private Session session;

	/**
	 * 队列阻塞
	 */
	private EventQueue eventQueue = new EventQueue(Config.getIntProperty(QUEUE_SIZE));

	/**
	 * URL to be used in refresh requests in pull/poll modes.
	 */
	private long queueReadTimeoutMillis = Config.getLongProperty(QUEUE_READ_TIMEOUT_MILLIS);
	private long queueWriteTimeoutMillis = Config.getLongProperty(QUEUE_WRITE_TIMEOUT_MILLIS);
	private long refreshTimeoutMillis = Config.getLongProperty(PULL_REFRESH_TIMEOUT_MILLIS);
	volatile long lastAlive = Sys.now();

	/**
	 * Map of active subscriptions, keyed by their subscription id.
	 */
	private Map subscriptions = Collections.synchronizedMap(new HashMap(3));

	/**
	 * Are we able to accept/send events ?.
	 */
	private volatile boolean active;

	/**
	 * Transfer mode (stream, pull, poll).
	 */
	private String mode;

	/**
	 * Protected constructor as we create through factory method.
	 */
	protected Subscriber() {
	}

	/**
	 * 通过工厂模式创建实例
	 * 
	 * @param aSession
	 *            the parent Session
	 * @return a Subscriber object (or derived)
	 * @throws PushletException
	 *             exception, usually misconfiguration
	 */
	public static Subscriber create(Session aSession) throws PushletException {
		Subscriber subscriber;
		try {
			subscriber = (Subscriber) Config.getClass(SUBSCRIBER_CLASS,
					"com.wondersgroup.eda.core.Subscriber").newInstance();
		}
		catch (Throwable t) {
			throw new PushletException("Cannot instantiate Subscriber from config", t);
		}
		subscriber.session = aSession;
		return subscriber;
	}

	public void start() {
		active = true;
	}

	public void stop() {
		removeSubscriptions();
		active = false;
	}

	public void bailout() {
		session.stop();
	}

	public boolean isActive() {
		return active;
	}

	public Session getSession() {
		return session;
	}

	public String getId() {
		return session.getId();
	}

	public Subscription[] getSubscriptions() {
		//TODO 可优化点
		return (Subscription[]) subscriptions.values().toArray(new Subscription[0]);
	}

	/**
	 * 增加一个订购
	 */
	public Subscription addSubscription(String subject, String label) throws PushletException {
		Subscription subscription = Subscription.create(subject, label);
		subscriptions.put(subscription.getId(), subscription);
		info("Subscription added subject=" + subject + " sid=" + subscription.getId() + " label="
				+ label);
		return subscription;
	}

	/**
	 * 删除订购
	 */
	public Subscription removeSubscription(String subscriptionId) {
		Subscription subscription = (Subscription) subscriptions.remove(subscriptionId);
		if (subscription == null) {
			warn("No subscription found sid=" + subscriptionId);
			return null;
		}
		info("Subscription removed subject=" + subscription.getSubject() + " sid="
				+ subscription.getId() + " label=" + subscription.getLabel());
		return subscription;
	}

	/**
	 * 移除所有备注
	 */
	public void removeSubscriptions() {
		subscriptions.clear();
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String aMode) {
		mode = aMode;
	}

	public long getRefreshTimeMillis() {
		String minWaitProperty = PULL_REFRESH_WAIT_MIN_MILLIS;
		String maxWaitProperty = PULL_REFRESH_WAIT_MAX_MILLIS;
		if (mode.equals((MODE_POLL))) {
			minWaitProperty = POLL_REFRESH_WAIT_MIN_MILLIS;
			maxWaitProperty = POLL_REFRESH_WAIT_MAX_MILLIS;

		}
		return Rand.randomLong(Config.getLongProperty(minWaitProperty),
				Config.getLongProperty(maxWaitProperty));
	}

	/**
	 * 从队列中获取事件并推送到客户端，此为关键方法
	 */
	public void fetchEvents(Command aCommand) throws PushletException {

		String refreshURL = aCommand.httpReq.getRequestURI() + "?" + P_ID + "=" + session.getId()
				+ "&" + P_EVENT + "=" + E_REFRESH;

		// This is the only thing required to support "poll" mode
		if (mode.equals(MODE_POLL)) {
			queueReadTimeoutMillis = 0;
			refreshTimeoutMillis = Config.getLongProperty(POLL_REFRESH_TIMEOUT_MILLIS);
		}

		// Required for fast bailout (tomcat)
		aCommand.httpRsp.setBufferSize(128);

		// Try to prevent caching in any form.
		aCommand.sendResponseHeaders();

		// Let clientAdapter determine how to send event
		ClientAdapter clientAdapter = aCommand.getClientAdapter();
		Event responseEvent = aCommand.getResponseEvent();
		try {
			clientAdapter.start();

			// Send first event (usually hb-ack or listen-ack)
			clientAdapter.push(responseEvent);

			// In pull/poll mode and when response is listen-ack or
			// join-listen-ack,
			// return and force refresh immediately
			// such that the client recieves response immediately over this
			// channel.
			// This is usually when loading the browser app for the first time
			if ((mode.equals(MODE_POLL) || mode.equals(MODE_PULL))
					&& responseEvent.getEventType().endsWith(Protocol.E_LISTEN_ACK)) {
				sendRefresh(clientAdapter, refreshURL);
				// We should come back later with refresh event...
				return;
			}
		}
		catch (Throwable t) {
			bailout();
			return;
		}
		Event[] events = null;

		long eventSeqNr = 1;
		while (isActive()) {
			lastAlive = Sys.now();
			session.kick();

			// Get next events; blocks until timeout or entire contents
			// of event queue is returned. Note that "poll" mode
			// will return immediately when queue is empty.
			try {
				// Put heartbeat in queue when starting to listen in stream mode
				// This speeds up the return of *_LISTEN_ACK
				if (mode.equals(MODE_STREAM) && eventSeqNr == 1) {
					eventQueue.enQueue(new Event(E_HEARTBEAT));
				}
				events = eventQueue.deQueueAll(queueReadTimeoutMillis);
			}
			catch (InterruptedException ie) {
				warn("interrupted");
				bailout();
			}
			// Send heartbeat when no events received
			if (events == null) {
				events = new Event[1];
				events[0] = new Event(E_HEARTBEAT);
			}
			for (int i = 0; i < events.length; i++) {
				if (events[i].getEventType().equals(E_ABORT)) {
					warn("Aborting Subscriber");
					bailout();
				}
				// 推送接下来一个事件到客户端
				try {
					events[i].setField(P_SEQ, eventSeqNr++);
					clientAdapter.push(events[i]);
				}
				catch (Throwable t) {
					bailout();
					return;
				}
			}
			if (mode.equals(MODE_PULL) || mode.equals(MODE_POLL)) {
				sendRefresh(clientAdapter, refreshURL);
				break;
			}
		}
	}

	/**
	 * Determine if we should receive event.
	 */
	public Subscription match(Event event) {
		Subscription[] subscriptions = getSubscriptions();
		for (int i = 0; i < subscriptions.length; i++) {
			if (subscriptions[i].match(event)) {
				return subscriptions[i];
			}
		}
		return null;
	}

	/**
	 * Dispather发布的事件
	 */
	public void onEvent(Event theEvent) {
		if (!isActive()) {
			return;
		}
		long now = Sys.now();
		if (now - lastAlive > refreshTimeoutMillis) {
			warn("not alive for at least: " + refreshTimeoutMillis + "ms, leaving...");
			bailout();
			return;
		}
		try {
			if (!eventQueue.enQueue(theEvent, queueWriteTimeoutMillis)) {
				warn("queue full, bailing out...");
				bailout();
			}
		}
		catch (InterruptedException ie) {
			bailout();
		}
	}

	/**
	 * 针对推拉客户端发出刷新命令
	 */
	protected void sendRefresh(ClientAdapter clientAdapter, String refreshURL) {
		Event refreshEvent = new Event(E_REFRESH);
		refreshEvent.setField(P_WAIT, "" + getRefreshTimeMillis());
		refreshEvent.setField(P_URL, refreshURL);
		try {
			clientAdapter.push(refreshEvent);
			clientAdapter.stop();
		}
		catch (Throwable t) {
			bailout();
		}
	}

	protected void info(String s) {
		session.info("[Subscriber] " + s);
	}

	protected void warn(String s) {
		session.warn("[Subscriber] " + s);
	}

	protected void debug(String s) {
		session.debug("[Subscriber] " + s);
	}

	public String toString() {
		return session.toString();
	}
}
