package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class ParticipateInCourse extends Action<Boolean> {

    private String Student; //TODO what should we do with this input?
    private String Course;
    private String Grade;

    public ParticipateInCourse(String student, String course, String grade){
        this.Student=student;
        this.Course=course;
        this.Grade=grade;
        this.setActionName("Participate In Course");
    }

    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        Action<Boolean> acceptToCourse = new AcceptToCourse(state.getGrades(),Long.toString(state.getSignature()));
        List<Action<Boolean>> actions = new ArrayList<>();
        actions.add(acceptToCourse);
        sendMessage(acceptToCourse,Course,new CoursePrivateState());
        then(actions, ()-> {
            if(acceptToCourse.getResult().get()){
                if(!Grade.equals("-"))
                    state.getGrades().put(Course,Integer.parseInt(Grade));
            }
            complete(true);});

    }
}
