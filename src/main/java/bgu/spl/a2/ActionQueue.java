package bgu.spl.a2;

import sun.misc.Queue;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ActionQueue extends Queue<Action>{

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
