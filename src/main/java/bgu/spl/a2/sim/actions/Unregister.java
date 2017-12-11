package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class Unregister extends Action<Boolean> {
    private String Student;
    private String Course;

    public Unregister(String Student, String Course){
        setActionName("Unregister");
        this.Course=Course;
        this.Student=Student;

    }
    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        //TODO implement flag to reject if close registration was called
        if (state.getRegStudents().contains(Student)){//if student is registered to course
            state.setAvailableSpots(state.getAvailableSpots()+1);//inc AvailableSpots
            List<Action<Boolean>> actions = new ArrayList<>();
            Action<Boolean> action = new SelfUnregisterStudent(Course);//asking student to remove self from course
            sendMessage(action,Student,new StudentPrivateState());//sending the action to the pool
            actions.add(action);
            then(actions,()->{//setting the continuation
                complete(true);
            });
        }//if
        else // if student is not registered to Course
            complete(true);
    }

}
