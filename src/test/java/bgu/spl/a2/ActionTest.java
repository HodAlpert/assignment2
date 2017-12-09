package bgu.spl.a2;

import bgu.spl.a2.sim.actions.naiveAction;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

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
        pool = new ActorThreadPool(3);
    }


    @Test
    public void then() {
        CountDownLatch latch = new CountDownLatch(5);

        Action a1 = new naiveAction();
        Action a2 = new naiveAction();
        Action a3 = new naiveAction();
        Action a4 = new naiveAction();
        Action a5 = new naiveAction();

        Collection<Action> actions = new LinkedList<>();
        actions.add(a2);
        actions.add(a3);
        actions.add(a4);
        actions.add(a5);
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            a1.then(actions, () -> {
                System.out.println("callback" + finalI);
                latch.countDown();
            });
        }
        a2.getResult().resolve(new Object());
        a3.getResult().resolve(new Object());
        a4.getResult().resolve(new Object());
        a5.getResult().resolve(new Object());

        pool.start();
        pool.submit(a1,"1",new CoursePrivateState());
        pool.submit(a2,"1",new CoursePrivateState());
        pool.submit(a3,"1",new CoursePrivateState());
        pool.submit(a4,"1",new CoursePrivateState());
        pool.submit(a5,"1",new CoursePrivateState());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

//    @Test
//    public void handle() {
//    }
//
//    @Test
//    public void complete() {
//    }
//
//    @Test
//    public void getResult() {
//    }
//
//    @Test
//    public void sendMessage() {
//    }
}