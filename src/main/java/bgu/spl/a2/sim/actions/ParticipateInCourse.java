package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticipateInCourse extends Action<Boolean> {

    private String[] Grade;
    private String Course;
    private String Student;


    public ParticipateInCourse(String Student, String Course, String[] Grade){
        this.Grade=Grade;
        this.Student=Student;
        this.Course=Course;
        this.setActionName("Participate In Course");
    }

    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        if (state.getAvailableSpots() != -1 && state.getAvailableSpots() < state.getRegistered()) { // no need to check if grade >=56
            Action<Boolean> acceptToCourse = new AcceptToCourse(this.Course, this.Grade[0]);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(acceptToCourse);
            sendMessage(acceptToCourse, Student, new StudentPrivateState());
            then(actions, () -> {
                if (acceptToCourse.getResult().get()) {
                    state.setRegistered(state.getRegistered() + 1);
                    state.getRegStudents().add(Student);
                    this.complete(true);
                } else
                    this.complete(false);
            });

        } else
            this.complete(false);
    }
}
