package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class Transmission extends Action<String> {
    int amount;
    String sender;
    String receiver;
    String receiverBank;
    String senderBank;

    public Transmission(int amount, String receiver,String sender,  String receiverBank, String senderBank){
        this.amount=amount;
        this.sender=sender;
        this.receiver=receiver;
        this.receiverBank=receiverBank;
        this.senderBank=senderBank;
    }
    @Override
    protected void start() {
        List<Action<Boolean>> actions=new ArrayList<>();
        Action<Boolean> confAction=new Confirmation(sender, receiver, receiverBank, new StudentPrivateState());
        actions.add(confAction);
        sendMessage(confAction,receiverBank, new StudentPrivateState());
        then(actions, ()->
        {Boolean result=actions.get(0).getResult().get();
        if (result==true){
            complete("transmission succeeded");
            System.out.println("transmission succeeded");
        }
        else{
            complete("transmission failed");
            System.out.println("transmission failed");
        }
        });
    }


}
