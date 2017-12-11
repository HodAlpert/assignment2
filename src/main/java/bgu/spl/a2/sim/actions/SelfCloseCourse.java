package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class SelfCloseCourse extends Action<Boolean> {
    String Course;
    public SelfCloseCourse(String Course){
        setActionName("SelfUnregisterStudent");
        this.Course=Course;
    }

    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        state.setavailableSpots(-1);//rejecting future registration requests
        List<Action<Boolean>> actions = new ArrayList<>();
        for (String student: state.getRegStudents()){//asking all students to remove themselfs from course
            Action<Boolean> action = new SelfUnregisterStudent(Course);//asking student to remove self from course
            sendMessage(action,student,new StudentPrivateState());//sending the action to the pool
            actions.add(action);
        }//for
        then(actions,()->{
            complete(true);
        });
    }
}
