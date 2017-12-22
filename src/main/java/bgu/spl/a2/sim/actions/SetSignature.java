package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class SetSignature extends Action<Boolean> {
    private Long Signature;
    public SetSignature(Long Signature){
        setActionName("Set Signature");
        this.Signature = Signature;
    }
    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        state.setSignature(Signature);
        complete(true);
    }
}
