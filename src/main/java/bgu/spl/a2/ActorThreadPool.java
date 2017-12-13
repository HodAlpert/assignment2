package bgu.spl.a2;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

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
	 * @param privateStates
	 *            A map of all the actors' private state, @actorId is the key.
	 * @param queues
	 *            A map of all the actors' queues, @actorId is the key.
	 *            each queue is of type @{@link ActionQueue} and contains @{@link Action}
	 *            to be executed by the threads
	 * @param threads
	 *            A list of all threads in the pool,
	 *            the threads are defined by {@link #initializeThreads(int)} method
	 * @param ShutDownLatch
	 *            A {@link CountDownLatch} that is initialized with @nthreads
	 *            and use @countDown every time a thread from the pool has terminated.
	 *            ShutDownLatch is used in {@link #shutdown()} to make sure all threads are terminated before
	 *            the method returns.
	 * @param startLatch
	 *            A {@link CountDownLatch} that is initialized with @nthreads
	 *            and use @countDown every time a thread from the pool has started.
	 *            startLatch is used in {@link #start()} to make sure all threads have started before
	 *            the method returns.
	 */

	private ConcurrentHashMap<String,PrivateState> privateStates;
	ConcurrentHashMap<String,ActionQueue> queues;
	protected List<Thread> threads;
	private CountDownLatch startLatch, ShutDownLatch;
	private VersionMonitor monitor;

	public ActorThreadPool(int nthreads) {
		privateStates = new ConcurrentHashMap<String,PrivateState>();
		queues = new ConcurrentHashMap<String,ActionQueue>();
		monitor = new VersionMonitor();
		startLatch = new CountDownLatch(nthreads);
		ShutDownLatch = new CountDownLatch(nthreads);
		threads = new LinkedList<Thread>();
		initializeThreads(nthreads);
	}

	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		return privateStates;
	}

	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivaetState(String actorId){
		return privateStates.get(actorId);
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
		try {
			if (!queues.containsKey(actorId)) {
				queues.put(actorId, new ActionQueue(actorId));
				queues.get(actorId).enqueue(action);
				privateStates.put(actorId, actorState);
			} else {
				queues.get(actorId).enqueue(action);
			}
			monitor.inc();
		}
		catch (NullPointerException e){
			System.out.println("Input cannot be null, submission failed!");
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

		for(Thread thread: this.threads) {
			thread.interrupt();

		}

		System.out.println("Waiting for all threads to terminate");
		ShutDownLatch.await();
		System.out.println("Shutdown complete");
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for(Thread thread: this.threads)
			thread.start();
		try {
			startLatch.await();
			System.out.println("All Threads have started");
		} catch (InterruptedException e) {
			System.out.println("Error while starting threads, start failed");
		}
	}
	private void initializeThreads(int nthreads){
		for(int i=0;i<nthreads;i++){
			threads.add(new Thread(() -> {
				startLatch.countDown();
				while (!Thread.currentThread().isInterrupted()){
					int version = monitor.getVersion();
					for(ActionQueue currQueue : queues.values()){
						if (!currQueue.isEmpty() && currQueue.getLock().tryLock()) {
							try {
								if (!currQueue.isEmpty()) { // get an Action from @currQueue if not empty
									currQueue.dequeue().handle(this, currQueue.getActorId(), privateStates.get(currQueue.getActorId()));
									if(!currQueue.isEmpty())
										monitor.inc();
								}//if
							}//try
							catch (InterruptedException e) {
								Thread.currentThread().interrupt(); // if thread is blocked
							}
							finally{
								currQueue.getLock().unlock();
							}
						}//if
					}//for
					try {
						monitor.await(version);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt(); // if thread is blocked
					}

				}//while
				ShutDownLatch.countDown();
			}));
		}
	}


}
