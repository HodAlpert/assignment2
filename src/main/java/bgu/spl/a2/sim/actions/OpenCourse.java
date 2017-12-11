package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class OpenCourse extends Action<Boolean>{

    private String department;
    private String course;
    private Integer availableSpots;
    private List<String> prerequisites;

    public OpenCourse(String department, String course,Integer availableSpots, List<String> prerequisites) {
        this.department = department;
        this.course = course;
        this.availableSpots=availableSpots;
        this.prerequisites=prerequisites;
        this.setActionName("Open Course");
    }

    @Override
    protected void start() {
        DepartmentPrivateState state = (DepartmentPrivateState) getState();
        if (!state.getCourseList().contains(course)){
            Action<Boolean> selfOpen = new SelfCloseCourse(course);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(selfOpen);
            sendMessage(selfOpen,course,new CoursePrivateState());
            then(actions, ()-> complete(true));
            state.getCourseList().add(course);
        }
    }
}
