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
 * EDA框架协议的参数定义
 */
public interface Protocol {

	/**
	 * 默认URI
	 */
	public static final String DEFAULT_SERVLET_URI = "/pushlet/pushlet.srv";

	/**
	 * 事件类型 (join, leave, data, subscribe等等) 
	 */
	public static final String P_EVENT = "p_event";

	/**
	 * 起始时间（1970）
	 */
	public static final String P_TIME = "p_time";

	/**
	 * 事件序列数, 每个客户端的数量
	 */
	public static final String P_SEQ = "p_seq";

	/**
	 * 数据事件对象（TOPIC）
	 */
	public static final String P_SUBJECT = "p_subject";

	/**
	 * 事件起源
	 */
	public static final String P_FROM = "p_from";

	/**
	 * 事件地址
	 */
	public static final String P_TO = "p_to";

	/**
	 * Identifier for client instance within server.
	 */
	public static final String P_ID = "p_id";

	/**
	 * Subscription id, identifies single subscription.
	 */
	public static final String P_SUBSCRIPTION_ID = "p_sid";

	/**
	 * Format to receive events
	 */
	public static final String P_FORMAT = "p_format";

	/**
	 * Protocol mode.
	 */
	public static final String P_MODE = "p_mode";

	/**
	 * Reason for errors.
	 */
	public static final String P_REASON = "p_reason";

	/**
	 * URL attribute.
	 */
	public static final String P_URL = "p_url";

	/**
	 * Wait attribute.
	 */
	public static final String P_WAIT = "p_wait";

	/**
	 * Subscription label, may be used to return user-specific
	 * token with a data event, e.g. the name of a function for a callback.
	 */
	public static final String P_SUBSCRIPTION_LABEL = "p_label";

	/**
	 * 客户端-->服务器端的请求
	 */
	public static final String E_JOIN = "join";

	/**
	 * S-->C Acknowledgement of join.
	 */
	public static final String E_JOIN_ACK = "join-ack";

	/**
	 * C-->S Request to join server.
	 */
	public static final String E_JOIN_LISTEN = "join-listen";

	/**
	 * S-->C Acknowledgement of join.
	 */
	public static final String E_JOIN_LISTEN_ACK = "join-listen-ack";

	/**
	 * C-->S Client starts listening.
	 */
	public static final String E_LISTEN = "listen";

	/**
	 * S-->C Ack of listen.
	 */
	public static final String E_LISTEN_ACK = "listen-ack";

	/**
	 * C-->S Client leaves server.
	 */
	public static final String E_LEAVE = "leave";

	/**
	 * S-->C Ack of leave.
	 */
	public static final String E_LEAVE_ACK = "leave-ack";

	/**
	 * C-->S Publish to subject.
	 */
	public static final String E_PUBLISH = "publish";

	/**
	 * S-->C Publish to subject acknowledge.
	 */
	public static final String E_PUBLISH_ACK = "publish-ack";

	/**
	 * C-->S Subscribe to subject request.
	 */
	public static final String E_SUBSCRIBE = "subscribe";

	/**
	 * S-->C Subscribe to subject acknowledge.
	 */
	public static final String E_SUBSCRIBE_ACK = "subscribe-ack";

	/**
	 * C-->S Unsubscribe from subject request.
	 */
	public static final String E_UNSUBSCRIBE = "unsubscribe";

	/**
	 * S--C Unsubscribe from subject acknowledge.
	 */
	public static final String E_UNSUBSCRIBE_ACK = "unsubscribe-ack";

	/**
	 * S-->C Client error response, transitional error.
	 */
	public static final String E_NACK = "nack";

	/**
	 * S-->C Client should abort, permanent error.
	 */
	public static final String E_ABORT = "abort";

	/**
	 * S-->C Data.
	 */
	public static final String E_DATA = "data";

	/**
	 * S-->C or C-->S Heartbeat.
	 */
	public static final String E_HEARTBEAT = "hb";

	/**
	 * S-->C S-->C or C-->S Heartbeat confirmed.
	 */
	public static final String E_HEARTBEAT_ACK = "hb-ack";

	/**
	 * S-->C or C-->S client refresh of data channel.
	 */
	public static final String E_REFRESH = "refresh";

	/**
	 * S-->C client should refresh data channel.
	 */
	public static final String E_REFRESH_ACK = "refresh-ack";

	/**
	 * JavaScript callback.
	 */
	public static String FORMAT_JAVASCRIPT = "js";

	/**
	 * Java serialized object.
	 */
	public static String FORMAT_SERIALIZED_JAVA_OBJECT = "ser";

	/**
	 * Stream of XML documents.
	 */
	public static String FORMAT_XML = "xml";

	/**
	 * Single XML document containing zero or more events.
	 */
	public static String FORMAT_XML_STRICT = "xml-strict";

	//
	// Values for P_MODE parameter
	//
	public static final String MODE_STREAM = "stream";
	public static final String MODE_PULL = "pull";
	public static final String MODE_POLL = "poll";

	//
	// Values for special/reserved subjects
	// TODO: use these to publish events when clients do these actions
	// TODO: Dispatcher may intercept these subjects to send cached events
	//
	public static final String SUBJECT_META = "/meta";
	public static final String SUBJECT_META_SUBS = SUBJECT_META + "/subs";
	public static final String SUBJECT_META_JOINS = SUBJECT_META + "/joins";
}

