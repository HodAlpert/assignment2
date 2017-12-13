package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.LinkedList;
import java.util.List;

public class AcceptToCourse extends Action<Boolean> {

    private List<String> prequisites;
    private String Course;
    private String Grade;

    public AcceptToCourse(String Course, String Grade){
        this.prequisites=new LinkedList<>();
        this.Course=Course;
        this.Grade=Grade;
        this.setActionName("Accept To Course");
    }

    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        if(!state.getGrades().containsKey(Course) && state.getGrades().values().containsAll(prequisites)){
           if(!Grade.equals("-"))
              state.getGrades().put(Course,Integer.parseInt(Grade));
            complete(true);
        }
        else
            complete(false);
    }
}
