package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.Arrays;

public class SelfOpenCourse extends Action<Boolean> {
    /**
     * an action that sets the courses parameters after it was created
     */
    private String course;
    private Integer availableSpots;
    private String[] prerequisites;

    /**
     * @param course course name
     * @param availableSpots number of available Spots on innitiation
     * @param prerequisites list of courses any student who wants to register to current course should have
     */
    public SelfOpenCourse(String course,Integer availableSpots, String[] prerequisites){
        this.course=course;
        this.availableSpots=availableSpots;
        this.prerequisites=prerequisites;
        this.setActionName("Self Open Course");
    }

    /**
     * setting data members with their values
     */
    @Override
    protected void start() {
        CoursePrivateState state = (CoursePrivateState) getState();
        state.setCourse(course);
        state.setAvailableSpots(this.availableSpots);
        state.setPrerequisites(Arrays.asList(this.prerequisites));
        complete(true);


    }
}
