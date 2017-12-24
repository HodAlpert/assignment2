package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
@RunWith(Parameterized.class)
public class OpenCourseTest {
    ActorThreadPool pool;
    CountDownLatch count;

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[1000][0]);
    }
    @Test
    public void positiveTest() {
        pool = new ActorThreadPool(6);
        pool.start();
        count=new CountDownLatch(2);
        String[] prerequisits={"Course1","Course2"};
        OpenCourse action=new OpenCourse(
                "CS",
                "Data Structore",
                "30",
                prerequisits
                );
        OpenCourse action2=new OpenCourse(
                "CS",
                "SPL",
                "30",
                prerequisits
        );
        pool.submit(action,"CS",new DepartmentPrivateState());
        pool.submit(action2,"CS",new DepartmentPrivateState());
        action.getResult().subscribe(()->{count.countDown();
            System.out.println("countDown subscribe called for "+ action.getActionName()+ " of Data Structore");});
        System.out.println("subscribed countdown latch for Data Structore");

        action2.getResult().subscribe(()->{count.countDown();
            System.out.println("countDown subscribe called for "+ action.getActionName()+ " of SPL");
    });
        System.out.println("subscribed countdown latch for SPL");


        try {
            count.await();
            System.out.println("finished stage 1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PrivateState department=pool.getPrivateState("CS");
        department=(DepartmentPrivateState)department;
        assertTrue("course is not in Department courses list",((DepartmentPrivateState) department).getCourseList().contains("Data Structore"));
        PrivateState course=pool.getPrivateState("Data Structore");
        course=(CoursePrivateState)course;
        assertTrue("prerequisits dont mach expectations",((CoursePrivateState) course).getPrequisites().contains("Course1"));
        assertTrue("prerequisits dont mach expectations",((CoursePrivateState) course).getPrequisites().contains("Course2"));
        assertTrue("available Spots dont mach expectations",((CoursePrivateState) course).getAvailableSpots()==30);
        CountDownLatch count2=new CountDownLatch(1);
        OpenCourse action1=new OpenCourse(
                "CS",
                "Data Structore",
                "30",
                prerequisits
        );
        action1.getResult().subscribe(()->count2.countDown());
        pool.submit(action1,"CS",new DepartmentPrivateState());
        try {
            count2.await();
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