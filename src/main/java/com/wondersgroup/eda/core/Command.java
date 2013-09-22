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
import com.wondersgroup.eda.util.Servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 包装request/response数据.
 * 
 * @author Jacky.Li
 */
public class Command implements Protocol {

	/**
	 * 请求事件
	 */
	public final Event requestEvent;

	/**
	 * 响应事件
	 */
	private Event responseEvent;

	/**
	 * HTTP Servlet GET/POST 请求
	 */
	public final HttpServletRequest httpReq;

	/**
	 * HTTP Servlet GET/POST 响应
	 */
	public final HttpServletResponse httpRsp;

	public final Session session;

	/**
	 * 响应客户端适配器
	 */
	private ClientAdapter clientAdapter;

	private Command(Session aSession, Event aRequestEvent, HttpServletRequest aHTTPReq,
			HttpServletResponse aHTTPRsp) {
		session = aSession;
		requestEvent = aRequestEvent;
		httpReq = aHTTPReq;
		httpRsp = aHTTPRsp;
	}

	/**
	 * 创建一个新的命令对象
	 */
	public static Command create(Session aSession, Event aReqEvent, HttpServletRequest aHTTPReq,
			HttpServletResponse aHTTPRsp) {
		return new Command(aSession, aReqEvent, aHTTPReq, aHTTPRsp);
	}

	/**
	 * 设置响应事件
	 */
	public void setResponseEvent(Event aResponseEvent) {
		responseEvent = aResponseEvent;
	}

	/**
	 * 获得相应事件
	 */
	public Event getResponseEvent() {
		return responseEvent;
	}

	/**
	 * 从请求中获取相应的客户端适配器
	 */
	public ClientAdapter getClientAdapter() throws PushletException {
		if (clientAdapter == null) {
			clientAdapter = createClientAdapter();
		}
		return clientAdapter;
	}

	protected ClientAdapter createClientAdapter() throws PushletException {
		String outputFormat = session.getFormat();
		if (outputFormat.equals(FORMAT_JAVASCRIPT)) {
			return new BrowserAdapter(httpRsp);
		}
		else if (outputFormat.equals(FORMAT_SERIALIZED_JAVA_OBJECT)) {
			return new SerializedAdapter(httpRsp);
		}
		else if (outputFormat.equals(FORMAT_XML)) {
			return new XMLAdapter(httpRsp);
		}
		else if (outputFormat.equals(FORMAT_XML_STRICT)) {
			return new XMLAdapter(httpRsp, true);
		}
		else {
			throw new PushletException("Null or invalid output format: " + outputFormat);
		}
	}

	protected void sendResponseHeaders() {
		Servlets.setNoCacheHeaders(httpRsp);
		if (session.getUserAgent().indexOf("java") > 0) {
			httpRsp.setHeader("Connection", "close");
		}
	}
}
