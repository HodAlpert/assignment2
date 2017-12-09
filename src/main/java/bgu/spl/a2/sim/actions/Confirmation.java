package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.PrivateState;

public class Confirmation extends Action<Boolean> {
    private String sender;
    private String receiver;
    private String receiverBank;
    private PrivateState state;

    public Confirmation(String sender, String receiver, String receiverBank, PrivateState state){
        this.sender=sender;
        this.receiver=receiver;
        this.receiverBank=receiverBank;
        this.state=state;
    }
    @Override
    protected void start() {

    }



}
