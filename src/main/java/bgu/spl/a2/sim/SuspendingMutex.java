package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private Computer computer;
	private ConcurrentLinkedQueue<Promise<Computer>> queue;
	private AtomicBoolean islocked;

	/**
	 * Constructor
	 *
	 * @param computer in which the mutex keep thread safe
	 */
	public SuspendingMutex(Computer computer) {
		this.computer = computer;
		this.queue = new ConcurrentLinkedQueue<>();
		this.islocked= new AtomicBoolean(false);
	}

	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down() {
		Promise<Computer> promise = new Promise<>();
		if (islocked.compareAndSet(false, true))//if the mutex in not locked
			promise.resolve(computer);//resolving the promise
		else
			queue.add(promise);
		return promise;
	}

	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up() {
		if (islocked.compareAndSet(true, false)) {
			while (!queue.isEmpty()) {
				queue.poll().resolve(computer);
			}
		}
	}
}

