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
import com.wondersgroup.eda.util.Sys;

import java.io.File;
import java.util.Properties;

/**
 * 装载和控制所有的配置信息
 * 
 * @author Jacky.Li
 */
public class Config implements ConfigDefs {
	private static final String PROPERTIES_FILE = "pushlet.properties";
	private static Properties properties;

	/**
	 * 工厂模式，通过配置文件中类名称形成其对象实例
	 * 
	 * @param classNameProp
	 *            property name e.g. "session.class"
	 * @return an instance of class denoted by property
	 * @throws PushletException
	 *             when class cannot be instantiated
	 */
	public static Object createObject(String classNameProp, String defaultValue)
			throws PushletException {
		Class clazz = getClass(classNameProp, defaultValue);
		try {
			return clazz.newInstance();
		}
		catch (Throwable t) {
			throw new PushletException("Cannot instantiate class for " + classNameProp + "="
					+ clazz, t);
		}
	}

	/**
	 * 工厂模式，通过配置文件中类名称形成其对象实例
	 * 
	 * @param aClassNameProp
	 *            property name e.g. "session.class"
	 * @return a Class object denoted by property
	 * @throws PushletException
	 *             when class cannot be instantiated
	 */
	public static Class getClass(String aClassNameProp, String aDefault) throws PushletException {
		String clazz = (aDefault == null ? getProperty(aClassNameProp) : getProperty(
				aClassNameProp, aDefault));
		try {
			return Class.forName(clazz);
		}
		catch (ClassNotFoundException t) {
			throw new PushletException("Cannot find class for " + aClassNameProp + "=" + clazz, t);
		}
	}

	/**
	 * 通过Properties配置文件初始化事件源
	 */
	public static void load(String aDirPath) {
		try {
			Log.info("Config: loading " + PROPERTIES_FILE + " from classpath");
			properties = Sys.loadPropertiesResource(PROPERTIES_FILE);
		}
		catch (Throwable t) {
			String filePath = aDirPath + File.separator + PROPERTIES_FILE;
			Log.info("Config: cannot load " + PROPERTIES_FILE + " from classpath, will try from "
					+ filePath);
			try {
				properties = Sys.loadPropertiesFile(filePath);
			}
			catch (Throwable t2) {
				Log.fatal("Config: cannot load properties file from " + filePath, t);
				// Give up
				return;
			}
		}
		Log.info("Config: loaded values=" + properties);
	}

	public static String getProperty(String name, String defaultValue) {
		return properties.getProperty(name, defaultValue);
	}

	public static String getProperty(String name) {
		String value = properties.getProperty(name);
		if (value == null) {
			throw new IllegalArgumentException("Unknown property: " + name);
		}
		return value;
	}

	public static boolean getBoolProperty(String name) {
		String value = getProperty(name);
		try {
			return value.equals("true");
		}
		catch (Throwable t) {
			throw new IllegalArgumentException("Illegal property value: " + name + " val=" + value);
		}
	}

	public static int getIntProperty(String name) {
		String value = getProperty(name);
		try {
			return Integer.parseInt(value);
		}
		catch (Throwable t) {
			throw new IllegalArgumentException("Illegal property value: " + name + " val=" + value);
		}
	}

	public static long getLongProperty(String name) {
		String value = getProperty(name);
		try {
			return Long.parseLong(value);
		}
		catch (Throwable t) {
			throw new IllegalArgumentException("Illegal property value: " + name + " val=" + value);
		}
	}

	public static boolean hasProperty(String name) {
		return properties.containsKey(name);
	}

}

