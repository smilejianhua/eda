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
 * Represents single subject subscription
 * 
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: Subscription.java,v 1.5 2007/11/23 14:33:07 justb Exp $
 */
public class Subscription implements ConfigDefs {
	public static final int ID_SIZE = 5;
	public static final String SUBJECT_SEPARATOR = ",";
	private String id = Rand.randomName(ID_SIZE);
	private String subject;
	private String[] subjects;

	/**
	 * Optional label, a user supplied token.
	 */
	private String label;

	/**
	 * Protected constructor as we create through factory method.
	 */
	protected Subscription() {
	}

	/**
	 * Create instance through factory method.
	 * 
	 * @param aSubject
	 *            the subject (topic).
	 * @return a Subscription object (or derived)
	 * @throws com.wondersgroup.eda.util.PushletException
	 *             exception, usually misconfiguration
	 */
	public static Subscription create(String aSubject) throws PushletException {
		return create(aSubject, null);
	}

	/**
	 * Create instance through factory method.
	 * 
	 * @param aSubject
	 *            the subject (topic).
	 * @param aLabel
	 *            the subject label (optional).
	 * @return a Subscription object (or derived)
	 * @throws com.wondersgroup.eda.util.PushletException
	 *             exception, usually misconfiguration
	 */
	public static Subscription create(String aSubject, String aLabel) throws PushletException {
		if (aSubject == null || aSubject.length() == 0) {
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
		// Init
		subscription.subject = aSubject;
		// We may subscribe to multiple subjects by separating
		// them with SUBJECT_SEPARATOR, e.g. "/stocks/aex,/system/memory,..").
		subscription.subjects = aSubject.split(SUBJECT_SEPARATOR);
		subscription.label = aLabel;
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
		// Silly case but check anyway
		if (eventSubject == null || eventSubject.length() == 0) {
			return false;
		}
		// Test if one of the subjects matches
		for (int i = 0; i < subjects.length; i++) {
			if (eventSubject.startsWith(subjects[i])) {
				return true;
			}
		}
		// No match
		return false;
	}
}
