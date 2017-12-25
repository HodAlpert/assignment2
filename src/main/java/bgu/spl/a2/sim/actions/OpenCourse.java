package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class OpenCourse extends Action<Boolean>{
    /**
     * an action that adds a new course to the department
     */
    private String Department;
    private String Course;
    private String Space;
    private String[] Prerequisites;

    /**
     * @param Department to open the course at
     * @param Course to open
     * @param availableSpots of the course
     * @param Prerequisites list of courses any student who wants to register needs to pass
     */
    public OpenCourse(String Department, String Course,String availableSpots, String[] Prerequisites) {
        this.setActionName("Open Course");
        this.Department = Department;
        this.Course = Course;
        this.Space=availableSpots;
        this.Prerequisites=Prerequisites;
    }

    /**
     * checks if course is already in courses list of the department
     * if it's not- sends an action for the course to open itself
     * action is completed when selfopencourse is completed.
     */
    @Override
    protected void start() {
        DepartmentPrivateState state = (DepartmentPrivateState) getState();
        if (!state.getCourseList().contains(Course)){
            state.getCourseList().add(Course);//to reject future requests
            Action<Boolean> selfOpen = new SelfOpenCourse(Course,Integer.parseInt(Space),Prerequisites);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(selfOpen);
            then(actions, ()-> {complete(true);
            });
            sendMessage(selfOpen,Course,new CoursePrivateState());
        }
        else//if course already exist
            complete(false);//should reject the request
    }
}
