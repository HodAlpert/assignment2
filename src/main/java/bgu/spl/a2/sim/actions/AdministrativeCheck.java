package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdministrativeCheck extends Action<Boolean> {

    private String Department;
    private String[] Students;
    private String Computer;
    private ArrayList<String> Conditions;
    private ArrayList<HashMap<String, Integer>> studentsGrades;
    private ArrayList<Long> signatures;
    private List<Action<HashMap<String, Integer>>> actions;
    private Promise<Computer> promise;
    private Warehouse warehouse = Warehouse.getInstance();

    private callback ContinuationAfterGettingTheSignatures=()->{//setting the continuation once we have all the signitures
        List<Action<Boolean>> actions1 = new ArrayList<>();
        for (int i=0;i<Students.length;i++){//creating the signatures back to the students
            actions1.add(i,new SetSignature(signatures.get(i)));
        }//for saving the signatures in the students privateState
        then(actions1,()->complete(true));//setting the final continuation- completing.
        for(int i=0;i<Students.length;i++){
            sendMessage(actions1.get(i),Students[i],new StudentPrivateState());//sending the signitures to the students
        }//for
    };//continuation once we have all the signatures

    private callback computeSignatureWithComputer =()->{//getting the signatures of the students with the promise
        for(int i=0;i<Students.length;i++){
            Long signature =promise.get().checkAndSign(Conditions,studentsGrades.get(i));//computing signiture
            signatures.add(i,signature);//adding signature to signature list
        }//for computing signature
        warehouse.getComputer(Computer).getMutex().up();
        ArrayList<Action<Boolean>> emptyAction = new ArrayList<>();
        then(emptyAction,ContinuationAfterGettingTheSignatures);
        getPool().submit(this,Department,new DepartmentPrivateState());//submiting back to the pool
    };

    private callback ContinuationAfterGettingTheGradesMaps = ()->{//setting the continuation once we have the grades of the students
        for(int i=0;i<actions.size();i++){//saving all the grades of the students in studentsGrades
            studentsGrades.add(actions.get(i).getResult().get());
        }//for saving the grades
        promise= warehouse.getComputer(Computer).getMutex().down();//getting the computer
        promise.subscribe(computeSignatureWithComputer);//subscribe to the promise
    };//continuation once we have the grades of the students





    public AdministrativeCheck(String department, String[] Students, String Computer, ArrayList<String> Conditions){
        setActionName("Administrative Check");
        this.Department = department;
        this.Students = Students;
        this.Computer = Computer;
        this.Conditions = Conditions;
        studentsGrades = new ArrayList<>();
        this.signatures = new  ArrayList<>();
        actions = new ArrayList<>();

    }
    @Override
    protected void start() {
        for(int i=0;i<Students.length;i++){
            actions.add(new getStudentGrade());//creating actions list to submit and to pass to then
        }//for creating actions
        then(actions,ContinuationAfterGettingTheGradesMaps);//setting the continuation once we have the grades of the students
        for (int i=0;i<Students.length;i++){
            sendMessage(actions.get(i),Students[i],new StudentPrivateState());
        }//for sending messages
    }//start
}
