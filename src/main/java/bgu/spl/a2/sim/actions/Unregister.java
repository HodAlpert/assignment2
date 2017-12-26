package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class Unregister extends Action<Boolean> {
    private String Student;
    private String Course;

    /**
     * @param Student to unregister
     * @param Course to unregister the student from
     */
    public Unregister(String Student, String Course){
        setActionName("Unregister");
        this.Course=Course;
        this.Student=Student;

    }

    /**
     * if student is registered to course:
     * remove it and do:
     *      getAvailableSpots++
     *      getRegistered--
     *      send the student a self unregister action
     *
     */
    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        if (state.getRegStudents().contains(Student)){//if student is registered to course
            state.setAvailableSpots(state.getAvailableSpots()+1);//inc AvailableSpots
            state.setRegistered(state.getRegistered()-1);//reduce num of registered students.
            state.getRegStudents().remove(Student); // remove student from course's list
            List<Action<Boolean>> actions = new ArrayList<>();
            Action<Boolean> action = new SelfUnregisterStudent(Course);//asking student to remove self from course
            actions.add(action);
            then(actions,()->{//setting the continuation
                complete(true);
            });
            sendMessage(action,Student,new StudentPrivateState());//sending the action to the pool

        }//if
        else if(state.getLogger().contains(Student)) // if the student is in the middle of registration
            state.getLogger().remove(Student);
        else // if student is not registered to Course
            complete(false);
    }

}
