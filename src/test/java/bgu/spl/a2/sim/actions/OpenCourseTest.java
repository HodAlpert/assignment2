package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class OpenCourseTest {
    ActorThreadPool pool;
    CountDownLatch count;

    @BeforeClass
    public static void startPool(){

    }
    @AfterClass
    public static void closePool(){

    }
    @Test
    public void positiveTest() {
        pool = new ActorThreadPool(4);
        pool.start();
        count=new CountDownLatch(1);
        List<String> prerequisits=new ArrayList<String>();
        prerequisits.add("Course1");
        prerequisits.add("Course2");
        OpenCourse action=new OpenCourse(
                "CS",
                "Data Structore",
                30,
                prerequisits
                );
        action.getResult().subscribe(()->count.countDown());
        pool.submit(action,"CS",new DepartmentPrivateState());
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PrivateState department=pool.getPrivaetState("CS");
        department=(DepartmentPrivateState)department;
        assertTrue("course is not in Department courses list",((DepartmentPrivateState) department).getCourseList().contains("Data Structore"));
        PrivateState course=pool.getPrivaetState("Data Structore");
        course=(CoursePrivateState)course;
        assertTrue("prerequisits dont mach expectations",((CoursePrivateState) course).getPrequisites().contains("Course1"));
        assertTrue("prerequisits dont mach expectations",((CoursePrivateState) course).getPrequisites().contains("Course2"));
        assertTrue("available Spots dont mach expectations",((CoursePrivateState) course).getAvailableSpots()==30);
        count=new CountDownLatch(1);
        OpenCourse action1=new OpenCourse(
                "CS",
                "Data Structore",
                30,
                prerequisits
        );
        action1.getResult().subscribe(()->count.countDown());
        pool.submit(action1,"CS",new DepartmentPrivateState());
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse("should not open course again",action1.getResult().get());
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}