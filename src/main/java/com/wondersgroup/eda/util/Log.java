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

import com.wondersgroup.eda.core.Config;
import com.wondersgroup.eda.core.ConfigDefs;

/**
 * Logging wrapper.
 * <p/>
 * Provides a hook to direct logging to your own logging library. Override the
 * DefaultLogger class by setting "logger.class" in pushlet.properties to your
 * own logger to integrate your own logging library.
 * 
 * @author Just van den Broecke
 * @version $Id: Log.java,v 1.5 2007/12/07 12:57:40 justb Exp $
 */
public class Log implements ConfigDefs {
	/**
	 * Init with default to have at least some logging.
	 */
	private static PushletLogger logger = new DefaultLogger();

	/**
	 * General purpose initialization.
	 */
	static public void init() {
		try {
			logger = (PushletLogger) Config.getClass(LOGGER_CLASS,
					"com.wondersgroup.eda.util.DefaultLogger").newInstance();
		}
		catch (Throwable t) {
			// Hmmm cannot log this since we don't have a log...
			System.out.println("Cannot instantiate Logger from config ex=" + t);
			return;
		}
		logger.init();
		// Set log level
		logger.setLevel(Config.getIntProperty(Config.LOG_LEVEL));
		logger.info("Logging intialized logger class=" + logger.getClass());
	}

	/**
	 * Log message for trace level.
	 * 
	 * @param aMessage
	 *            the message to be logged
	 */
	static public void trace(String aMessage) {
		logger.debug(aMessage);
	}

	/**
	 * Log message for debug level.
	 * 
	 * @param aMessage
	 *            the message to be logged
	 */
	static public void debug(String aMessage) {
		logger.debug(aMessage);
	}

	/**
	 * Log message for info level.
	 * 
	 * @param aMessage
	 *            the message to be logged
	 */
	static public void info(String aMessage) {
		logger.info(aMessage);
	}

	/**
	 * Log message for warning level.
	 * 
	 * @param aMessage
	 *            the message to be logged
	 */
	static public void warn(String aMessage) {
		logger.warn(aMessage);
	}

	/**
	 * Log message for warning level with exception.
	 * 
	 * @param aMessage
	 *            the message to be logged
	 * @param aThrowable
	 *            the exception
	 */
	static public void warn(String aMessage, Throwable aThrowable) {
		logger.warn(aMessage, aThrowable);
	}

	/**
	 * Log message for error level.
	 * 
	 * @param aMessage
	 *            the message to be logged
	 */
	static public void error(String aMessage) {
		logger.error(aMessage);
	}

	/**
	 * Log message (error level with exception).
	 * 
	 * @param aMessage
	 *            the message to be logged
	 * @param aThrowable
	 *            the exception
	 */
	static public void error(String aMessage, Throwable aThrowable) {
		logger.error(aMessage, aThrowable);
	}

	/**
	 * Log message for fatal level.
	 * 
	 * @param aMessage
	 *            the message to be logged
	 */
	static public void fatal(String aMessage) {
		logger.fatal(aMessage);
	}

	/**
	 * Log message (fatal level with exception).
	 * 
	 * @param aMessage
	 *            the message to be logged
	 * @param aThrowable
	 *            the exception
	 */
	static public void fatal(String aMessage, Throwable aThrowable) {
		logger.fatal(aMessage, aThrowable);
	}

	/**
	 * Set log level
	 * 
	 * @param aLevel
	 *            the message to be logged
	 */
	static public void setLevel(int aLevel) {
		logger.setLevel(aLevel);
	}
}