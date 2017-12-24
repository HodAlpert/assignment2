package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

public class AcceptToCourse extends Action<String[]> {
    /**
     * An action that adds a given course and it's grade to the student
     * if he meets all of the course's prerequisites
     */
    private List<String> prequisites;
    private String Course;
    private String Grade;

    public AcceptToCourse(String Course, String Grade, List<String> prequisites){
        this.prequisites=prequisites;
        this.Course=Course;
        this.Grade=Grade;
        this.setActionName("Accept To Course");
    }

    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        String[] result={Course,"-"};
        //if all prerequisites are OK
        boolean flag=true;
        for(int i=0;i<prequisites.size() & flag;i++){//if student meets all prerequisites
            if (!state.getGrades().containsKey(prequisites.get(i)))
                flag=false;
        }
        if(flag){//if all prerequisites are OK
            result[1]=Grade;
            if(!Grade.equals("-")) {
               state.getGrades().put(Course, Integer.parseInt(Grade));
           }
           else//if student should have no grade
               state.getGrades().put(Course, null);
        }
        else
            result[0]="-";
        complete(result);
    }
}
