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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ClientAdapter 以XML格式发布事件
 *
 * @author Jacky.Li
 */
class XMLAdapter implements ClientAdapter {
	
	// public static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
	private String contentType = "text/plain;charset=UTF-8";
	private ServletOutputStream out = null;
	private HttpServletResponse servletResponse;
	private boolean strictXML;

	/**
	 * 初始化
	 */
	public XMLAdapter(HttpServletResponse aServletResponse) {
		this(aServletResponse, false);
	}

	public XMLAdapter(HttpServletResponse aServletResponse, boolean useStrictXML) {
		servletResponse = aServletResponse;
		strictXML = useStrictXML;
		if (strictXML) {
			contentType = "text/xml;charset=UTF-8";
		}
	}

	public void start() throws IOException {
		servletResponse.setContentType(contentType);
		out = servletResponse.getOutputStream();
		servletResponse = null;
		if (strictXML) {
			out.print("<pushlet>");
		}
	}

	public void push(Event anEvent) throws IOException {
		debug("event=" + anEvent);
		out.print(anEvent.toXML(strictXML));
		out.flush();
	}

	public void stop() throws IOException {
		if (strictXML) {
			out.print("</pushlet>");
			out.flush();
		}
	}

	private void debug(String s) {
		Log.debug("[XMLAdapter]" + s);
	}
}