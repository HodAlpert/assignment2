package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class SelfAddStudent extends Action<Boolean> {
    /**
     * an action that sets the student's name
     */
    private  String student;

    public SelfAddStudent(String student){
        this.student=student;
        this.setActionName("Self Add Student");
    }

    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        state.setStudent(this.student); // sets the name of the student
        complete(true);
    }
}
