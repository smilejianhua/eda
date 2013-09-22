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
import com.wondersgroup.eda.util.Sys;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.io.File;

/**
 * 控制事件资源的生命周期
 * 
 * @author Jacky.Li
 */
public class EventSourceManager {
	
	private static Vector eventSources = new Vector(0);
	private static final String PROPERTIES_FILE = "sources.properties";

	/**
	 * 通过配置（source.properties）文件初始化事件资源
	 */
	public static void start(String aDirPath) {
		Log.info("EventSourceManager: start");
		Properties properties = null;
		try {
			properties = Sys.loadPropertiesResource(PROPERTIES_FILE);
		}
		catch (Throwable t) {
			String filePath = aDirPath + File.separator + PROPERTIES_FILE;
			Log.info("EventSourceManager: cannot load " + PROPERTIES_FILE
					+ " from classpath, will try from " + filePath);
			try {
				properties = Sys.loadPropertiesFile(filePath);
			}
			catch (Throwable t2) {
				Log.fatal("EventSourceManager: cannot load properties file from " + filePath, t);
				Log.warn("EventSourceManager: not starting local event sources (maybe that is what you want)");
				return;
			}
		}
		// Create event source collection
		eventSources = new Vector(properties.size());
		// Add the configured sources
		for (Enumeration e = properties.keys(); e.hasMoreElements();) {
			String nextKey = (String) e.nextElement();
			String nextClass = properties.getProperty(nextKey);
			EventSource nextEventSource = null;
			try {
				nextEventSource = (EventSource) Class.forName(nextClass).newInstance();
				Log.info("created EventSource: key=" + nextKey + " class=" + nextClass);
				eventSources.addElement(nextEventSource);
			}
			catch (Exception ex) {
				Log.warn("Cannot create EventSource: class=" + nextClass, ex);
			}
		}
		activate();
	}

	/**
	 * 激活所有的事件资源
	 */
	public static void activate() {
		Log.info("Activating " + eventSources.size() + " EventSources");
		for (int i = 0; i < eventSources.size(); i++) {
			((EventSource) eventSources.elementAt(i)).activate();
		}
		Log.info("EventSources activated");
	}

	/**
	 * 钝化所有的事件资源
	 */
	public static void passivate() {
		Log.info("Passivating " + eventSources.size() + " EventSources");
		for (int i = 0; i < eventSources.size(); i++) {
			((EventSource) eventSources.elementAt(i)).passivate();
		}
		Log.info("EventSources passivated");
	}

	/**
	 * 挂起事件资源
	 */
	public static void stop() {
		Log.info("Stopping " + eventSources.size() + " EventSources...");
		for (int i = 0; i < eventSources.size(); i++) {
			((EventSource) eventSources.elementAt(i)).stop();
		}
		Log.info("EventSources stopped");
	}
}

