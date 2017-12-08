package bgu.spl.a2;

import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.actions.Confirmation;
import bgu.spl.a2.sim.actions.naiveAction;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ActorThreadPoolTest {

    private ActorThreadPool tester;
    AtomicInteger counter= new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(8);


    @BeforeClass
    public static void BeforeClass() throws Exception {
        System.out.println("Start testing ActorThreadPool class");
    }
    @AfterClass
    public static void AfterClass() throws Exception {
        System.out.println("Finish testing ActorThreadPool class");
    }
    @Before
    public void setUp() throws Exception {
        tester = new ActorThreadPool(3);
    }

    @Test

    public void submit() { //TODO write private states first.
        tester.start();
        naiveAction action = new naiveAction();
        action.getResult().subscribe(() -> {this.counter.getAndIncrement();
        latch.countDown();});
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isWaitingAndAlive();
        tester.submit(action, "something", new CoursePrivateState());
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isWaitingAndAlive();
        assertTrue("action not completed",counter.get()==1&action.getResult().isResolved());
        naiveAction action2 = new naiveAction();
        action2.getResult().subscribe(() -> {this.counter.getAndIncrement();
            latch.countDown();});
        isWaitingAndAlive();
        tester.submit(action2, "something", new CoursePrivateState());
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isWaitingAndAlive();
        assertTrue("action not completed",latch.getCount()==6&action2.getResult().isResolved());
        assertTrue("should not create actor again",tester.queues.size()==1);
        naiveAction action3 = new naiveAction();
        naiveAction action4 = new naiveAction();
        naiveAction action5 = new naiveAction();
        naiveAction action6 = new naiveAction();
        naiveAction action7 = new naiveAction();
        naiveAction action8 = new naiveAction();
        action3.getResult().subscribe(() -> this.latch.countDown());
        action4.getResult().subscribe(() -> this.latch.countDown());
        action5.getResult().subscribe(() -> this.latch.countDown());
        action6.getResult().subscribe(() -> this.latch.countDown());
        action7.getResult().subscribe(() -> this.latch.countDown());
        action8.getResult().subscribe(() -> this.latch.countDown());
        tester.submit(action3, "something", new CoursePrivateState());
        tester.submit(action4, "something", new CoursePrivateState());
        tester.submit(action5, "something", new CoursePrivateState());
        tester.submit(action6, "somethinsEntirelyElse", new CoursePrivateState());
        tester.submit(action7, "somethinsEntirelyElse", new CoursePrivateState());
        tester.submit(action8, "somethingElse", new CoursePrivateState());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("action Counter is incorrect",latch.getCount()==0);
        assertTrue("should have 3 qeueus",tester.queues.size()==3);




    }


    @After
    public void finish() throws Exception {
    }


    @Test
    //check that threads running count is OK
    public void start() {
        tester.start();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(tester.threads.size()==3){
            isWaitingAndAlive();
        }//tester size
        try {
            tester.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }//start


    @Test
    //checks that all threads have been terminated
    public void shutdown() throws InterruptedException {
        tester.start();
        sleep(1000);
        isWaitingAndAlive();
        tester.shutdown();
        sleep(1000);
        for (Thread thread: tester.threads){
            assertTrue("thread"+ thread.getId()+ "  alive",!thread.isAlive());
            assertTrue("thread"+ thread.getId()+ "not terminated",thread.getState()== Thread.State.TERMINATED);
        }//for
    }
    private void isWaitingAndAlive() {
        for (Thread thread : tester.threads) {
            try {
                assertTrue("thread" + thread.getId() + "not waiting", thread.getState() == Thread.State.WAITING);
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }


}