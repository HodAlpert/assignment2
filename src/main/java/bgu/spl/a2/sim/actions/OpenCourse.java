package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class OpenCourse extends Action<Boolean>{

    private String Department;
    private String Course;
    private Integer Space;
    private List<String> Prerequisites;

    public OpenCourse(String department, String Course,Integer availableSpots, List<String> Prerequisites) {
        this.setActionName("Open Course");
        this.Department = Department;
        this.Course = Course;
        this.Space=availableSpots;
        this.Prerequisites=Prerequisites;
    }

    @Override
    protected void start() {
        DepartmentPrivateState state = (DepartmentPrivateState) getState();
        if (!state.getCourseList().contains(Course)){
            state.getCourseList().add(Course);//to reject future requests
            Action<Boolean> selfOpen = new SelfOpenCourse(Course,Space,Prerequisites);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(selfOpen);
            sendMessage(selfOpen,Course,new CoursePrivateState());
            then(actions, ()-> complete(true));
        }
        else//if course already exist
            complete(true);
    }
}
