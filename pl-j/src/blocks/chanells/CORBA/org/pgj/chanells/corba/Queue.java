package org.pgj.chanells.corba;

/**
 * @author Laszlo Hornyak
 * Hopefully I can replace this with someone`s else`s FIFO implementation.
 */
public class Queue {

	/** The Objects in the queue. */
	volatile Object[] que = null;
	/** The last element of the queue */
	volatile int last = 0;

	public final static int DEFAULT_SIZE = 100;

	public Queue() {
		this(DEFAULT_SIZE);
	}

	public Queue(int maxsize) {
		que = new Object[maxsize + 1];
	}

	public void put(Object o) throws QueueException {
		if (last < que.length - 1) {
			que[last] = o;
			last++;
			System.out.println(o + " placed to " + last);
		} else {
			throw new QueueException("Queue is full.");
		}
	}

	public Object get() throws QueueException {

		if (last <= 0)
			throw new QueueException("Queue is empty");

		Object o = que[0];
		System.out.println(o + " returned");
		for (int i = 0; i < last; i++) {
			que[i] = que[i + 1];
		}
		last--;

		return o;

	}

	public boolean isEmpty() {
		return last == 0;
	}

}
