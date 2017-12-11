package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class CloseCourse extends Action<Boolean> {
    private String Department;
    private String Course;

    /**
     * @param Department to remove course from
     * @param Course to remove from department
     */
    public CloseCourse(String Department, String Course){
        setActionName("Close Course");
        this.Department = Department;
        this.Course=Course;
    }
    @Override
    protected void start() {
        DepartmentPrivateState state = (DepartmentPrivateState) getState();
        if (state.getCourseList().contains(Course)){
            state.getCourseList().remove(Course);
            Action<Boolean> action = new SelfCloseCourse(Course);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(action);
            sendMessage(action,Course,new CoursePrivateState()); //TODO should send to existing course
            then(actions, ()->{
                complete(true);
            });

        }


    }
}
