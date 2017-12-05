package bgu.spl.a2;

import org.junit.*;

import static org.junit.Assert.*;

public class PromiseTest {
/**
 * INV: isResolved()==false;
 * */
    private boolean beforeResolving;
    private boolean afterResolving;
    @Before
    public void setUp() throws Exception {
        Promise<Integer> promise=new Promise<Integer>();
        assertFalse("isResolved should be false",promise.isResolved());
    }

    @After
    public void tearDown() throws Exception {
    }
    @Test
    /**
     * pre: if (@isResolved()!=false)
     *      throw {@link IllegalStateException}
     * if resolved- should return value;
     * */
    public void get() {
        Promise<Integer> promise=new Promise<Integer>();
        try{
            promise.get();
            Assert.fail("should throw IllegalStateException if 'get' is called before 'resolve'");
        }
        catch (IllegalStateException ex){
            promise.resolve(5);
            assertEquals("returnd value is incorrect",5, java.util.Optional.ofNullable(promise.get()));
        }
        catch(Exception ex){
            Assert.fail(ex.getMessage());
        }

    }

    @Test
    /**should return false after initiation
     * should return true after {@link #resolve(java.lang.Object)} is called.
     * */
    public void isResolved() {
        Promise<Integer> promise = new Promise<Integer>();
        assertFalse("isResolved should be false on initiation",promise.isResolved());
        promise.resolve(5);
        assertTrue("isResolved should be true after #resolve is called",promise.isResolved());

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
        try {
            Promise<Integer> promise = new Promise<Integer>();
            promise.resolve(5);
            try {
                promise.resolve(6);
                Assert.fail("resolve should only be called once");
            } catch (IllegalStateException ex) {
                int x = promise.get();
                assertEquals("value returned is not currect",x, 5);
            } catch (Exception ex) {
                Assert.fail(ex.getMessage());
            }
            try {
                assertTrue("isResolved() should be true after #resolve() is called",promise.isResolved());
            } catch (Exception ex) {
                Assert.fail(ex.getMessage());
            }

        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
    /** callback !=null;
     *if (@isResolved())
     *     callback should be called immidietly
     * */
    @Test
    public void subscribe() {
        Promise<Integer> promise = new Promise<Integer>();
        try {
            promise.subscribe(null);
            Assert.fail("cannot subscribe a null callback");
        }
        catch (NullPointerException ex){
            Assert.fail(ex.getMessage());
        }
        this.beforeResolving=false;
        promise.subscribe(()->{
            this.beforeResolving=true;
        });
        assertFalse("callback should be executed onlt after promise is resolved",beforeResolving);
        promise.resolve(5);
        assertTrue("callback should be resolved after promise is resolved",beforeResolving);
        this.afterResolving=false;
        promise.subscribe(()->{
            this.afterResolving=true;
        });
        assertTrue("callback should be resolved after promise is resolved",afterResolving);


    }

}
