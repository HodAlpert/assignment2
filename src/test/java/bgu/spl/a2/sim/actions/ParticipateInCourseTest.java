package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ParticipateInCourseTest {
    private CountDownLatch latch;
    private ActorThreadPool pool;
    private CountDownLatch latch1;
    private CountDownLatch latch2;
    private CountDownLatch latch3;
    private CountDownLatch latch4;

    @Before
    public void setup(){
        pool = new ActorThreadPool(9);
        latch = new CountDownLatch(6);
        latch1 = new CountDownLatch(3);
        latch2 = new CountDownLatch(1);
        latch3 = new CountDownLatch(1);
        latch4 = new CountDownLatch(2);

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
     * - check with "-" grade
     * - check with valid grade
     * - check with a closed course
     * - check with a full course
     * - check that the student is in the student's list
     * - check when student stand the prerequisits
     * - check when the student don't match the prerequisits
     * */
    @Test
    public void positiveTest(){
        String[] pre = {"noPre"};
        String[] EmptyPre = {};

        OpenCourse noPre = new OpenCourse("dept","noPre","5",EmptyPre);
        noPre.getResult().subscribe(latch::countDown);
        OpenCourse course1 = new OpenCourse("dept","course1","1",pre);
        course1.getResult().subscribe(latch::countDown);
        OpenCourse courseToClose = new OpenCourse("dept","courseToClose","1",EmptyPre);
        courseToClose.getResult().subscribe(latch::countDown);
        AddStudent student1 = new AddStudent("dept","student1");
        student1.getResult().subscribe(latch::countDown);
        AddStudent student2 = new AddStudent("dept","student2");
        student2.getResult().subscribe(latch::countDown);
        AddStudent student3 = new AddStudent("dept","student3");
        student3.getResult().subscribe(latch::countDown);
        pool.submit(noPre,"dept", new DepartmentPrivateState());
        pool.submit(course1,"dept", new DepartmentPrivateState());
        pool.submit(courseToClose,"dept", new DepartmentPrivateState());
        pool.submit(student1,"dept", new DepartmentPrivateState());
        pool.submit(student2,"dept", new DepartmentPrivateState());
        pool.submit(student3,"dept", new DepartmentPrivateState());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] grade1 = {"-"};
        String[] grade2 = {"80"};

        ParticipateInCourse student1TonoPre = new ParticipateInCourse("student1","noPre",grade1);
        ParticipateInCourse student2TonoPre = new ParticipateInCourse("student2","noPre",grade2);
        CloseCourse close = new CloseCourse("dept","courseToClose");
        student1TonoPre.getResult().subscribe(latch1::countDown);
        student2TonoPre.getResult().subscribe(latch1::countDown);
        close.getResult().subscribe(latch1::countDown);
        pool.submit(student1TonoPre,"noPre",new CoursePrivateState());
        pool.submit(student2TonoPre,"noPre",new CoursePrivateState());
        pool.submit(close,"dept",new DepartmentPrivateState());


        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StudentPrivateState student1t = (StudentPrivateState)pool.getPrivateState("student1");
        StudentPrivateState student2t = (StudentPrivateState)pool.getPrivateState("student2");
        CoursePrivateState course1t = (CoursePrivateState)pool.getPrivateState("noPre");
        assertTrue("student1 is not registered to noPreCourse",student1t.getGrades().containsKey("noPre")&student1t.getGrades().get("noPre")==null);
        assertTrue("student2 is not registered to noPreCourse",student2t.getGrades().containsKey("noPre")&student2t.getGrades().get("noPre")==80);
        assertTrue("course dont have the students",course1t.getRegStudents().contains("student1")&course1t.getRegStudents().contains("student2"));
        assertTrue("availablespots not right",course1t.getAvailableSpots()==3);
        assertTrue("two students should be registered",course1t.getRegistered()==2);
        ParticipateInCourse s3=new ParticipateInCourse("student3","courseToClose",grade2);
        s3.getResult().subscribe(latch2::countDown);
        pool.submit(s3,"courseToClose",new CoursePrivateState());
        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StudentPrivateState student3t = (StudentPrivateState)pool.getPrivateState("student3");
        CoursePrivateState coursetoclose = (CoursePrivateState)pool.getPrivateState("courseToClose");

        assertFalse("student3 is registered to course",student3t.getGrades().containsKey("courseToClose")|coursetoclose.getRegStudents().contains("student3"));


        ParticipateInCourse fail=new ParticipateInCourse("student3","course1",grade2);

        ParticipateInCourse sucess2=new ParticipateInCourse("student2","course1",grade2);
        sucess2.getResult().subscribe(latch4::countDown);
        ParticipateInCourse sucess=new ParticipateInCourse("student1","course1",grade2);
        sucess.getResult().subscribe(latch4::countDown);

        fail.getResult().subscribe(latch3::countDown);
        pool.submit(fail,"course1",new CoursePrivateState());
        try {
            latch3.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse("student3 is registered to course1",student3t.getGrades().containsKey("courseToClose")|coursetoclose.getRegStudents().contains("student3"));
        pool.submit(sucess,"course1",new CoursePrivateState());
        pool.submit(sucess2,"course1",new CoursePrivateState());
        try {
            latch4.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CoursePrivateState course = (CoursePrivateState)pool.getPrivateState("course1");
        assertFalse("student1 and student 2 were registered to course1",student1t.getGrades().containsKey("course1") & student2t.getGrades().containsKey("course1"));
        boolean student1registered = course.getRegStudents().contains("student1");
        boolean student2registered = course.getRegStudents().contains("student2");
        assertTrue("course1 has both students",((student1registered|student2registered)&!(student1registered&student2registered)));
        assertTrue("course should have 0 available spots",course.getRegistered()==1&course.getAvailableSpots()==0);




    }

}