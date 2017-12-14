package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class SelfCloseCourse extends Action<Boolean> {
    String Course;
    public SelfCloseCourse(String Course){
        setActionName("Self Close Course");
        this.Course=Course;
    }

    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        if(state.getRegistered()>0) {
            state.setAvailableSpots(-1);//rejecting future registration requests
            state.setRegistered(0);
            List<Action<Boolean>> actions = new ArrayList<>();
            for (String student : state.getRegStudents()) {//asking all students to remove themselfs from course
                Action<Boolean> action = new SelfUnregisterStudent(Course);//asking student to remove self from course
                actions.add(action);
            }//for
            then(actions, () -> {
                complete(true);
            });
            state.setRegStudents(new ArrayList<String>());//erasing registered students
            state.setPrerequisites(new ArrayList<String>());//erasing prerequisites
            for (String student : state.getRegStudents()) {//asking all students to remove themselfs from course
                for (Action action : actions)
                    sendMessage(action, student, new StudentPrivateState());//sending the action to the pool
            }//for
        }
        else {
            state.setAvailableSpots(-1);//rejecting future registration requests
            complete(true);

        }
    }
}

