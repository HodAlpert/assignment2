package bgu.spl.a2;

import sun.misc.Queue;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ActionQueue extends Queue<Action>{

    private Lock lock;

    public ActionQueue() {
        super();
        this.lock = new ReentrantLock();
    }

    public Lock getLock(){
        return this.lock;
    }


}
