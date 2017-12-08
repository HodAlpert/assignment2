package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;

public class naiveAction extends Action<Boolean> {
    @Override
    protected void start() {
        complete(true);
        System.out.println("action "+ this.hashCode()+": completed by thread"+ Thread.currentThread().getName());
    }

    @Override
    protected void computeResult() {
    }
}
