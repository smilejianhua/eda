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

import com.wondersgroup.eda.util.Log;
import com.wondersgroup.eda.util.PushletException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 路由事件给相关订阅者
 * 
 * @author Jacky.Li
 */
public class Dispatcher implements Protocol, ConfigDefs {

	/**
	 * 注意，单例模式
	 */
	private static Dispatcher instance;
	protected SessionManagerVisitor sessionManagerVisitor;

	static {
		try {
			instance = (Dispatcher) Config.getClass(DISPATCHER_CLASS,
					"com.wondersgroup.eda.core.Dispatcher").newInstance();
			Log.info("Dispatcher created className=" + instance.getClass());
		}
		catch (Throwable t) {
			Log.fatal("Cannot instantiate Dispatcher from config", t);
		}
	}

	protected Dispatcher() {
	}

	public static Dispatcher getInstance() {
		return instance;
	}

	/**
	 * 将事件推到相应订阅者
	 */
	public synchronized void broadcast(Event anEvent) {
		try {
			Object[] args = new Object[2];
			args[1] = anEvent;
			Method method = sessionManagerVisitor.getMethod("visitBroadcast");
			SessionManager.getInstance().apply(sessionManagerVisitor, method, args);
		}
		catch (Throwable t) {
			Log.error("Error calling SessionManager.apply: ", t);
		}
	}

	/**
	 * 根据Event主题将相应事件推送到订阅者
	 */
	public synchronized void multicast(Event anEvent) {
		try {
			Method method = sessionManagerVisitor.getMethod("visitMulticast");
			Object[] args = new Object[2];
			args[1] = anEvent;
			SessionManager.getInstance().apply(sessionManagerVisitor, method, args);
		}
		catch (Throwable t) {
			Log.error("Error calling SessionManager.apply: ", t);
		}
	}

	/**
	 * 将事件推送到特殊的订阅者
	 */
	public synchronized void unicast(Event event, String aSessionId) {
		Session session = SessionManager.getInstance().getSession(aSessionId);
		if (session == null) {
			Log.warn("unicast: session with id=" + aSessionId + " does not exist");
			return;
		}
		session.getSubscriber().onEvent((Event) event.clone());
	}

	/**
	 * 启动调度
	 */
	public void start() throws PushletException {
		Log.info("Dispatcher started");
		sessionManagerVisitor = new SessionManagerVisitor();
	}

	/**
	 * 停止调度
	 */
	public void stop() {
		Log.info("Dispatcher stopped: broadcast abort to all subscribers");
		broadcast(new Event(E_ABORT));
	}

	/**
	 * 私有内部类，控制SessionManager的访问者回调函数
	 */
	private class SessionManagerVisitor {
		private final Map visitorMethods = new HashMap(2);
		SessionManagerVisitor() throws PushletException {
			try {
				Class[] argsClasses = { Session.class, Event.class };
				visitorMethods.put("visitMulticast",
						this.getClass().getMethod("visitMulticast", argsClasses));
				visitorMethods.put("visitBroadcast",
						this.getClass().getMethod("visitBroadcast", argsClasses));
			}
			catch (NoSuchMethodException e) {
				throw new PushletException("Failed to setup SessionManagerVisitor", e);
			}
		}

		public Method getMethod(String aName) {
			return (Method) visitorMethods.get(aName);

		}

		public void visitBroadcast(Session aSession, Event event) {
			aSession.getSubscriber().onEvent((Event) event.clone());
		}

		public void visitMulticast(Session aSession, Event event) {
			Subscriber subscriber = aSession.getSubscriber();
			Event clonedEvent;
			Subscription subscription;
			if ((subscription = subscriber.match(event)) != null) {
				clonedEvent = (Event) event.clone();
				clonedEvent.setField(P_SUBSCRIPTION_ID, subscription.getId());
				if (subscription.getLabel() != null) {
					event.setField(P_SUBSCRIPTION_LABEL, subscription.getLabel());
				}
				subscriber.onEvent(clonedEvent);
			}
		}
	}
}
