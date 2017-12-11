package bgu.spl.a2;

import org.junit.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertTrue;

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

    public void submit() {

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