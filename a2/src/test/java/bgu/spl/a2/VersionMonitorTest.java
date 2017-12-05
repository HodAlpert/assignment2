package bgu.spl.a2;

import org.junit.*;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class VersionMonitorTest {

    private VersionMonitor tester;
    private boolean flag = false;

    @BeforeClass
    public static void BeforeClass() throws Exception {
        System.out.println("Start testing VersionMonitor");
    }
    @AfterClass
    public static void AfterClass() throws Exception {
        System.out.println("Finish testing VersionMonitor");
    }
    @Before
    /**version==0
     * */
    public void setUp() throws Exception {
        tester = new VersionMonitor();
    }
    @After
    public void tearDown() throws Exception {
    }
/**getVersion()==version;
 * */
    @Test
    public void getVersion() {
        assertEquals(true, tester.getVersion() == 0);
    }
/**getVersion= @pre {@link #getVersion()}+1
 * should awake all threads waiting.
 * */
    @Test
    public void inc() {
        int preVersion = tester.getVersion();
        tester.inc();
        assertEquals(preVersion+1, tester.getVersion());
    }

    @Test
    /**#version>=0;
     *
     * */
    public void await() {

        Thread t1 = new Thread(() -> {
            try {
                tester.await(tester.getVersion());
            } catch (InterruptedException e) {
                fail("await was interrupted!");
            }
            assertTrue("Thread did not wait",flag);
        });

        t1.start();
        while(t1.getState()!= Thread.State.WAITING) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        flag=true;
        tester.inc();
        assertTrue("Thread was not notified when version number changed",t1.getState()!= Thread.State.WAITING);

    }
}