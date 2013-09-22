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
package com.wondersgroup.eda.client;

import com.wondersgroup.eda.core.Event;
import com.wondersgroup.eda.core.Protocol;

/**
 * 提供给PushletClient对象的监听器接口
 * 
 * @author Jacky.Li
 */
public interface PushletClientListener extends Protocol {
	
	public void onAbort(Event theEvent);

	public void onData(Event theEvent);

	public void onHeartbeat(Event theEvent);

	public void onError(String message);
}