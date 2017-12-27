package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class ParticipateInCourse extends Action<String[]> {
    /**
     * an action that register a given student to the course
     * if there is available space and the student meets all the course's prerequisites
     */
    private String[] Grade;
    private String Course;
    private String Student;

    /**
     * @param Student that needs to participate
     * @param Course that the student needs to register to
     * @param Grade to give the student in case student can register to course
     */
    public ParticipateInCourse(String Student, String Course, String[] Grade){
        this.Grade=Grade;
        this.Student=Student;
        this.Course=Course;
        this.setActionName("Participate In Course");
    }

    /**
     * if there is place and student is not already registered:
     *          we will add the student to regstudentslist and send a an action for the student to register himself if possible
     *          when student will return an answer
     *          if he registered successfully-
     *              we will check if it's possible for him to register and there are still available spots,
     *              1) if there are we will set availableSpots and registered accordingly
     *              2) if the student can register but there are no more availablespots- we will remove the student from the list
     *                 and giva him an action to remove the course from his grade's list
     *              3) if the student cannot register due to prerequisits issue- we will remove him from regStudents
     */
    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        String[] result={"-","-"};
        //
        if (state.getAvailableSpots() >0 && !state.getRegStudents().contains(Student)) {
            Action<String[]> acceptToCourse = new AcceptToCourse(this.Course, this.Grade[0], state.getPrequisites());
            List<Action<String[]>> actions = new ArrayList<>();
            actions.add(acceptToCourse);
            state.getLogger().add(Student);
            then(actions, () ->{
                    if (!acceptToCourse.getResult().get()[0].equals("-") && state.getAvailableSpots() >0&&
                            state.getLogger().contains(Student)&&!state.getRegStudents().contains(Student)) {
                        //if student should be registered, or has already been registered
                        state.setRegistered(state.getRegistered() + 1);
                        state.setAvailableSpots(state.getAvailableSpots()-1);
                        state.getRegStudents().add(Student);
                        this.complete(acceptToCourse.getResult().get());
                    }
                    else if (!state.getLogger().contains(Student)) {
                        complete(result);
                    }
                    else{// if the student doesn't meet the prerequisites or there is not enougf room, remove from list
                        state.getLogger().remove(Student);
                        SelfUnregisterStudent unregister = new SelfUnregisterStudent(Course);
                        List<Action<Boolean>> actions1 = new ArrayList<>();
                        actions1.add(unregister);
                        then(actions1,()-> this.complete(result));
                        sendMessage(unregister,Student,new StudentPrivateState());
                    }

            });
            sendMessage(acceptToCourse, Student, new StudentPrivateState());
        }
        else
            this.complete(result);
    }
}
