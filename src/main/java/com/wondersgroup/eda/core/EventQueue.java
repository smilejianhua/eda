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
 * 先进先出（FIFO）队列，FIFO 队列类通过循环数组来实现
 * 
 * enQueue() 和 deQueue() 方法通过读/写方式进行启动和暂停、
 * 实现并复写了 java.lang.Object.wait()/notify() 方法
 *
 * @author Jacky.Li
 */
public class EventQueue {
	
	/**
	 * 定义最大队列长度
	 */
	private int capacity = 8;
	private Event[] queue = null;
	private int front, rear;

	/**
	 * 默认构造器
	 */
	public EventQueue() {
		this(8);
	}

	/**
	 * 通过特殊长度能力进行构造
	 */
	public EventQueue(int capacity) {
		this.capacity = capacity;
		queue = new Event[capacity];
		front = rear = 0;
	}

	/**
	 * 将对象放入队列
	 */
	public synchronized boolean enQueue(Event item) throws InterruptedException {
		return enQueue(item, -1);
	}

	/**
	 * 如果超过最长等待时间，将对象放入队列
	 */
	public synchronized boolean enQueue(Event item, long maxWaitTime) throws InterruptedException {
		while (isFull()) {
			if (maxWaitTime > 0) {
				wait(maxWaitTime);
				if (isFull()) {
					return false;
				}
			}
			else {
				wait();
			}
		}
		queue[rear] = item;
		rear = next(rear);
		notifyAll();
		return true;
	}

	public synchronized Event deQueue() throws InterruptedException {
		return deQueue(-1);
	}

	public synchronized Event deQueue(long maxWaitTime) throws InterruptedException {
		while (isEmpty()) {
			if (maxWaitTime >= 0) {
				wait(maxWaitTime);
				if (isEmpty()) {
					return null;
				}
			}
			else {
				wait();
			}
		}
		Event result = fetchNext();
		notifyAll();
		return result;
	}

	/**
	 * 获得所有队列中的事件
	 */
	public synchronized Event[] deQueueAll(long maxWaitTime) throws InterruptedException {
		while (isEmpty()) {
			if (maxWaitTime >= 0) {
				wait(maxWaitTime);
				if (isEmpty()) {
					return null;
				}
			}
			else {
				wait();
			}
		}
		Event[] events = new Event[getSize()];
		for (int i = 0; i < events.length; i++) {
			events[i] = fetchNext();
		}
		notifyAll();
		return events;
	}

	public synchronized int getSize() {
		return (rear >= front) ? (rear - front) : (capacity - front + rear);
	}

	/**
	 * 判断队列是否为空
	 */
	public synchronized boolean isEmpty() {
		return front == rear;
	}

	/**
	 * 判断队列是否已经满
	 */
	public synchronized boolean isFull() {
		return (next(rear) == front);
	}

	/**
	 * 循环计数
	 */
	private int next(int index) {
		return (index + 1 < capacity ? index + 1 : 0);
	}

	/**
	 * 循环计数
	 */
	private Event fetchNext() {
		Event temp = queue[front];
		queue[front] = null;
		front = next(front);
		return temp;
	}

	public static void p(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) {
		EventQueue q = new EventQueue(8);
		Event event = new Event("t");
		try {
			q.enQueue(event);
			p("(1) size = " + q.getSize());
			q.enQueue(event);
			p("(2) size = " + q.getSize());
			q.deQueue();
			p("(1) size = " + q.getSize());
			q.deQueue();
			p("(0) size = " + q.getSize());

			q.enQueue(event);
			q.enQueue(event);
			q.enQueue(event);
			p("(3) size = " + q.getSize());
			q.deQueue();
			p("(2) size = " + q.getSize());
			q.enQueue(event);
			q.enQueue(event);
			q.enQueue(event);
			p("(5) size = " + q.getSize());
			q.enQueue(event);
			q.enQueue(event);
			p("(7) size = " + q.getSize());
			q.deQueue();
			q.deQueue();
			q.deQueue();
			p("(4) size = " + q.getSize());
			q.deQueue();
			q.deQueue();
			q.deQueue();
			;
			q.deQueue();
			p("(0) size = " + q.getSize());

			q.enQueue(event);
			q.enQueue(event);
			q.enQueue(event);
			q.enQueue(event);
			q.enQueue(event);
			p("(5) size = " + q.getSize());

			q.deQueue();
			q.deQueue();
			q.deQueue();
			;
			q.deQueue();
			p("(1) size = " + q.getSize());
		}
		catch (InterruptedException ie) {
		}
	}
}
