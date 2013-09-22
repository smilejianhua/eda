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
package com.wondersgroup.eda.util;

/**
 * Generic exception wrapper.
 *
 * @author Just van den Broecke
 */
public class PushletException extends Exception {

	private PushletException() {
	}

	public PushletException(String aMessage, Throwable t) {
		super(aMessage + "\n embedded exception=" + t.toString());
	}

	public PushletException(String aMessage) {
		super(aMessage);
	}

	public PushletException(Throwable t) {
		this("PushletException: ", t);
	}

	public String toString() {
		return "PushletException: " + getMessage();
	}
}