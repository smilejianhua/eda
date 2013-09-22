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
	 * Put item in queue; waits() indefinitely if queue is full.
	 */
	public synchronized boolean enQueue(Event item) throws InterruptedException {
		return enQueue(item, -1);
	}

	/**
	 * Put item in queue; if full wait maxtime.
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
		// Put item in queue
		queue[rear] = item;
		rear = next(rear);
		// Wake up waiters; NOTE: first waiter will eat item
		notifyAll();
		return true;
	}

	/**
	 * Get head; if empty wait until something in queue.
	 */
	public synchronized Event deQueue() throws InterruptedException {
		return deQueue(-1);
	}

	/**
	 * Get head; if empty wait for specified time at max.
	 */
	public synchronized Event deQueue(long maxWaitTime) throws InterruptedException {
		while (isEmpty()) {
			if (maxWaitTime >= 0) {
				wait(maxWaitTime);
				// Timed out or woken; if still empty we
				// had bad luck and return failure.
				if (isEmpty()) {
					return null;
				}
			}
			else {
				// Wait indefinitely for something in queue.
				wait();
			}
		}
		// Dequeue item
		Event result = fetchNext();
		// Notify possible wait()-ing enQueue()-ers
		notifyAll();
		// Return dequeued item
		return result;
	}

	/**
	 * Get all queued Events.
	 */
	public synchronized Event[] deQueueAll(long maxWaitTime) throws InterruptedException {
		while (isEmpty()) {
			if (maxWaitTime >= 0) {
				wait(maxWaitTime);
				// Timed out or woken; if still empty we
				// had bad luck and return failure.
				if (isEmpty()) {
					return null;
				}
			}
			else {
				// Wait indefinitely for something in queue.
				wait();
			}
		}

		// Dequeue all items item
		Event[] events = new Event[getSize()];
		for (int i = 0; i < events.length; i++) {
			events[i] = fetchNext();
		}

		// Notify possible wait()-ing enQueue()-ers
		notifyAll();

		// Return dequeued item
		return events;
	}

	public synchronized int getSize() {
		return (rear >= front) ? (rear - front) : (capacity - front + rear);
	}

	/**
	 * Is the queue empty ?
	 */
	public synchronized boolean isEmpty() {
		return front == rear;
	}

	/**
	 * Is the queue full ?
	 */
	public synchronized boolean isFull() {
		return (next(rear) == front);
	}

	/**
	 * Circular counter.
	 */
	private int next(int index) {
		return (index + 1 < capacity ? index + 1 : 0);
	}

	/**
	 * Circular counter.
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
