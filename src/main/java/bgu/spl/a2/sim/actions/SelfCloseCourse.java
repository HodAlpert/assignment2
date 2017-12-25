package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class SelfCloseCourse extends Action<Boolean> {

    private String Course;

    /**
     * @param Course to be closed
     */
    public SelfCloseCourse(String Course){
        setActionName("Self Close Course");
        this.Course=Course;
    }

    /**
     * first: we will set fet availableSpots to be -1
     * then we will send all students that are already registered to the course
     * a request to unregister themselfs, and when they will all finish- we will return true.
     */
    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        state.setAvailableSpots(-1);//rejecting future registration requests
        if(state.getRegistered()>0) {
            state.setRegistered(0);
            List<Action<Boolean>> actions = new ArrayList<>();
            for (String student : state.getRegStudents()) {//asking all students to remove themselves from course
                Action<Boolean> action = new SelfUnregisterStudent(Course);//asking student to remove self from course
                actions.add(action);
            }//for
            then(actions, () -> complete(true));
            for (int i=0;i<state.getRegStudents().size();i++) {//asking all students to remove themselves from course
                sendMessage(actions.get(i), state.getRegStudents().get(i), new StudentPrivateState());//sending the action to the pool
            }//for
            state.setRegStudents(new ArrayList<String>());//erasing registered students
            state.setPrerequisites(new ArrayList<String>());//erasing prerequisites
        }
        else {
            complete(true);

        }
    }
}

