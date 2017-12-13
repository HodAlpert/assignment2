package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class SelfAddStudent extends Action<Boolean> {

    private  String student;

    public SelfAddStudent(String student){
        this.student=student;
        this.setActionName("Self Add Student");
    }

    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        System.out.println("STATE in SelfAddStudent: "+state);
        state.setStudent(this.student);
        complete(true);
    }
}
