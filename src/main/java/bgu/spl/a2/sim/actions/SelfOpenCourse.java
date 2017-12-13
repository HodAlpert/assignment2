package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.List;

public class SelfOpenCourse extends Action<Boolean> {

    private String course;
    private Integer availableSpots;
    private List<String> prerequisites;

    public SelfOpenCourse(String course,Integer availableSpots, List<String> prerequisites){
        this.course=course;
        this.availableSpots=availableSpots;
        this.prerequisites=prerequisites;
        this.setActionName("Self Open Course");
    }

    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        state.setAvailableSpots(this.availableSpots);
        state.setPrerequisites(this.prerequisites);
        complete(true);
    }
}
