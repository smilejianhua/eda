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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Logger to use Log4j for logging.
 * <p/>
 * Logs using Log4j. This class will require a log4j library in the classpath of
 * the Pushlet.
 * 
 * @author Uli Romahn
 * @version $Id: Log4jLogger.java,v 1.1 2007/12/07 12:57:40 justb Exp $
 */
public class Log4jLogger implements PushletLogger {

	/**
	 * Level intialized with default.
	 */
	private Logger logger = LogManager.getLogger("pushlet");

	public void init() {
		setLevel(LOG_LEVEL_INFO);
	}

	public void debug(String aMessage) {
		if (!logger.isDebugEnabled()) {
			return;
		}
		logger.debug(aMessage);
	}

	public void error(String aMessage) {
		logger.error(aMessage);
	}

	public void error(String aMessage, Throwable aThrowable) {
		logger.error(aMessage, aThrowable);
	}

	public void fatal(String aMessage) {
		logger.fatal(aMessage);
	}

	public void fatal(String aMessage, Throwable aThrowable) {
		logger.fatal(aMessage, aThrowable);
	}

	public void info(String aMessage) {
		if (!logger.isInfoEnabled()) {
			return;
		}
		logger.info(aMessage);
	}

	public void trace(String aMessage) {
		logger.trace(aMessage);
	}

	public void warn(String aMessage) {
		logger.warn(aMessage);
	}

	public void warn(String aMessage, Throwable aThrowable) {
		logger.warn(aMessage, aThrowable);
	}

	public void setLevel(int aLevel) {
		if (aLevel < LOG_LEVEL_FATAL) {
			logger.setLevel(Level.OFF);
		}
		else {
			switch (aLevel) {
			case LOG_LEVEL_FATAL:
				logger.setLevel(Level.FATAL);
				break;
			case LOG_LEVEL_ERROR:
				logger.setLevel(Level.ERROR);
				break;
			case LOG_LEVEL_WARN:
				logger.setLevel(Level.WARN);
				break;
			case LOG_LEVEL_INFO:
				logger.setLevel(Level.INFO);
				break;
			case LOG_LEVEL_DEBUG:
				logger.setLevel(Level.DEBUG);
				break;
			case LOG_LEVEL_TRACE:
				logger.setLevel(Level.TRACE);
				break;
			default:
				logger.setLevel(Level.INFO);
			}
		}
	}
}
