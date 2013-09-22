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

/**
 * 定义所有配置文件中的属性字段
 * 
 * @author Jacky.Li
 */
public interface ConfigDefs {
	
	/**
	 * 类工厂定义, 可以加入你自己的客户化类实现
	 */
	public static final String CONTROLLER_CLASS = "controller.class";
	public static final String DISPATCHER_CLASS = "dispatcher.class";
	public static final String LOGGER_CLASS = "logger.class";
	public static final String SESSION_MANAGER_CLASS = "sessionmanager.class";
	public static final String SESSION_CLASS = "session.class";
	public static final String SUBSCRIBER_CLASS = "subscriber.class";
	public static final String SUBSCRIPTION_CLASS = "subscription.class";

	/**
	 * Session管理
	 */
	public static final String SESSION_ID_SIZE = "session.id.size";
	public static final String SESSION_ID_GENERATION = "session.id.generation";
	public static final String SESSION_ID_GENERATION_UUID = "uuid";
	public static final String SESSION_ID_GENERATION_RANDOMSTRING = "randomstring";
	public static final String SESSION_TIMEOUT_MINS = "session.timeout.mins";

	public static final String SOURCES_ACTIVATE = "sources.activate";

	/**
	 * 日志
	 */
	public static final String LOG_LEVEL = "log.level";
	public static final int LOG_LEVEL_FATAL = 1;
	public static final int LOG_LEVEL_ERROR = 2;
	public static final int LOG_LEVEL_WARN = 3;
	public static final int LOG_LEVEL_INFO = 4;
	public static final int LOG_LEVEL_DEBUG = 5;
	public static final int LOG_LEVEL_TRACE = 6;

	/**
	 * 队列
	 */
	public static final String QUEUE_SIZE = "queue.size";
	public static final String QUEUE_READ_TIMEOUT_MILLIS = "queue.read.timeout.millis";
	public static final String QUEUE_WRITE_TIMEOUT_MILLIS = "queue.write.timeout.millis";

	/**
	 * 监听模式
	 */
	public static final String LISTEN_FORCE_PULL_ALL = "listen.force.pull.all";
	public static final String LISTEN_FORCE_PULL_AGENTS = "listen.force.pull.agents";


	public static final String PULL_REFRESH_TIMEOUT_MILLIS = "pull.refresh.timeout.millis";
	public static final String PULL_REFRESH_WAIT_MIN_MILLIS = "pull.refresh.wait.min.millis";
	public static final String PULL_REFRESH_WAIT_MAX_MILLIS = "pull.refresh.wait.max.millis";


	public static final String POLL_REFRESH_TIMEOUT_MILLIS = "poll.refresh.timeout.millis";
	public static final String POLL_REFRESH_WAIT_MIN_MILLIS = "poll.refresh.wait.min.millis";
	public static final String POLL_REFRESH_WAIT_MAX_MILLIS = "poll.refresh.wait.max.millis";

}
