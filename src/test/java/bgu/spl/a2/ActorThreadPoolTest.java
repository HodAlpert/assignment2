package bgu.spl.a2;

import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.actions.Confirmation;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.*;

import static org.junit.Assert.*;

public class ActorThreadPoolTest {

    private ActorThreadPool tester;

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
        //tester.submit(new Confirmation("sender","receiver","bank",new StudentPrivateState()),"id",new StudentPrivateState());
    }

    @Test
    public void start() {
        int activeThreads = Thread.activeCount();
        tester.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("All or part of the Threads were not activated",activeThreads+3,Thread.activeCount());
    }

    @Test
    public void shutdown() throws InterruptedException {
        tester.start();
        Thread.sleep(3000);
        int activeThreads = Thread.activeCount();
        tester.shutdown();
        Thread.sleep(3000);
        assertEquals("All or part of the Threads were not terminated",activeThreads-3,Thread.activeCount());
    }


}