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

/**
 * 针对于事件拉方案的事件资源接口定义
 *
 * @author Jacky.Li
 */

public interface EventSource {
	
	/**
	 * 激活事件资源
	 */
	public void activate();

	/**
	 * 钝化事件资源
	 */
	public void passivate();

	/**
	 * 挂起事件资源
	 */
	public void stop();
}

