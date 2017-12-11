package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class AddStudent extends Action<Boolean> {

    private String department;
    private String student;

    public AddStudent(String department, String student) {
        this.department = department;
        this.student=student;
        this.setActionName("Add Student");
    }

    @Override
    protected void start() {
        DepartmentPrivateState state = (DepartmentPrivateState) getState();
        if (!state.getStudentList().contains(student)){
            Action<Boolean> selfAddStudent = new SelfAddStudent(student);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(selfAddStudent);
            sendMessage(selfAddStudent,student,new StudentPrivateState());
            then(actions, ()-> complete(true));
            state.getStudentList().add(student);
        }
    }
}
