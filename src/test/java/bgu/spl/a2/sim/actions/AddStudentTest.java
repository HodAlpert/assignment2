package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

public class AddStudentTest {
    CountDownLatch latch;
    ActorThreadPool pool;
    CountDownLatch latch1;

    @Before
    public void setup() {
        pool = new ActorThreadPool(4);
        latch = new CountDownLatch(1);
        latch1 = new CountDownLatch(1);
        pool.start();
    }

    @After
    public void close() {
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * check that student was initialized with name
     * and that the student is in the department
     * */
    public void positiveCheck() {
        AddStudent action = new AddStudent("dept1", "student1");
        action.getResult().subscribe(latch::countDown);
        DepartmentPrivateState state = new DepartmentPrivateState();
        pool.submit(action,"dept1",state);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("student was not added for some reason",action.getResult().get());
        assertTrue("student is not in dept",state.getStudentList().contains("student1"));
        StudentPrivateState student = (StudentPrivateState)pool.getPrivateState("student1");
        assertTrue("student name was init",student.getStudent().equals("student1"));


    }
}