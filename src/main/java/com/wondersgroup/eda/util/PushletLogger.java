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
package com.wondersgroup.eda.util;

import com.wondersgroup.eda.core.ConfigDefs;

/**
 * 定义日志接口，可以支持不同的日志实现
 *
 * @author Jacky.Li
 */
public interface PushletLogger extends ConfigDefs {

	/**
	 * 用于初始化日志系统的方法
	 */
	public void init();

	/**
	 * Log message for trace level.	 
	 */
	public void trace(String aMessage);

	/**
	 * Log message for debug level.
	 */
	public void debug(String aMessage);

	/**
	 * Log message for info level.
	 */
	public void info(String aMessage);

	/**
	 * Log message for warning level.
	 */
	public void warn(String aMessage);

	/**
	 * Log message for warning level with exception.
	 */
	public void warn(String aMessage, Throwable aThrowable);

	/**
	 * Log message for error level.
	 *
	 * @param aMessage the message to be logged
	 */
	public void error(String aMessage);

	/**
	 * Log message (error level with exception).
	 */
	public void error(String aMessage, Throwable aThrowable);

	/**
	 * 致命层级，日志信息Log message for fatal level.
	 *
	 * @param aMessage the message to be logged
	 */
	public void fatal(String aMessage);

	/**
	 * Log message (fatal level with exception).
	 *
	 * @param aMessage   the message to be logged
	 * @param aThrowable the exception
	 */
	public void fatal(String aMessage, Throwable aThrowable);

	/**
	 * Set log level
	 *
	 * @param aLevel a valid Level from ConfigDefs
	 */
	public void setLevel(int aLevel);
}
