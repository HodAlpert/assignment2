package bgu.spl.a2;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ActionQueue extends ConcurrentLinkedQueue<Action> {


    /**
     * A Queue that contains the Actions of an Actor
     * it uses @{@link ReentrantLock} to maintain a thread-safe environment
     *
     * @param lock
     *          lock the queue once a thread is iterating through the queue
     * @param actorId
     *          represent the id of the actor that owns this queue
     */
    private Lock lock;
    private String actorId;

    public ActionQueue(String actorId) {
        super();
        this.lock = new ReentrantLock();
        this.actorId=actorId;
    }

    public Lock getLock(){
        return this.lock;
    }
    public String getActorId(){ return this.actorId; }

}
