package bgu.spl.a2;

import org.junit.*;

import static org.junit.Assert.*;

public class PromiseTest {
/**
 * INV: isResolved()==false;
 * */
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    @Test
    /**
     * pre: if (@isResolved()!=false)
     *      throw {@link IllegalStateException}
     * if resulved- should return value;
     * */
    public void get() {
    }

    @Test
    /**should return false after initiation
     * should return true after {@link #resolve(java.lang.Object)} is called.
     * */
    public void isResolved() {
    }

    /**
     * pre: if (@isResolved()!=false)
     *      throw {@link IllegalStateException}
     *  value != null;
     *  post @get()==value;
     *  post @isResolved()==true;
     *  <callbacks> should be empty
     *  * */
    @Test
    public void resolve() {
    }
    /** callback !=null;
     *if (@isResolved())
     *     callback should be called immidietly
     * */
    @Test
    public void subscribe() {
    }
}