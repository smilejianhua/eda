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

import com.wondersgroup.eda.util.Rand;
import com.wondersgroup.eda.util.PushletException;

/**
 * 描绘单个对象的表述
 * 
 * @author Jacky.Li
 */
public class Subscription implements ConfigDefs {
	
	public static final int ID_SIZE = 5;
	public static final String SUBJECT_SEPARATOR = ",";
	private String id = Rand.randomName(ID_SIZE);
	private String subject;
	private String[] subjects;

	/**
	 * 可选标签, 用户提供的令牌
	 */
	private String label;

	protected Subscription() {
	}

	/**
	 * 通过Factory模式来创建实例
	 * 
	 * @param subject
	 *            the subject (topic).
	 * @return a Subscription object (or derived)
	 * @throws com.wondersgroup.eda.util.PushletException
	 *             exception, usually misconfiguration
	 */
	public static Subscription create(String subject) throws PushletException {
		return create(subject, null);
	}

	/**
	 * 通过Factory模式来创建实例
	 * 
	 * @param subject
	 *            the subject (topic).
	 * @param label
	 *            the subject label (optional).
	 * @return a Subscription object (or derived)
	 * @throws com.wondersgroup.eda.util.PushletException
	 *             exception, usually misconfiguration
	 */
	public static Subscription create(String subject, String label) throws PushletException {
		if (subject == null || subject.length() == 0) {
			throw new IllegalArgumentException("Null or emtpy subject");
		}
		Subscription subscription;
		try {
			subscription = (Subscription) Config.getClass(SUBSCRIPTION_CLASS,
					"com.wondersgroup.eda.core.Subscription").newInstance();
		}
		catch (Throwable t) {
			throw new PushletException("Cannot instantiate Subscriber from config", t);
		}
		subscription.subject = subject;
		subscription.subjects = subject.split(SUBJECT_SEPARATOR);
		subscription.label = label;
		return subscription;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * 一旦事件符合相应的描述，监测出来。
	 */
	public boolean match(Event event) {
		String eventSubject = event.getSubject();
		if (eventSubject == null || eventSubject.length() == 0) {
			return false;
		}
		for (int i = 0; i < subjects.length; i++) {
			if (eventSubject.startsWith(subjects[i])) {
				return true;
			}
		}
		return false;
	}
}
