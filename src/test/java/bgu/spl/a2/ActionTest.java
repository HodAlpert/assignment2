package bgu.spl.a2;

import bgu.spl.a2.sim.actions.naiveAction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActionTest {

    private Action tester;
    private ActorThreadPool pool;

    @BeforeClass
    public static void BeforeClass() throws Exception {
        System.out.println("Start testing Action class");
    }
    @AfterClass
    public static void AfterClass() throws Exception {
        System.out.println("Finish testing Action class");
    }

    @Before
    public void setUp() throws Exception {
        tester = new naiveAction();
    }

    @Test
    public void handle() {
    }

    @Test
    public void then() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void getResult() {
    }

    @Test
    public void sendMessage() {
    }
}