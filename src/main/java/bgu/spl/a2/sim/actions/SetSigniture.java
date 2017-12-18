package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class SetSigniture extends Action<Boolean> {
    private Long Signiture;
    public SetSigniture(Long Signiture){
        this.Signiture = Signiture;
    }
    @Override
    protected void start() {
        StudentPrivateState state = (StudentPrivateState) getState();
        state.setSignature(Signiture);
        complete(true);
    }
}
