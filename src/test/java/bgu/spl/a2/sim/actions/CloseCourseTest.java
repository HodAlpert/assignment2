package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CloseCourseTest {
    CountDownLatch latch;
    ActorThreadPool pool;
    CountDownLatch latch1;
    CountDownLatch latch2;
    CountDownLatch latch3;


    @Before
    public void setup(){
        pool = new ActorThreadPool(4);
        latch = new CountDownLatch(1);
        latch1 = new CountDownLatch(14);
        latch2 = new CountDownLatch(1);
        latch3 = new CountDownLatch(1);
        pool.start();
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
 * no student should have a grade from the course
 * availablespots should be -1
 * registered should be 0
 * future partici[ate
 * */
    @Test
    public void positiveTest(){
        String[] pre = {};
        OpenCourse course = new OpenCourse("dept","course","8",pre);
        course.getResult().subscribe(latch::countDown);
        DepartmentPrivateState dept = new DepartmentPrivateState();
        pool.submit(course,"dept",dept);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] grade = {"60"};
        CoursePrivateState course1 = (CoursePrivateState)pool.getPrivateState("course");
        for (int i=0;i<7;i++){
            AddStudent student = new AddStudent("dept","student"+i);
            student.getResult().subscribe(latch1::countDown);
            ParticipateInCourse par=new ParticipateInCourse("student"+i,"course",grade);
            par.getResult().subscribe(latch1::countDown);
            pool.submit(student,"dept",new StudentPrivateState());
            pool.submit(par,"course",course1);

        }
        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StudentPrivateState[] students = new StudentPrivateState[7];
        for (int i=0;i<7;i++){
            students[i]=(StudentPrivateState)pool.getPrivateState("student"+i);
        }
        CloseCourse close = new CloseCourse("dept","course");
        close.getResult().subscribe(latch2::countDown);
        pool.submit(close,"dept",new DepartmentPrivateState());
        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse("department should not contain the course",dept.getCourseList().contains("course"));
        assertTrue("available spots should be -1",course1.getAvailableSpots()==-1);
        assertTrue("registered should be 0",course1.getRegistered()==0);
        for (int i=0;i<students.length;i++){
            assertFalse("student "+i+" is still registered to course",students[i].getGrades().containsKey("course"));
        }
        ParticipateInCourse par=new ParticipateInCourse("student6","course",grade);
        par.getResult().subscribe(latch3::countDown);
        pool.submit(par,"course",course1);
        try {
            latch3.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse("student was able to register after course was closed",par.getResult().get()[0].equals("course"));

    }
}