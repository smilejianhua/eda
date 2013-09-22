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

import java.io.*;
import java.util.HashMap;

/**
 * 把XML格式Parse成为Event对象。
 * 
 * @author Jacky.Li
 */
public class EventParser {

	private EventParser() {
	}

	/**
	 * 从文件中获得事件对象
	 */
	public static Event parse(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		return parse(br);
	}

	/**
	 * 从输入读取流中获得事件对象
	 */
	public static Event parse(Reader aReader) throws IOException {
		StringBuffer preparsedString = new StringBuffer(24);
		// First find the opening tag ('<')
		char nextChar;
		while ((nextChar = (char) aReader.read()) != '<')
			;
		// Append '<'
		preparsedString.append(nextChar);
		// Then find end-tag ('>'), appending all chars to preparsed string.
		do {
			nextChar = (char) aReader.read();
			preparsedString.append(nextChar);
		}
		while (nextChar != '>');
		return parse(preparsedString.toString());
	}

	/**
	 * 从一个字符串中获得事件对象
	 */
	public static Event parse(String aString) throws IOException {
		aString = aString.trim();
		if (!aString.startsWith("<") || !aString.endsWith("/>")) {
			throw new IOException("No start or end tag found while parsing event [" + aString + "]");
		}
		// Create the attributes object.
		HashMap properties = new HashMap(3);
		aString = aString.substring(1, aString.length() - 2).trim();
		int index = 0;
		while (!Character.isWhitespace(aString.charAt(index)) && (index < aString.length())) {
			index++;
		}
		aString = aString.substring(index).trim();
		index = 0;
		String attrName;
		String attrValue;
		while (index < aString.length()) {
			while ((aString.charAt(index) != '=') && (index < aString.length())) {
				index++;
			}
			// 创建属性名称字符串
			attrName = aString.substring(0, index).trim();
			aString = aString.substring(index + 1).trim();
			index = 1; 
			// 获得属性值
			while ((aString.charAt(index) != '\"') && (index < aString.length())) {
				if (aString.charAt(index) == '\\') {
					aString = aString.substring(0, index) + aString.substring(index + 1);
				}
				index++;
			}
			attrValue = aString.substring(1, index);
			properties.put(attrName, attrValue);
			aString = aString.substring(index + 1).trim();
			index = 0;
		}
		return new Event(properties);
	}

	/**
	 * 测试方法
	 */
//	public static void main(String[] args) {
//		try {
//			Event event = parse(new File(args[0]));
//			System.out.println("OK parsed Event file " + args[0]);
//			System.out.println(event.toXML());
//
//			event = parse(event.toXML());
//			System.out.println("OK parsed Event string");
//			System.out.println(event.toXML());
//		}
//		catch (Throwable t) {
//			System.out.println("Error parsing event file: " + args[0]);
//			t.printStackTrace();
//		}
//	}
}
