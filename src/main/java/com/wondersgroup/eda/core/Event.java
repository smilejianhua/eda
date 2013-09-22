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

import com.wondersgroup.eda.util.Sys;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 描述事件数据
 *
 * @author Jacky.Li
 */
public class Event implements Protocol, Serializable {

	protected Map attributes = new HashMap(3);

	public Event(String anEventType) {
		this(anEventType, null);
	}

	public Event(String anEventType, Map theAttributes) {
		if (theAttributes != null) {
			setAttrs(theAttributes);
		}
		setField(P_EVENT, anEventType);
		setField(P_TIME, System.currentTimeMillis() / 1000);
	}

	public Event(Map theAttributes) {
		if (!theAttributes.containsKey(P_EVENT)) {
			throw new IllegalArgumentException(P_EVENT + " not found in attributes");
		}
		setAttrs(theAttributes);
	}

	public static Event createDataEvent(String aSubject) {
		return createDataEvent(aSubject, null);
	}

	public static Event createDataEvent(String aSubject, Map theAttributes) {
		Event dataEvent = new Event(E_DATA, theAttributes);
		dataEvent.setField(P_SUBJECT, aSubject);
		return dataEvent;
	}

	public String getEventType() {
		return getField(P_EVENT);
	}

	public String getSubject() {
		return getField(P_SUBJECT);
	}

	public void setField(String name, String value) {
		attributes.put(name, value);
	}

	public void setField(String name, int value) {
		attributes.put(name, value + "");
	}

	public void setField(String name, long value) {
		attributes.put(name, value + "");
	}

	public String getField(String name) {
		return (String) attributes.get(name);
	}

	/**
	 * Return field; if null return default.
	 */
	public String getField(String name, String aDefault) {
		String result = getField(name);
		return result == null ? aDefault : result;
	}

	public Iterator getFieldNames() {
		return attributes.keySet().iterator();
	}

	public String toString() {
		return attributes.toString();
	}

	/**
	 * 转换HTTP查询字符串
	 */
	public String toQueryString() {
		String queryString = "";
		String amp = "";
		for (Iterator iter = getFieldNames(); iter.hasNext();) {
			String nextAttrName = (String) iter.next();
			String nextAttrValue = toUTF8(getField(nextAttrName));
//			nextAttrValue = toUTF8(nextAttrValue);  
			queryString = queryString + amp + nextAttrName + "=" + nextAttrValue;
			// 在第一个串后面加"&".
			amp = "&";
		}
		return queryString;
	}

	public String toXML(boolean strict) {
		String xmlString = "<event ";
		for (Iterator iter = getFieldNames(); iter.hasNext();) {
			String nextAttrName = (String) iter.next();
			String nextAttrValue = getField(nextAttrName);
			xmlString = xmlString + nextAttrName + "=\""
					+ (strict ? Sys.forHTMLTag(nextAttrValue) : nextAttrValue) + "\" ";
		}
		xmlString += "/>";
		return xmlString;
	}

	public String toXML() {
		return toXML(false);
	}

	public Object clone() {
		return new Event(attributes);
	}

	private void setAttrs(Map theAttributes) {
		attributes.putAll(theAttributes);
	}

	/** 
	 * "ISO-8859-1"格式字符转换成"UTF-8" 
	 * @param str "ISO-8859-1"格式字符 
	 * @return "UTF-8"格式字符 
	 */  
	public String toUTF8(String str) {
		try {
			str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return str;
	}
}