package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

public class AddSpacesTest {
    CountDownLatch latch;
    ActorThreadPool pool;
    CountDownLatch latch1;
    @Before
    public void setup(){
        pool = new ActorThreadPool(4);
        latch = new CountDownLatch(1);
        latch1 = new CountDownLatch(1);
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
 * if availablespots==-1: do nothing
 * else- should return value after inc()
 * */
    @Test
    public void positiveCheck() {
        int numOfCourses = 5;
        String[] prerequisits = {"Course2"};
        OpenCourse course = new OpenCourse("dept1","course1","5",prerequisits);
        course.getResult().subscribe(latch::countDown);
        pool.submit(course,"dept1",new DepartmentPrivateState());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AddSpaces add = new AddSpaces("course1",3);
        add.getResult().subscribe(latch1::countDown);
        pool.submit(add,"course1",new CoursePrivateState());
        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("numOf availablespots is not OK",add.getResult().get()==numOfCourses+3);
    }
    @Test
    public void negativeTest(){
        String[] prerequisits = {"Course2"};
        OpenCourse course = new OpenCourse("dept1","course1","5",prerequisits);
        course.getResult().subscribe(latch::countDown);
        pool.submit(course,"dept1",new DepartmentPrivateState());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CoursePrivateState course1 =  (CoursePrivateState)pool.getPrivateState("course1");
        course1.setAvailableSpots(-1);
        AddSpaces add = new AddSpaces("course1",3);
        add.getResult().subscribe(latch1::countDown);
        pool.submit(add,"course1",new CoursePrivateState());
        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("numOf availablespots is not OK2",add.getResult().get()==-1);
    }

}