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

import java.io.IOException;

/**
 * 控制来自客户端的Servlet请求
 * 
 * @author Jacky.Li
 */
public class Controller implements Protocol, ConfigDefs {

	private Session session;

	/**
	 * 被保护的构造器，我们通过工厂模式来创建对象
	 */
	protected Controller() {
	}

	/**
	 * 通过工厂模式创建实例
	 * 
	 * @param aSession
	 *            the parent Session
	 * @return a Controller object (or derived)
	 * @throws PushletException
	 *             exception, usually misconfiguration
	 */
	public static Controller create(Session aSession) throws PushletException {
		Controller controller;
		try {
			controller = (Controller) Config.getClass(CONTROLLER_CLASS,
					"com.wondersgroup.eda.core.Controller").newInstance();
		}
		catch (Throwable t) {
			throw new PushletException("Cannot instantiate Controller from config", t);
		}
		controller.session = aSession;
		return controller;
	}

	/**
	 * 控制命令
	 */
	public void doCommand(Command command) {
		try {
			session.kick();
			session.setAddress(command.httpReq.getRemoteAddr());
			debug("doCommand() event=" + command.requestEvent);
			// 获得事件类型
			String eventType = command.requestEvent.getEventType();
			if (eventType.equals(Protocol.E_REFRESH)) {
				// 客户端推拉模式的刷新
				doRefresh(command);
			}
			else if (eventType.equals(Protocol.E_SUBSCRIBE)) {
				doSubscribe(command);
			}
			else if (eventType.equals(Protocol.E_UNSUBSCRIBE)) {
				doUnsubscribe(command);
			}
			else if (eventType.equals(Protocol.E_JOIN)) {
				doJoin(command);
			}
			else if (eventType.equals(Protocol.E_JOIN_LISTEN)) {
				// 加入监听 (e.g. REST应用)
				doJoinListen(command);
			}
			else if (eventType.equals(Protocol.E_LEAVE)) {
				doLeave(command);
			}
			else if (eventType.equals(Protocol.E_HEARTBEAT)) {
				doHeartbeat(command);
			}
			else if (eventType.equals(Protocol.E_PUBLISH)) {
				// 发布事件
				doPublish(command);
			}
			else if (eventType.equals(Protocol.E_LISTEN)) {
				// 监听被推送的事件
				doListen(command);
			}
			// Handle response back to client
			if (eventType.endsWith(Protocol.E_LISTEN) || eventType.equals(Protocol.E_REFRESH)) {
				// Data channel events
				// Loops until refresh or connection closed
				getSubscriber().fetchEvents(command);

			}
			else {
				// Send response for control commands
				sendControlResponse(command);
			}

		}
		catch (Throwable t) {
			warn("Exception in doCommand(): " + t);
			t.printStackTrace();
		}
	}

	public String toString() {
		return session.toString();
	}

	/**
	 * Handle heartbeat event.
	 */
	protected void doHeartbeat(Command aCommand) {

		// Set heartbeat acknowledgement to client
		aCommand.setResponseEvent(new Event(E_HEARTBEAT_ACK));
	}

	/**
	 * Handle Join request.
	 */
	protected void doJoin(Command aCommand) throws PushletException {
		Event responseEvent = null;
		try {
			session.start();
			// Determine format for encoding Events to client.
			// Default assume a userAgent window on the other end.
			String format = aCommand.requestEvent.getField(P_FORMAT, FORMAT_JAVASCRIPT);
			session.setFormat(format);
			responseEvent = new Event(E_JOIN_ACK);
			// Set unique subscriber id and encoding format
			responseEvent.setField(P_ID, session.getId());
			responseEvent.setField(P_FORMAT, format);
			info("joined");
		}
		catch (Throwable t) {
			session.stop();
			responseEvent = new Event(E_NACK);
			responseEvent.setField(P_ID, session.getId());
			responseEvent.setField(P_REASON, "unexpected error: " + t);
			warn("doJoin() error: " + t);
			t.printStackTrace();
		}
		finally {
			// Always set response event in command
			aCommand.setResponseEvent(responseEvent);
		}

	}

	/**
	 * Handle JoinListen request.
	 */
	protected void doJoinListen(Command aCommand) throws PushletException {

		// Basically bundles a join and a listen
		// This request is handly for simple apps that
		// need to do a single request to get events immediately
		// For example in RESTful apps.

		// First do regular join
		doJoin(aCommand);
		if (!aCommand.getResponseEvent().getEventType().equals(E_NACK)) {
			// If successful do the listen
			doListen(aCommand);
			if (!aCommand.getResponseEvent().getEventType().equals(E_NACK)) {
				// If still ok do the listen ack
				aCommand.getResponseEvent().setField(P_EVENT, E_JOIN_LISTEN_ACK);
			}
		}
	}

	/**
	 * Handle Leave request.
	 */
	protected void doLeave(Command aCommand) throws IOException {
		Event responseEvent = null;
		try {
			// Also removes all subscriptions
			session.stop();
			responseEvent = new Event(E_LEAVE_ACK);
			responseEvent.setField(P_ID, session.getId());
			info("left");
		}
		catch (Throwable t) {
			responseEvent = new Event(E_NACK);
			responseEvent.setField(P_ID, session.getId());
			responseEvent.setField(P_REASON, "unexpected error: " + t);
			warn("doLeave() error: " + t);
			t.printStackTrace();
		}
		finally {
			// Always set response event in command
			aCommand.setResponseEvent(responseEvent);
		}
	}

	/**
	 * 控制监听请求
	 */
	protected void doListen(Command command) throws PushletException {
		String mode = MODE_STREAM;

		if (Config.getBoolProperty(LISTEN_FORCE_PULL_ALL)) {
			mode = MODE_PULL;
		}
		else {
			mode = command.requestEvent.getField(P_MODE, MODE_STREAM);
			String userAgent = command.httpReq.getHeader("User-Agent");
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				for (int i = 0; i < session.FORCED_PULL_AGENTS.length; i++) {
					if ((userAgent.indexOf(session.FORCED_PULL_AGENTS[i]) != -1)) {
						info("Forcing pull mode for agent=" + userAgent);
						mode = MODE_PULL;
						break;
					}
				}
			}
			else {
				userAgent = "unknown";
			}
		}
		getSubscriber().setMode(mode);
		Event listenAckEvent = new Event(E_LISTEN_ACK);
		String subject = command.requestEvent.getField(P_SUBJECT);
		if (subject != null) {
			String label = command.requestEvent.getField(Protocol.P_SUBSCRIPTION_LABEL);
			Subscription subscription = getSubscriber().addSubscription(subject, label);
			listenAckEvent.setField(P_SUBSCRIPTION_ID, subscription.getId());
			if (label != null) {
				listenAckEvent.setField(P_SUBSCRIPTION_LABEL, label);
			}
		}
		listenAckEvent.setField(P_ID, session.getId());
		listenAckEvent.setField(P_MODE, mode);
		listenAckEvent.setField(P_FORMAT, session.getFormat());
		getSubscriber().start();

		command.setResponseEvent(listenAckEvent);
		info("Listening mode=" + mode + " userAgent=" + session.getUserAgent());
	}

	/**
	 * 控制发布请求
	 */
	protected void doPublish(Command aCommand) {
		Event responseEvent = null;
		try {
			String subject = aCommand.requestEvent.getField(Protocol.P_SUBJECT);
			if (subject == null) {
				// 返回错误响应
				responseEvent = new Event(E_NACK);
				responseEvent.setField(P_ID, session.getId());
				responseEvent.setField(P_REASON, "no subject provided");
			}
			else {
				aCommand.requestEvent.setField(P_FROM, session.getId());
				aCommand.requestEvent.setField(P_EVENT, E_DATA);
				String to = aCommand.requestEvent.getField(P_TO);
				if (to != null) {
					Dispatcher.getInstance().unicast(aCommand.requestEvent, to);
				}
				else {
					debug("doPublish() event=" + aCommand.requestEvent);
					Dispatcher.getInstance().multicast(aCommand.requestEvent);
				}
				responseEvent = new Event(E_PUBLISH_ACK);
			}
		}
		catch (Throwable t) {
			responseEvent = new Event(E_NACK);
			responseEvent.setField(P_ID, session.getId());
			responseEvent.setField(P_REASON, "unexpected error: " + t);
			warn("doPublish() error: " + t);
			t.printStackTrace();
		}
		finally {
			aCommand.setResponseEvent(responseEvent);
		}
	}

	/**
	 * 控制刷新事件
	 */
	protected void doRefresh(Command aCommand) {
		aCommand.setResponseEvent(new Event(E_REFRESH_ACK));
	}

	/**
	 * 控制订阅者请求
	 */
	protected void doSubscribe(Command aCommand) throws IOException {
		Event responseEvent = null;
		try {
			String subject = aCommand.requestEvent.getField(Protocol.P_SUBJECT);
			Subscription subscription = null;
			if (subject == null) {
				responseEvent = new Event(E_NACK);
				responseEvent.setField(P_ID, session.getId());
				responseEvent.setField(P_REASON, "no subject provided");
			}
			else {
				String label = aCommand.requestEvent.getField(Protocol.P_SUBSCRIPTION_LABEL);
				subscription = getSubscriber().addSubscription(subject, label);
				responseEvent = new Event(E_SUBSCRIBE_ACK);
				responseEvent.setField(P_ID, session.getId());
				responseEvent.setField(P_SUBJECT, subject);
				responseEvent.setField(P_SUBSCRIPTION_ID, subscription.getId());
				if (label != null) {
					responseEvent.setField(P_SUBSCRIPTION_LABEL, label);
				}
				info("subscribed to " + subject + " sid=" + subscription.getId());
			}
		}
		catch (Throwable t) {
			responseEvent = new Event(E_NACK);
			responseEvent.setField(P_ID, session.getId());
			responseEvent.setField(P_REASON, "unexpected error: " + t);
			warn("doSubscribe() error: " + t);
			t.printStackTrace();
		}
		finally {
			aCommand.setResponseEvent(responseEvent);
		}
	}

	/**
	 * 控制非订阅者请求
	 */
	protected void doUnsubscribe(Command command) throws IOException {
		Event responseEvent = null;
		try {
			String subscriptionId = command.requestEvent.getField(Protocol.P_SUBSCRIPTION_ID);
			if (subscriptionId == null) {
				// Unsuscbribe all
				getSubscriber().removeSubscriptions();
				responseEvent = new Event(E_UNSUBSCRIBE_ACK);
				responseEvent.setField(P_ID, session.getId());
				info("unsubscribed all");
			}
			else {
				Subscription subscription = getSubscriber().removeSubscription(subscriptionId);
				if (subscription == null) {
					responseEvent = new Event(E_NACK);
					responseEvent.setField(P_ID, session.getId());
					responseEvent.setField(P_REASON, "no subscription for sid=" + subscriptionId);
					warn("unsubscribe: no subscription for sid=" + subscriptionId);
				}
				else {
					responseEvent = new Event(E_UNSUBSCRIBE_ACK);
					responseEvent.setField(P_ID, session.getId());
					responseEvent.setField(P_SUBSCRIPTION_ID, subscription.getId());
					responseEvent.setField(P_SUBJECT, subscription.getSubject());
					if (subscription.getLabel() != null) {
						responseEvent.setField(P_SUBSCRIPTION_LABEL, subscription.getLabel());
					}
					info("unsubscribed sid= " + subscriptionId);
				}
			}
		}
		catch (Throwable t) {
			responseEvent = new Event(E_NACK);
			responseEvent.setField(P_ID, session.getId());
			responseEvent.setField(P_REASON, "unexpected error: " + t);
			warn("doUnsubscribe() error: " + t);
			t.printStackTrace();
		}
		finally {
			command.setResponseEvent(responseEvent);
		}
	}

	public Subscriber getSubscriber() {
		return session.getSubscriber();
	}

	protected void sendControlResponse(Command command) {
		try {
			command.sendResponseHeaders();
			command.getClientAdapter().start();
			command.getClientAdapter().push(command.getResponseEvent());
			command.getClientAdapter().stop();
		}
		catch (Throwable t) {
			session.stop();
		}
	}

	protected void info(String s) {
		session.info("[Controller] " + s);
	}

	protected void warn(String s) {
		session.warn("[Controller] " + s);
	}

	protected void debug(String s) {
		session.debug("[Controller] " + s);
	}

}
