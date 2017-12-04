package bgu.spl.a2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionMonitorTest {
/**version==0
 * */
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
/**getVersion()==version;
 * */
    @Test
    public void getVersion() {

    }
/**getVersion= @pre {@link #getVersion()}+1
 * should awake all threads waiting.
 * */
    @Test
    public void inc() {
    }

    @Test
    /**#version>=0;
     *
     * */
    public void await() {
    }
}