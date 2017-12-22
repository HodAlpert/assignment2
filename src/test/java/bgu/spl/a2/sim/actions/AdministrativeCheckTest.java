package bgu.spl.a2.sim.actions;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;

public class AdministrativeCheckTest {
    private ActorThreadPool pool = new ActorThreadPool(7);
    private CountDownLatch latch1 = new CountDownLatch(9);
    private CountDownLatch latch2 = new CountDownLatch(16);
    private CountDownLatch latch3=new CountDownLatch(1);

    private StudentPrivateState studentState1;
    private StudentPrivateState studentState2;
    private StudentPrivateState studentState3;
    private StudentPrivateState studentState4;
    private StudentPrivateState studentState5;
    private StudentPrivateState studentState6;
    private long Failsig = 1111;
    private long successSig = 2222;


    @Before
    public void setup() {
        //----------------------------------initializing-------------------------
        Warehouse warehouse=Warehouse.getInstance();
        pool.start();

        Computer A = new Computer("A",successSig,Failsig);
        Computer B = new Computer("B",successSig,Failsig);
        Computer C = new Computer("C",successSig,Failsig);

        warehouse.addComputer(A);
        warehouse.addComputer(B);
        warehouse.addComputer(C);

        String[] pre = {};
        String[] passingGrade = {"80"};
        String[] failingGrade = {"40"};
        String[] emptyGrade = {"-"};
        //---------------------------------phase1-------------------------
        OpenCourse course1 = new OpenCourse("dept","course1","10",pre);
        OpenCourse course2 = new OpenCourse("dept","course2","10",pre);
        OpenCourse course3 = new OpenCourse("dept","course3","10",pre);
        course1.getResult().subscribe(latch1::countDown);
        course2.getResult().subscribe(latch1::countDown);
        course3.getResult().subscribe(latch1::countDown);


        AddStudent student1 = new AddStudent("dept","student1");
        AddStudent student2 = new AddStudent("dept","student2");
        AddStudent student3= new AddStudent("dept","student3");
        AddStudent student4= new AddStudent("dept","student4");
        AddStudent student5 = new AddStudent("dept","student5");
        AddStudent student6 = new AddStudent("dept","student6");
        student1.getResult().subscribe(latch1::countDown);
        student2.getResult().subscribe(latch1::countDown);
        student3.getResult().subscribe(latch1::countDown);
        student4.getResult().subscribe(latch1::countDown);
        student5.getResult().subscribe(latch1::countDown);
        student6.getResult().subscribe(latch1::countDown);

        pool.submit(course1,"dept",new DepartmentPrivateState());
        pool.submit(course2,"dept",new DepartmentPrivateState());
        pool.submit(course3,"dept",new DepartmentPrivateState());
        pool.submit(student1,"dept",new DepartmentPrivateState());
        pool.submit(student2,"dept",new DepartmentPrivateState());
        pool.submit(student3,"dept",new DepartmentPrivateState());
        pool.submit(student4,"dept",new DepartmentPrivateState());
        pool.submit(student5,"dept",new DepartmentPrivateState());
        pool.submit(student6,"dept",new DepartmentPrivateState());

        try {
            latch1.await();
            System.out.println(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //----------------------------------phase2------------------------------
        //student1 courses: (should pass all)
        ParticipateInCourse student1Course1 = new ParticipateInCourse("student1","course1",passingGrade);
        ParticipateInCourse student1Course2 = new ParticipateInCourse("student1","course2",passingGrade);
        ParticipateInCourse student1Course3 = new ParticipateInCourse("student1","course3",passingGrade);
        //student2 courses: (should register to two courses)
        ParticipateInCourse student2Course1 = new ParticipateInCourse("student2","course1",passingGrade);
        ParticipateInCourse student2Course2 = new ParticipateInCourse("student2","course2",passingGrade);
        //student3 courses: (should register to two courses
        ParticipateInCourse student3Course2 = new ParticipateInCourse("student3","course2",passingGrade);
        ParticipateInCourse student3Course3 = new ParticipateInCourse("student3","course3",passingGrade);
        //student4 courses: (should register all courses- one o then with empty grade)
        ParticipateInCourse student4Course1 = new ParticipateInCourse("student4","course1",passingGrade);
        ParticipateInCourse student4Course2 = new ParticipateInCourse("student4","course2",emptyGrade);
        ParticipateInCourse student4Course3 = new ParticipateInCourse("student4","course3",passingGrade);
        //student5 courses:( should register all)
        ParticipateInCourse student5Course1 = new ParticipateInCourse("student5","course1",passingGrade);
        ParticipateInCourse student5Course2 = new ParticipateInCourse("student5","course2",passingGrade);
        ParticipateInCourse student5Course3 = new ParticipateInCourse("student5","course3",passingGrade);
        //student6 courses:( should register all with one failing grade)
        ParticipateInCourse student6Course1 = new ParticipateInCourse("student6","course1",passingGrade);
        ParticipateInCourse student6Course2 = new ParticipateInCourse("student6","course2",passingGrade);
        ParticipateInCourse student6Course3 = new ParticipateInCourse("student6","course3",failingGrade);
        //-----------------------------------------phase3---------------------------------------------------
        student1Course1.getResult().subscribe(latch2::countDown);
        student1Course2.getResult().subscribe(latch2::countDown);
        student1Course3.getResult().subscribe(latch2::countDown);
        student2Course1.getResult().subscribe(latch2::countDown);
        student2Course2.getResult().subscribe(latch2::countDown);
        student3Course2.getResult().subscribe(latch2::countDown);
        student3Course3.getResult().subscribe(latch2::countDown);
        student4Course1.getResult().subscribe(latch2::countDown);
        student4Course2.getResult().subscribe(latch2::countDown);
        student4Course3.getResult().subscribe(latch2::countDown);
        student5Course1.getResult().subscribe(latch2::countDown);
        student5Course2.getResult().subscribe(latch2::countDown);
        student5Course3.getResult().subscribe(latch2::countDown);
        student6Course1.getResult().subscribe(latch2::countDown);
        student6Course2.getResult().subscribe(latch2::countDown);
        student6Course3.getResult().subscribe(latch2::countDown);

        pool.submit(student1Course1,"course1",new CoursePrivateState());
        pool.submit(student1Course2,"course2",new CoursePrivateState());
        pool.submit(student1Course3,"course3",new CoursePrivateState());
        pool.submit(student2Course1,"course1",new CoursePrivateState());
        pool.submit(student2Course2,"course2",new CoursePrivateState());
        pool.submit(student3Course2,"course2",new CoursePrivateState());
        pool.submit(student3Course3,"course3",new CoursePrivateState());
        pool.submit(student4Course1,"course1",new CoursePrivateState());
        pool.submit(student4Course2,"course2",new CoursePrivateState());
        pool.submit(student4Course3,"course3",new CoursePrivateState());
        pool.submit(student5Course1,"course1",new CoursePrivateState());
        pool.submit(student5Course2,"course2",new CoursePrivateState());
        pool.submit(student5Course3,"course3",new CoursePrivateState());
        pool.submit(student6Course1,"course1",new CoursePrivateState());
        pool.submit(student6Course2,"course2",new CoursePrivateState());
        pool.submit(student6Course3,"course3",new CoursePrivateState());
        try {
            latch2.await();
            System.out.println(2);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String[] students1 = {"student1"};
        String[] students2 = {"student2"};
        String[] students3 = {"student3"};
        String[] students4 = {"student4"};
        String[] students5 = {"student5"};
        String[] students6 = {"student6"};



        ArrayList<String> obligations = new ArrayList<>();
        obligations.add("course1");
        obligations.add("course2");
        obligations.add("course3");
        AdministrativeCheck check = new AdministrativeCheck("dept",students1,"A",obligations);
        AdministrativeCheck check2 = new AdministrativeCheck("dept",students2,"A",obligations);
        AdministrativeCheck check3 = new AdministrativeCheck("dept",students3,"A",obligations);
        AdministrativeCheck check4 = new AdministrativeCheck("dept",students4,"A",obligations);
        AdministrativeCheck check5 = new AdministrativeCheck("dept",students5,"A",obligations);
        AdministrativeCheck check6 = new AdministrativeCheck("dept",students6,"A",obligations);


        check.getResult().subscribe(latch3::countDown);
        check2.getResult().subscribe(latch3::countDown);
        check3.getResult().subscribe(latch3::countDown);
        check4.getResult().subscribe(latch3::countDown);
        check5.getResult().subscribe(latch3::countDown);
        check6.getResult().subscribe(latch3::countDown);


        pool.submit(check,"dept",new DepartmentPrivateState());
//        pool.submit(check2,"dept",new DepartmentPrivateState());
//        pool.submit(check3,"dept",new DepartmentPrivateState());
//        pool.submit(check4,"dept",new DepartmentPrivateState());
//        pool.submit(check5,"dept",new DepartmentPrivateState());
//        pool.submit(check6,"dept",new DepartmentPrivateState());



        try {
            latch3.await();
            System.out.println(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        studentState1 = (StudentPrivateState)pool.getPrivateState("student1");
        studentState2 = (StudentPrivateState)pool.getPrivateState("student2");
        studentState3 = (StudentPrivateState)pool.getPrivateState("student3");
        studentState4 = (StudentPrivateState)pool.getPrivateState("student4");
        studentState5 = (StudentPrivateState)pool.getPrivateState("student5");
        studentState6 = (StudentPrivateState)pool.getPrivateState("student6");




    }
    @Test
    public void test(){
        assertTrue("student1 should have successsig",studentState1.getSignature()==successSig);
//        assertTrue("student2 should have failsig",studentState2.getSignature()==Failsig);
//        assertTrue("student3 should have failsig",studentState3.getSignature()==Failsig);
//        assertTrue("student4 should have failsig",studentState4.getSignature()==Failsig);
//        assertTrue("student5 should have successsig",studentState5.getSignature()==successSig);
//        assertTrue("student6 should have failsig",studentState6.getSignature()==Failsig);

    }
    @After
    public void after(){
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}