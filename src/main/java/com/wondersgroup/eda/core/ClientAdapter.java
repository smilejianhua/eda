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

import java.io.IOException;

/**
 * HTTP协议的客户端适配器接口
 * 
 * @author Jacky.Li
 */
public interface ClientAdapter {

	/**
	 * 开始事件推送
	 */
	public void start() throws IOException;

	/**
	 * 将单个事件推送到客户端
	 */
	public void push(Event anEvent) throws IOException;

	/**
	 * 停止事件推送
	 */
	public void stop() throws IOException;
}

