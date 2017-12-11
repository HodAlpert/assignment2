package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class AddSpaces extends Action<Boolean> {
    private String Course;
    private int Number;

    /**
     * @param Course to add spaces to
     * @param Number of places to add to the course
     */
    public AddSpaces(String Course, int Number){
        setActionName("Add Spaces");
        this.Course=Course;
        this.Number=Number;
    }
    @Override
    protected void start() {
        //TODO implement flag to show if registration close was called
        CoursePrivateState state = (CoursePrivateState) getState();
        if (state.getAvailableSpots()==-1)
            complete(false);
        else{
            state.setAvailableSpots(state.getAvailableSpots()+Number);
            complete(true);
        }//else
    }//start
}//AddSpaces
