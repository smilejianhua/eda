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

/**
 * 针对于事件拉方案的事件资源抽象
 *
 * @author Jacky.Li
 */

abstract public class EventPullSource implements EventSource, Runnable {
	private volatile boolean alive = false;
	private volatile boolean active = false;
	private static int threadNum = 0;
	private Thread thread;

	public EventPullSource() {
	}

	abstract protected long getSleepTime();

	abstract protected Event pullEvent();

	public void start() {
		thread = new Thread(this, "EventPullSource-" + (++threadNum));
		thread.setDaemon(true);
		thread.start();
	}

	public boolean isAlive() {
		return alive;
	}

	/**
	 * 停止事件产生的线程
	 */
	public void stop() {
		alive = false;
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * 激活事件生成线程
	 */
	synchronized public void activate() {
		if (active) {
			return;
		}
		active = true;
		if (!alive) {
			start();
			return;
		}
		Log.debug(getClass().getName() + ": notifying...");
		notifyAll();
	}

	/**
	 * 钝化事件生成线程
	 */
	public void passivate() {
		if (!active) {
			return;
		}
		active = false;
	}

	/**
	 * 主要轮询：睡眠，生成，发布事件
	 */
	public void run() {
		Log.debug(getClass().getName() + ": starting...");
		alive = true;
		while (alive) {
			try {
				Thread.sleep(getSleepTime());
				// 睡眠状态，停止轮询
				if (!alive) {
					break;
				}
				synchronized (this) {
					while (!active) {
						Log.debug(getClass().getName() + ": waiting...");
						wait();
					}
				}
			}
			catch (InterruptedException e) {
				break;
			}
			try {
				Event event = pullEvent();
				Dispatcher.getInstance().multicast(event);
			}
			catch (Throwable t) {
				Log.warn("EventPullSource exception while multicasting ", t);
				t.printStackTrace();
			}
		}
		Log.debug(getClass().getName() + ": stopped");
	}
}
