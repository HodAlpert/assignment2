package bgu.spl.a2;

import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class ActionTest {

    @Parameterized.Parameters
    public static Collection parameters() {
        Object[][] params = new Object[1000][];
        for (int i = 0; i < params.length; i++) {
            params[i] = new Object[]{i % 10 + 1};
        }
        return Arrays.asList(params);
    }

    public ActionTest(int nThreads) {
        this.nThreads = nThreads;
    }

    private int nThreads;
    private ActorThreadPool threadPool;

    @Before
    public void setUp() {
        threadPool = new ActorThreadPool(nThreads);
    }

    @Test
    public void registerUnregister(){
        Action openCourse1 = new OpenCourse("dept1","course1","10",new String[]{});
        Action openCourse2 = new OpenCourse("dept1","course2","10",new String[]{});
        Action openCourse3 = new OpenCourse("dept2","course3","10",new String[]{"course1","course2"});
        Action addStudent1 = new AddStudent("dept1","student1");
        Action addStudent2 = new AddStudent("dept1","student2");
        Action addStudent3 = new AddStudent("dept2","student3");


        Action Participate1 = new ParticipateInCourse("student1","course3",new String[]{"56"});
        Action unregister1 = new Unregister("student1","course3");
        Action Participate2 = new ParticipateInCourse("student2","course2",new String[]{"100"});
        Action unregister2 = new Unregister("student2","course2");
        Action Participate3 = new ParticipateInCourse("student2","course2",new String[]{"90"});

        try {
            CountDownLatch latch = new CountDownLatch(6);
            openCourse1.getResult().subscribe(() -> {
                System.out.println("openCourse1 done");
                latch.countDown();
            });
            openCourse2.getResult().subscribe(() -> {
                System.out.println("openCourse2 done");
                latch.countDown();
            });
            openCourse3.getResult().subscribe(() -> {
                System.out.println("openCourse3 done");
                latch.countDown();
            });
            addStudent1.getResult().subscribe(() -> {
                System.out.println("addStudent1 done");
                latch.countDown();
            });
            addStudent2.getResult().subscribe(() -> {
                System.out.println("addStudent2 done");
                latch.countDown();
            });
            addStudent3.getResult().subscribe(() -> {
                System.out.println("addStudent3 done");
                latch.countDown();
            });
        threadPool.start();
        threadPool.submit(openCourse1,"dept1",new DepartmentPrivateState());
        threadPool.submit(openCourse2,"dept1",new DepartmentPrivateState());
        threadPool.submit(openCourse3,"dept2",new DepartmentPrivateState());
        threadPool.submit(addStudent1,"dept1",new DepartmentPrivateState());
        threadPool.submit(addStudent2,"dept1",new DepartmentPrivateState());
        threadPool.submit(addStudent3,"dept2",new DepartmentPrivateState());
        latch.await();


            CountDownLatch latch1 = new CountDownLatch(5);
            Participate1.getResult().subscribe(() -> {
                System.out.println("Participate1 done");
                latch1.countDown();
            });
            unregister1.getResult().subscribe(() -> {
                System.out.println("unregister1 done");
                latch1.countDown();
            });
            Participate2.getResult().subscribe(() -> {
                System.out.println("Participate2 done");
                latch1.countDown();
            });
            unregister2.getResult().subscribe(() -> {
                System.out.println("unregister2 done");
                latch1.countDown();
            });
            Participate3.getResult().subscribe(() -> {
                System.out.println("Participate3 done");
                latch1.countDown();
            });

            // register and then unregister to course when student doesn't meet prerequisites
            threadPool.submit(Participate1,"course1",new StudentPrivateState());
            threadPool.submit(unregister1,"course1",new CoursePrivateState());
            // register -> unregister -> register
            threadPool.submit(Participate2,"course2",new StudentPrivateState());
            threadPool.submit(unregister1,"course2",new CoursePrivateState());
            threadPool.submit(Participate3,"course2",new StudentPrivateState());
            latch1.await();

            assertFalse("student1 was added to course1 when he shouldn't",
                    ((CoursePrivateState)threadPool.getPrivateState("course1")).getRegStudents().contains("student1") ||
                            ((CoursePrivateState)threadPool.getPrivateState("course1")).getRegistered()==1
            );
            assertFalse("student1 has a grade for course1 when he shouldn't",
                    ((StudentPrivateState)threadPool.getPrivateState("student1")).getGrades().containsKey("course1"));
            assertTrue("student2 isn't in course2 when he should",
                    ((CoursePrivateState)threadPool.getPrivateState("course2")).getRegStudents().contains("student2") ||
                            ((CoursePrivateState)threadPool.getPrivateState("course2")).getRegistered()==1
            );
            assertTrue("student2 has a grade for course2 when he shouldn't",
                    ((StudentPrivateState)threadPool.getPrivateState("student2")).getGrades().containsKey("course2"));

            threadPool.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        }

//        @Test
//        public void AllActions(){
//        Action openCourse1 = new OpenCourse("dept1","course1","10",new String[]{});
//        Action openCourse2 = new OpenCourse("dept1","course2","10",new String[]{});
//        Action openCourse3 = new OpenCourse("dept2","course3","10",new String[]{"course1","course2"});
//        Action openCourse4 = new OpenCourse("dept2","course4","10",new String[]{"course2"});
//
//        Action addStudent1 = new AddStudent("dept1","1");
//        Action addStudent2 = new AddStudent("dept1","2");
//        Action addStudent3 = new AddStudent("dept2","3");
//        Action Participate1 = new ParticipateInCourse("1","course1",new String[]{"-"});
//        Action Participate2 = new ParticipateInCourse("1","course2",new String[]{"100"});
//        Action Participate3 = new ParticipateInCourse("2","course2",new String[]{"50"});
//
//        Action regWithPref1 = new RegisterWithPreferences("1",new String[]{"course3","course4"},new String[]{"90","80"});
//        Action regWithPref2 = new RegisterWithPreferences("2",new String[]{"course3","course4"},new String[]{"90","80"});
//
//        Action addSpace1 = new AddSpaces("course1",1);
//        Action addSpace2 = new AddSpaces("course1",1);
//        Action addSpace3 = new AddSpaces("course1",1);
//        Action addSpace4 = new AddSpaces("course1",1);
//
//        Action unregister1 = new Unregister("1","course1");
//        Action unregister2 = new Unregister("1","course2");
//        Action unregister3 = new Unregister("2","course2");
//
//        Action closeCourse1 = new CloseCourse("dept1","course1");
//        Action closeCourse2 = new CloseCourse("dept1","course2");
//        Action closeCourse3= new CloseCourse("dept2","course3");
//        Action closeCourse4 = new CloseCourse("dept2","course4");
//
//
//        try {
//            CountDownLatch latch = new CountDownLatch(4);
//            openCourse1.getResult().subscribe(() -> {
//                System.out.println("openCourse1 done");
//                latch.countDown();
//            });
//            openCourse2.getResult().subscribe(() -> {
//                System.out.println("openCourse2 done");
//                latch.countDown();
//            });
//            openCourse3.getResult().subscribe(() -> {
//                System.out.println("openCourse3 done");
//                latch.countDown();
//            });
//            openCourse4.getResult().subscribe(() -> {
//                System.out.println("openCourse4 done");
//                latch.countDown();
//            });
//
//        threadPool.start();
//        threadPool.submit(openCourse1,"dept1",new DepartmentPrivateState());
//        threadPool.submit(openCourse2,"dept1",new DepartmentPrivateState());
//        threadPool.submit(openCourse3,"dept2",new DepartmentPrivateState());
//        threadPool.submit(openCourse4,"dept2",new DepartmentPrivateState());
//
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            CountDownLatch latch1 = new CountDownLatch(6);
//            addStudent1.getResult().subscribe(() -> {
//                System.out.println("addStudent1 done");
//                latch1.countDown();
//            });
//            addStudent2.getResult().subscribe(() -> {
//                System.out.println("addStudent2 done");
//                latch1.countDown();
//            });
//            addStudent3.getResult().subscribe(() -> {
//                System.out.println("addStudent3 done");
//                latch1.countDown();
//            });
//            Participate1.getResult().subscribe(() -> {
//                System.out.println("Participate1 done");
//                latch1.countDown();
//            });
//            Participate2.getResult().subscribe(() -> {
//                System.out.println("Participate3 done");
//                latch1.countDown();
//            });
//            Participate3.getResult().subscribe(() -> {
//                System.out.println("Participate3 done");
//                latch1.countDown();
//            });
//
//            threadPool.submit(addStudent1,"dept1",new DepartmentPrivateState());
//            threadPool.submit(addStudent2,"dept1",new DepartmentPrivateState());
//            threadPool.submit(addStudent3,"dept2",new DepartmentPrivateState());
//            threadPool.submit(Participate1,"course1",new StudentPrivateState());
//            threadPool.submit(Participate2,"course2",new StudentPrivateState());
//            threadPool.submit(Participate3,"course2",new StudentPrivateState());
//
//            latch1.await();
//
//            assertTrue("not all courses are in dept1",
//                    ((DepartmentPrivateState)threadPool.getPrivateState("dept1")).getCourseList().containsAll(Arrays.asList(new String[]{"course1","course2"})));
//            assertTrue("not all courses are in dept2",
//                    ((DepartmentPrivateState)threadPool.getPrivateState("dept2")).getCourseList().containsAll(Arrays.asList(new String[]{"course3","course4"})));
//            assertTrue("not all students are in dept1",
//                    ((DepartmentPrivateState)threadPool.getPrivateState("dept1")).getStudentList().containsAll(Arrays.asList(new String[]{"1","2"})));
//            assertTrue("not all students are in dept2",
//                    ((DepartmentPrivateState)threadPool.getPrivateState("dept2")).getStudentList().containsAll(Arrays.asList(new String[]{"3"})));
//
//            assertTrue("students were not added to course2 ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course1")).getRegStudents().contains("1") &&
//                            ((CoursePrivateState)threadPool.getPrivateState("course1")).getRegistered()==1
//            );
//            assertTrue("students were not added to course2 ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course2")).getRegStudents().containsAll(Arrays.asList(new String[]{"1", "2"})) &&
//                            ((CoursePrivateState)threadPool.getPrivateState("course2")).getRegistered()==2
//            );
//
//            CountDownLatch latch5 = new CountDownLatch(2);
//            regWithPref1.getResult().subscribe(() -> {
//                System.out.println("regWithPref1 done");
//                latch5.countDown();
//            });
//            regWithPref2.getResult().subscribe(() -> {
//                System.out.println("regWithPref2 done");
//                latch5.countDown();
//            });
//            threadPool.submit(regWithPref1,"1",new StudentPrivateState());
//            threadPool.submit(regWithPref2,"2",new StudentPrivateState());
//            latch5.await();
//
//            assertTrue("students were not added to course3 ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course3")).getRegStudents().contains("1") &&
//                            ((CoursePrivateState)threadPool.getPrivateState("course3")).getRegistered()==1
//            );
//            assertTrue("students were not added to course4 ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course4")).getRegStudents().contains("2") &&
//                            ((CoursePrivateState)threadPool.getPrivateState("course4")).getRegistered()==1
//            );
//
//            CountDownLatch latch2 = new CountDownLatch(4);
//            addSpace1.getResult().subscribe(() -> {
//                System.out.println("addSpace done");
//                latch2.countDown();
//            });
//            addSpace2.getResult().subscribe(() -> {
//                System.out.println("addSpace done");
//                latch2.countDown();
//            });
//            addSpace3.getResult().subscribe(() -> {
//                System.out.println("addSpace done");
//                latch2.countDown();
//            });
//            addSpace4.getResult().subscribe(() -> {
//                System.out.println("addSpace done");
//                latch2.countDown();
//            });
//
//            threadPool.submit(addSpace1,"course1",new CoursePrivateState());
//            threadPool.submit(addSpace2,"course1",new CoursePrivateState());
//            threadPool.submit(addSpace3,"course1",new CoursePrivateState());
//            threadPool.submit(addSpace4,"course1",new CoursePrivateState());
//            latch2.await();
//
//            assertTrue("added "+(((CoursePrivateState)threadPool.getPrivateState("course1")).getAvailableSpots()-9)+ " available spots instead of 4 to course1",
//                    ((CoursePrivateState)threadPool.getPrivateState("course1")).getAvailableSpots()==13);
//
//            CountDownLatch latch3 = new CountDownLatch(3);
//            unregister1.getResult().subscribe(() -> {
//                System.out.println("unregister1 done");
//                latch3.countDown();
//            });
//            unregister2.getResult().subscribe(() -> {
//                System.out.println("unregister2 done");
//                latch3.countDown();
//            });
//            unregister3.getResult().subscribe(() -> {
//                System.out.println("unregister3 done");
//                latch3.countDown();
//            });
//
//            threadPool.submit(unregister1,"course1",new CoursePrivateState());
//            threadPool.submit(unregister2,"course2",new CoursePrivateState());
//            threadPool.submit(unregister3,"course2",new CoursePrivateState());
//            latch3.await();
//
//            assertTrue("students were not removed from course1 ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course1")).getRegStudents().isEmpty() &&
//                            ((CoursePrivateState)threadPool.getPrivateState("course1")).getRegistered()==0
//            );
//            assertTrue("students were not removed from course2 ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course2")).getRegStudents().isEmpty() &&
//                            ((CoursePrivateState)threadPool.getPrivateState("course2")).getRegistered()==0
//            );
//            assertTrue("course was not removed from student 1 ",
//                    ((StudentPrivateState)threadPool.getPrivateState("1")).getGrades().size()==1);
//            assertTrue("course was not removed from student 2 ",
//                    ((StudentPrivateState)threadPool.getPrivateState("2")).getGrades().size()==1);
//
//            CountDownLatch latch4 = new CountDownLatch(4);
//            closeCourse1.getResult().subscribe(() -> {
//                System.out.println("closeCourse1 done");
//                latch4.countDown();
//            });
//            closeCourse2.getResult().subscribe(() -> {
//                System.out.println("closeCourse2 done");
//                latch4.countDown();
//            });
//            closeCourse3.getResult().subscribe(() -> {
//                System.out.println("closeCourse3 done");
//                latch4.countDown();
//            });
//            closeCourse4.getResult().subscribe(() -> {
//                System.out.println("closeCourse4 done");
//                latch4.countDown();
//            });
//
//            threadPool.submit(closeCourse1,"dept1",new DepartmentPrivateState());
//            threadPool.submit(closeCourse2,"dept1",new DepartmentPrivateState());
//            threadPool.submit(closeCourse3,"dept2",new DepartmentPrivateState());
//            threadPool.submit(closeCourse4,"dept2",new DepartmentPrivateState());
//            latch4.await();
//            assertTrue("not all courses were removed from dept1 ",
//                    ((DepartmentPrivateState)threadPool.getPrivateState("dept1")).getCourseList().isEmpty());
//            assertTrue("not all courses were removed from dept2 ",
//                    ((DepartmentPrivateState)threadPool.getPrivateState("dept2")).getCourseList().isEmpty());
//            assertTrue("course1 didn't close ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course1")).getAvailableSpots()==-1);
//            assertTrue("course2 didn't close ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course2")).getAvailableSpots()==-1);
//            assertTrue("course3 didn't close ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course3")).getAvailableSpots()==-1);
//            assertTrue("course4 didn't close ",
//                    ((CoursePrivateState)threadPool.getPrivateState("course4")).getAvailableSpots()==-1);
//
//
//            threadPool.shutdown();
//
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void transactionTest() {
//        Transaction transaction = new Transaction(100, "A", "B", "Bank2");
//        threadPool.start();
//        threadPool.submit(new AddClient("A", 100, "Bank1"), "Bank1", new BankState());
//        threadPool.submit(new AddClient("B", 0, "Bank2"), "Bank2", new BankState());
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        threadPool.submit(transaction, "Bank1", new BankState());
//        try {
//            CountDownLatch latch = new CountDownLatch(1);
//            transaction.getResult().subscribe(() -> {
//                System.out.println("callback");
//                latch.countDown();
//            });
//            latch.await();
//            threadPool.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    private class AddClient extends Action<Boolean> {
        private String name;
        private int amount;
        private String bank;

        public AddClient(String name, int amount, String bank) {
            this.name = name;
            this.amount = amount;
            this.bank = bank;
            setActionName("AddClient " + name);
        }

        @Override
        protected void start() {
            System.out.println("#### " + getActionName() + ": start()");
            ((BankState) state).clients.add(new Pair<>(name, amount));
            System.out.println("added client " + name + " to bank " + bank);
            complete(true);
        }
    }

    private class Transaction extends Action<String> {
        int amount;
        String sender;
        String receiver;
        String receiverBank;

        public Transaction(int amount, String sender, String receiver, String receiverBank) {
            this.amount = amount;
            this.sender = sender;
            this.receiver = receiver;
            this.receiverBank = receiverBank;
            setActionName("Transaction");
        }

        @Override
        protected void start() {
            System.out.println("#### " + getActionName() + ": start()");
            Action<Boolean> confAction = new Confirmation(amount, sender, receiver, receiverBank, new BankState());
            Collection<Action<?>> requiredActions = new LinkedList<>();
            requiredActions.add(confAction);
            then(requiredActions,() ->{
                Boolean result = (Boolean) ((LinkedList<Action<?>>)requiredActions).get(0).getResult().get();
                if (result) {
                    System.out.println("Transaction Succeeded");
                    Pair<String, Integer> client = ((BankState) state).clients.remove(0);
                    ((BankState) state).clients.add(new Pair<>(client.getKey(), client.getValue() - amount));
                    complete("Transaction Succeeded");
                } else {
                    System.out.println("Transaction Failed");
                    complete("Transaction Failed");
                }
            });
            sendMessage(confAction, receiverBank, new BankState());

        }
    }

    private class Confirmation extends Action<Boolean> {
        int amount;
        String sender;
        String receiver;
        String receiverBank;

        public Confirmation(int amount, String sender, String receiver, String receiverBank, PrivateState privateState) {
            this.amount = amount;
            this.sender = sender;
            this.receiver = receiver;
            this.receiverBank = receiverBank;
            this.setActionName("Confirmation");
            privateState.addRecord(getActionName());
        }

        @Override
        protected void start() {
            System.out.println("#### " + getActionName() + ": start()");
            if (Math.random() < 0.5) {
                Pair<String, Integer> client = ((BankState) state).clients.remove(0);
                ((BankState) state).clients.add(new Pair<>(client.getKey(), client.getValue() + amount));
                System.out.println(receiverBank + " confirmed transaction from: " + sender + " to " + receiver);
                complete(true);
            } else {
                System.out.println(receiverBank + " unconfirmed transaction from: " + sender + " to " + receiver);
                complete(false);
            }
        }
    }

    private class BankState extends PrivateState {
        List<Pair<String, Integer>> clients = new ArrayList<>();

        @Override
        public String toString() {
            return clients.get(0).getKey() + ": " + clients.get(0).getValue();
        }
    }

    private class Pair<K, V> {

        final K element0;
        final V element1;


        public Pair(K element0, V element1) {
            this.element0 = element0;
            this.element1 = element1;
        }

        public K getKey() {
            return element0;
        }

        public V getValue() {
            return element1;
        }

    }
}