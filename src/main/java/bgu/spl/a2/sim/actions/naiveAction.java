package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;

public class naiveAction extends Action<Boolean> {
    @Override
    protected void start() {
        complete(true);
    }

    @Override
    protected void computeResult() {
    }
}
