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
        if (state==null)
            System.out.println( Thread.currentThread().getName()+". Action: "+Action+ "actor state is null");
        state.setStudent(this.student);
        complete(true);
    }
}
