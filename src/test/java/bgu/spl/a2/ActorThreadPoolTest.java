package bgu.spl.a2;

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
    public void submit() {

    }

    @Test
    public void shutdown() {
    }

    @Test
    public void start() {
    }
}