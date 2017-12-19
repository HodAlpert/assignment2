//package bgu.spl.a2.sim.actions;
//
//import bgu.spl.a2.ActorThreadPool;
//import bgu.spl.a2.sim.privateStates.CoursePrivateState;
//import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
//import bgu.spl.a2.sim.privateStates.StudentPrivateState;
//import org.junit.Before;
//
//import java.util.concurrent.CountDownLatch;
//
//public class AdministrativeCheckTest {
//    private ActorThreadPool pool;
//    private CountDownLatch latch1;
//    private StudentPrivateState student;
//    private CoursePrivateState course;
//    private CoursePrivateState course1;
//
//    @Before
//    public void setup(){
//        CountDownLatch latch;
//        pool = new ActorThreadPool(4);
//        latch = new CountDownLatch(3);
//        latch1 = new CountDownLatch(1);
//        pool.start();
//        String[] pre = {};
//        OpenCourse c = new OpenCourse("dept","course", "5",pre);
//        OpenCourse c1 = new OpenCourse("dept","course1", "5",pre);
//        AddStudent s = new AddStudent("dept","student");
//        c.getResult().subscribe(latch::countDown);
//        c1.getResult().subscribe(latch::countDown);
//        s.getResult().subscribe(latch::countDown);
//        pool.submit(c,"dept",new DepartmentPrivateState());
//        pool.submit(s,"dept",new DepartmentPrivateState());
//        pool.submit(c1,"dept",new DepartmentPrivateState());
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        student = (StudentPrivateState)pool.getPrivateState("student");
//        course = (CoursePrivateState)pool.getPrivateState("course");
//        course1 = (CoursePrivateState)pool.getPrivateState("course1");
//        CountDownLatch latch2 = new CountDownLatch(1);
//        String[] pre1 = {"-"};
//        ParticipateInCourse parInc1 = new ParticipateInCourse("student","course1",pre1);
//        parInc1.getResult().subscribe(latch1::countDown);
//        pool.submit(parInc1,"course1",new CoursePrivateState());
//        try {
//            latch1.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//}