package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class SelfUnregisterStudent extends Action<Boolean> {
    private String Course;

    /**
     * @param Course to be remove if exist
     */
    public SelfUnregisterStudent(String Course){
        setActionName("SelfUnregisterStudent");
        this.Course=Course;
    }

    /**
     * if Course is in getGrades list- remove it
     * return true
     */
    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        if(state.getGrades().containsKey(Course))
            state.getGrades().remove(Course);
        complete(true);
    }

}
