package bgu.spl.a2;

import java.util.HashMap;
import java.util.List;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */

	private HashMap<String,PrivateState> privateStates;
	private HashMap<String,ActionQueue> queues;
	private List<Thread> threads;
	private VersionMonitor monitor;

	public ActorThreadPool(int nthreads) {
		privateStates = new HashMap<>();
		queues = new HashMap<>();
		monitor = new VersionMonitor();
		initializeThreads(nthreads);
	}

	private void initializeThreads(int nthreads){
		for(int i=0;i<nthreads;i++){
			int finalI = i;
			threads.add(new Thread(() -> {

				while (!Thread.currentThread().isInterrupted()){
						int version = monitor.getVersion();
				        for(ActionQueue currQueue : queues.values()){
				        	if(!currQueue.isEmpty()) {
								if (currQueue.getLock().tryLock()) {
									try {
										if (!currQueue.isEmpty()) {
											currQueue.dequeue().handle(this, currQueue.getActorId(), privateStates.get(currQueue.getActorId()));
										}
									}
									catch (InterruptedException e) {
											break;
									}
									finally{
										currQueue.getLock().unlock();
									}
									}
								}
							}
                    try {
                        monitor.await(version);
                    } catch (InterruptedException e) {
                        break;
                    }

                }
			}));
		}
	}


	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		if(action==null | actorState==null | actorId.equals(""))
			throw new NullPointerException("Input is null, submission failed.");

		if(!queues.containsKey(actorId)){
			queues.put(actorId,new ActionQueue(actorId));
			queues.get(actorId).enqueue(action);
			privateStates.put(actorId,actorState);
		}
		else{
			queues.get(actorId).enqueue(action);
		}
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		if(Thread.currentThread().isInterrupted())
		    throw new InterruptedException("current thread is interrupted, shutdown failed");

        System.out.println("Shutting down pool");

        for(Thread thread: this.threads)
        	if(thread!=Thread.currentThread())
            	thread.interrupt();

        System.out.println("Waiting for all threads to stop");
        boolean alive = true;
        while(alive){
            for(Thread thread: this.threads)
                if(thread.isAlive() && thread!=Thread.currentThread()){
                    Thread.currentThread().sleep(1000);
                    break;
                }
                else
                    alive=false;
        }

        System.out.println("Shutdown complete");

	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for(Thread thread: this.threads)
		    thread.start();
	}

}
