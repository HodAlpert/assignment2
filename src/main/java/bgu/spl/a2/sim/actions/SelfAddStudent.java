package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class SelfAddStudent extends Action<Boolean> {


    public SelfAddStudent(String student){
        this.setActionName("Self Add Student");
    }

    @Override
    protected void start() {
        complete(true);
    }
}
