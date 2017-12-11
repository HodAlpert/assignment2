package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.HashMap;

public class AcceptToCourse extends Action<Boolean> {

    private HashMap<String, Integer> grades;
    private String student;


    public AcceptToCourse(HashMap<String, Integer> grades, String student){
        this.grades=grades;
        this.student=student;
        this.setActionName("Accept To Course");
    }

    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        if(state.getAvailableSpots()!=-1 && state.getAvailableSpots()!=state.getRegistered() &&
                state.getPrerequisites().containsAll(grades.keySet())) { // no need to check if grade >=56

            state.setRegistered(state.getRegistered()+1);
            state.getRegStudents().add(student);
            this.complete(true);
        }
        else
            this.complete(false);
    }

}
