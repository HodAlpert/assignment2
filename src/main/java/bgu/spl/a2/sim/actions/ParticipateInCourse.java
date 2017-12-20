package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class ParticipateInCourse extends Action<String[]> {

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
        String[] result={"-","-"};
        if (state.getAvailableSpots() >0 && !state.getRegStudents().contains(Student)) { // no need to check if grade >=56
            Action<String[]> acceptToCourse = new AcceptToCourse(this.Course, this.Grade[0], state.getPrequisites());
            List<Action<String[]>> actions = new ArrayList<>();
            actions.add(acceptToCourse);
            then(actions, () -> {
                    if (!acceptToCourse.getResult().get()[0].equals("-") && state.getAvailableSpots() >0) {
                        state.setRegistered(state.getRegistered() + 1);
                        state.setAvailableSpots(state.getAvailableSpots()-1);
                        state.getRegStudents().add(Student);
                        this.complete(acceptToCourse.getResult().get());
                    }
                    else if (state.getAvailableSpots() <1)
                        this.complete(result);
                    else
                        this.complete(acceptToCourse.getResult().get());

            });
            sendMessage(acceptToCourse, Student, new StudentPrivateState());
        }
        else
            this.complete(result);
    }
}
