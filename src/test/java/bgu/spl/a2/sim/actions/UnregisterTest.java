package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
@RunWith(Parameterized.class)

public class UnregisterTest {
    private ActorThreadPool pool;
    private CountDownLatch latch1;
    private StudentPrivateState student;
    private CoursePrivateState course;
    private CoursePrivateState course1;
    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[2000][0]);
    }

    @Before
    public void setup(){
        CountDownLatch latch;
        pool = new ActorThreadPool(4);
        latch = new CountDownLatch(3);
        latch1 = new CountDownLatch(1);
        pool.start();
        String[] pre = {};
        OpenCourse c = new OpenCourse("dept","course", "5",pre);
        OpenCourse c1 = new OpenCourse("dept","course1", "5",pre);
        AddStudent s = new AddStudent("dept","student");
        c.getResult().subscribe(latch::countDown);
        c1.getResult().subscribe(latch::countDown);
        s.getResult().subscribe(latch::countDown);
        pool.submit(c,"dept",new DepartmentPrivateState());
        pool.submit(s,"dept",new DepartmentPrivateState());
        pool.submit(c1,"dept",new DepartmentPrivateState());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        student = (StudentPrivateState)pool.getPrivateState("student");
        course = (CoursePrivateState)pool.getPrivateState("course");
        course1 = (CoursePrivateState)pool.getPrivateState("course1");


    }
    @After
    public void close(){
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
/**
 * negative check- if a student is not registered- should return false and not inc
 * */
    @Test
    public void negativeTest(){
        Unregister unregister = new Unregister("student","course");
        unregister.getResult().subscribe(latch1::countDown);
        pool.submit(unregister,"course", new CoursePrivateState());
        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse("should return false",unregister.getResult().get());
        assertTrue("registered number should not be changed",course.getRegistered()==0);
        assertTrue("availableSpots number should not be changed",course.getAvailableSpots()==5);
    }
    /**
     * positive check- if a student is registered- should return true and remove him from course
     */
    @Test
    public void positiveTest(){
        CountDownLatch latch2 = new CountDownLatch(3);
        String[] pre = {"-"};
        ParticipateInCourse parInc1 = new ParticipateInCourse("student","course1",pre);
        parInc1.getResult().subscribe(latch2::countDown);
        Unregister unregister = new Unregister("student","course1");
        unregister.getResult().subscribe(latch2::countDown);
        ParticipateInCourse parInc2 = new ParticipateInCourse("student","course1",pre);
        parInc2.getResult().subscribe(latch2::countDown);



        pool.submit(parInc1,"course1",new CoursePrivateState());

        pool.submit(unregister,"course1", new CoursePrivateState());
        pool.submit(parInc2,"course1",new CoursePrivateState());

        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("should return true",unregister.getResult().get());
        assertTrue("registered number should not be changed",course1.getRegistered()==1);
        assertTrue("availableSpots number should not be changed",course1.getAvailableSpots()==4);

    }


}