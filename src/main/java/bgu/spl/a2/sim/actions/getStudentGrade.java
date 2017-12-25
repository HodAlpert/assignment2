package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.HashMap;

public class getStudentGrade extends Action<HashMap<String, Integer>> {

    /**
     * returning a copy of student's grades list
     */
    public getStudentGrade(){
        setActionName("get Student Grade");
    }

    /**
     * should return a copy of it's grade list
     */
    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        HashMap<String, Integer> grades = new HashMap<>();
        state.getGrades().forEach(grades::put);
        complete(grades);
    }
}
