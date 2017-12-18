package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.HashMap;

public class getStudentGrade extends Action<HashMap<String, Integer>> {
    @Override
    //should return it's grade list
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        complete(state.getGrades());
    }
}
