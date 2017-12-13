package bgu.spl.a2;

import bgu.spl.a2.sim.actions.AddStudent;
import bgu.spl.a2.sim.actions.OpenCourse;
import bgu.spl.a2.sim.actions.ParticipateInCourse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ActionTest {

    @Parameterized.Parameters
    public static Collection parameters() {
        Object[][] params = new Object[200][];
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
    public void CourseAction(){
        Action openCourse1 = new OpenCourse("dept1","course1","10",new String[]{});
        Action openCourse2 = new OpenCourse("dept1","course2","10",new String[]{});
        Action openCourse3 = new OpenCourse("dept2","course3","10",new String[]{});
        Action openCourse4 = new OpenCourse("dept2","course4","10",new String[]{});
        Action addStudent1 = new AddStudent("dept1","1");
        Action addStudent2 = new AddStudent("dept1","2");
        Action addStudent3 = new AddStudent("dept2","3");
        Action Participate1 = new ParticipateInCourse("1","course1",new String[]{"-"});
        Action Participate2 = new ParticipateInCourse("1","course2",new String[]{"100"});
        Action Participate3 = new ParticipateInCourse("2","course2",new String[]{"50"});



        threadPool.start();
        threadPool.submit(openCourse1,"dept1",new DepartmentPrivateState());
        threadPool.submit(openCourse2,"dept1",new DepartmentPrivateState());
        threadPool.submit(openCourse3,"dept2",new DepartmentPrivateState());
        threadPool.submit(openCourse4,"dept2",new DepartmentPrivateState());

        try {
            CountDownLatch latch = new CountDownLatch(4);
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
            openCourse4.getResult().subscribe(() -> {
                System.out.println("openCourse4 done");
                latch.countDown();
            });

            latch.await();

            threadPool.submit(addStudent1,"dept1",new DepartmentPrivateState());
            threadPool.submit(addStudent2,"dept1",new DepartmentPrivateState());
            threadPool.submit(addStudent3,"dept2",new DepartmentPrivateState());
            threadPool.submit(Participate1,"course1",new StudentPrivateState());
            threadPool.submit(Participate2,"course2",new StudentPrivateState());
            threadPool.submit(Participate3,"course2",new StudentPrivateState());


            CountDownLatch latch1 = new CountDownLatch(6);
            addStudent1.getResult().subscribe(() -> {
                System.out.println("addStudent1 done");
                latch1.countDown();
            });
            addStudent2.getResult().subscribe(() -> {
                System.out.println("addStudent2 done");
                latch1.countDown();
            });
            addStudent3.getResult().subscribe(() -> {
                System.out.println("addStudent3 done");
                latch1.countDown();
            });
            Participate1.getResult().subscribe(() -> {
                System.out.println("Participate1 done");
                latch1.countDown();
            });
            Participate2.getResult().subscribe(() -> {
                System.out.println("Participate2 done");
                latch1.countDown();
            });
            Participate3.getResult().subscribe(() -> {
                System.out.println("Participate3 done");
                latch1.countDown();
            });

            latch1.await();

            threadPool.shutdown();

            assertTrue("not all courses are in dept1",
                    ((DepartmentPrivateState)threadPool.getPrivaetState("dept1")).getCourseList().containsAll(Arrays.asList(new String[]{"course1","course2"})));
            assertTrue("not all courses are in dept2",
                    ((DepartmentPrivateState)threadPool.getPrivaetState("dept2")).getCourseList().containsAll(Arrays.asList(new String[]{"course3","course4"})));
            assertTrue("not all students are in dept1",
                    ((DepartmentPrivateState)threadPool.getPrivaetState("dept1")).getStudentList().containsAll(Arrays.asList(new String[]{"1","2"})));
            assertTrue("not all students are in dept2",
                    ((DepartmentPrivateState)threadPool.getPrivaetState("dept2")).getStudentList().containsAll(Arrays.asList(new String[]{"3"})));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

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