package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {
    private Promise<R> promise;
    private AtomicBoolean hasBeenStartedBefore;
    private AtomicInteger actionsCompletedCounter;
    protected String Action;
    private ActorThreadPool pool;
    protected PrivateState state; //TODO change back to private
    protected callback continuation; //TODO change back to private
    protected String actorid;
/**
 * initializing the fiels.
 * */
    public Action(){
        hasBeenStartedBefore = new AtomicBoolean(false);
        actionsCompletedCounter = new AtomicInteger(0);
        promise=new Promise<R>();

    }
    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();


    /**
     *
     * start/continue handling the action
     *
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * @param pool saving as field for future submit
     * @param actorId saving as field for future submit
     * @param actorState saving as field for future "sendMessage".
     * if has not been started before- calls start
     * else- calls the continuation.
     *
     */
    /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {

        if(actorState==null)//TODO remove
            throw new NullPointerException("whattt");//TODO remove
        if (!promise.isResolved()){
            if (!hasBeenStartedBefore.get()){
                hasBeenStartedBefore.set(true);
                this.pool=pool;
                this.state=actorState;
                this.actorid=actorId;
                start();
            }//if not been started before
            else {
                continuation.call();
            }//else
        }//if not resolved
    }//handle


    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions that we wait for them to be completed
     * @param callback the callback to execute once all the results are resolved- saving it as a field and
     * {@link #handle(ActorThreadPool, String, PrivateState)} will see that it executes next time it will come out of the qeueu.
     *      using internal counter to make sure @param actions were completed
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
        continuation=callback;
        actionsCompletedCounter.set(0);
        for (Action action: actions){
            action.getResult().subscribe(()->{
                actionsCompletedCounter.getAndIncrement();
                if(actions.size()==actionsCompletedCounter.get()) {//if all actions in actions list are completed
                    pool.submit(this, actorid, state);//reenqeueu this
                }
            });
        }//for

        }//then

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        getResult().resolve(result);
        state.addRecord(Action);
    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        return promise;
    }

    /**
     * send an action to an other actor
     *
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
     * 				actor's private state (actor's information)
     *
     * @return promise that will hold the result of the sent action
     */
    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
        pool.submit(action,actorId,actorState);
        return action.getResult();
    }
    /**
     * compute the result of the action
     */
    /**
     * set action's name
     * @param actionName
     */
    public void setActionName(String actionName){
        this.Action=actionName;
    }

    /**
     * @return action's name
     */
    public String getActionName(){
        return this.Action;
    }
    protected PrivateState getState(){return this.state;}

    public ActorThreadPool getPool() {
        return pool;
    }
}
