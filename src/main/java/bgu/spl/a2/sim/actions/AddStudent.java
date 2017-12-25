package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class AddStudent extends Action<Boolean> {
    /**
     * An action that adds a given student to the department
     */
    private String Department;
    private String Student;

    /**
     * @param Department the department to whom the student should register
     * @param Student the student who needs to register to the department
     */
    public AddStudent(String Department, String Student) {
        this.setActionName("Add Student");
        this.Department = Department;
        this.Student=Student;
    }

    @Override
    protected void start() {
        DepartmentPrivateState state = (DepartmentPrivateState) getState();
        if (!state.getStudentList().contains(Student)){//if student is not in student's list of department
            state.getStudentList().add(Student);//to reject future requests
            Action<Boolean> selfAddStudent = new SelfAddStudent(Student); // create and add the student (if he doesn't exist)
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(selfAddStudent);
            then(actions, ()-> complete(true));

            sendMessage(selfAddStudent,Student,new StudentPrivateState());//sending the action for the student

        }
        else//if student exist
            complete(false);//should reject the request
    }
}
