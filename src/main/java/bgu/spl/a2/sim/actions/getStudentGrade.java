package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.HashMap;

public class getStudentGrade extends Action<HashMap<String, Integer>> {

    public getStudentGrade(){
        setActionName("get Student Grade");
    }
    @Override
    //should return it's grade list
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        HashMap<String, Integer> grades = new HashMap<>();
        state.getGrades().forEach(grades::put);
        complete(grades);
    }
}
