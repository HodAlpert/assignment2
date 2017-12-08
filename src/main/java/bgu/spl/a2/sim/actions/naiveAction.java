package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;

public class naiveAction extends Action<Boolean> {
    @Override
    protected void start() {
        System.out.println("starting naive action");
        complete(true);
    }

    @Override
    protected void computeResult() {
    }
}
