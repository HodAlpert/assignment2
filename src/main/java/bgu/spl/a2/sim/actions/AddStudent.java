package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class AddStudent extends Action<Boolean> {

    private String Department;
    private String Student;

    public AddStudent(String Department, String Student) {
        this.setActionName("Add Student");
        this.Department = Department;
        this.Student=Student;
    }

    @Override
    protected void start() {
        DepartmentPrivateState state = (DepartmentPrivateState) getState();
        if (!state.getStudentList().contains(Student)){
            state.getStudentList().add(Student);//to reject future requests
            Action<Boolean> selfAddStudent = new SelfAddStudent(Student);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(selfAddStudent);
            sendMessage(selfAddStudent,Student,new StudentPrivateState());
            then(actions, ()-> complete(true));

        }
        else//if student exist
            complete(false);//should reject the request
    }
}
