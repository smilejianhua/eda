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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * 浏览器客户端对于客户适配器接口的实现。
 * 
 * @author Jacky.Li
 */
public class BrowserAdapter implements ClientAdapter, Protocol {

	public static final String START_DOCUMENT = "<html><head><meta http-equiv=\"Pragma\" content=\"no-cache\"><meta http-equiv=\"Expires\" content=\"Tue, 31 Dec 1997 23:59:59 GMT\"></head>"
			+ "<body>"
			+ "\n<script language=\"JavaScript\"> var url=\" \"; \nfunction refresh() { document.location.href=url; }</script>";
	public static final String END_DOCUMENT = "</body></html>";

	private PrintWriter servletOut;
	private HttpServletResponse servletRsp;
	private int bytesSent;

	public BrowserAdapter(HttpServletResponse aServletResponse) {
		servletRsp = aServletResponse;
	}

	/**
	 * 初始化
	 */
	public void start() throws IOException {
		// HTML类型的内容
		servletRsp.setStatus(HttpServletResponse.SC_OK);
		servletRsp.setContentType("text/html;charset=UTF-8");
		servletOut = servletRsp.getWriter();
		send(START_DOCUMENT);
	}

	/**
	 * 把事件推送到客户端
	 */
	public void push(Event anEvent) throws IOException {
		Log.debug("BCA event=" + anEvent.toXML());
		if (anEvent.getEventType().equals(Protocol.E_REFRESH)) {
			long refreshWaitMillis = Long.parseLong(anEvent.getField(P_WAIT));
			String url = anEvent.getField(P_URL);
			String jsRefreshTrigger = "\n<script language=\"JavaScript\">url=\'" + url
					+ "\';\n setTimeout(\"refresh()\", " + refreshWaitMillis + ");\n</script>";
			send(jsRefreshTrigger + END_DOCUMENT);
		}
		else {
			send(event2JavaScript(anEvent));
		}
	}

	public void stop() {
		servletOut = null;
	}

	/**
	 * 将字符串推送到浏览器
	 */
	protected void send(String s) throws IOException {
		if (servletOut == null) {
			throw new IOException("Client adapter was stopped");
		}
		servletOut.print(s);
		servletOut.flush();
		servletRsp.flushBuffer();
		bytesSent += s.length();
		Log.debug("bytesSent= " + bytesSent);
	}

	/**
	 * 在浏览器中将Java Event转换成Javascript函数
	 */
	protected String event2JavaScript(Event event) throws IOException {
		String jsArgs = "";
		for (Iterator iter = event.getFieldNames(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = event.getField(name);
			String nextArgument = (jsArgs.equals("") ? "" : ",") + "'" + name + "'" + ", \""
					+ value + "\"";
			jsArgs += nextArgument;
		}
		return "<script language=\"JavaScript\">parent.push(" + jsArgs + ");</script>";
	}
}

