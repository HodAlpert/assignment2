package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterWithPreferences extends Action<String[]> {
    private String Student;
    private String[] Preferences;
    private String[] Grade;
    private AtomicInteger index = new AtomicInteger(0);
    private callback call;
    private ParticipateInCourse currentRequest;

    public RegisterWithPreferences(String Student, String[] Preferences, String[] Grade) {
        setActionName("Register With Preferences");
        this.Student = Student;
        this.Preferences = Preferences;
        this.Grade = Grade;
        // public void call()
        call = () -> {
            if (currentRequest.getResult().isResolved()&& (!currentRequest.getResult().get()[0].equals("-"))) {//if current Request to register to course is successful
                complete(currentRequest.getResult().get());//complete result will be the {course,grade}
            }//if
            else if(currentRequest.getResult().get()[0].equals("-")){//if registration to course in index was not successful
                if(index.get()<Preferences.length) {
                    String[] grade = {Grade[index.get()]};
                    currentRequest = new ParticipateInCourse(Student, Preferences[index.get()], grade);
                    List<Action<String[]>> actions1 = new ArrayList<>();
                    currentRequest.getResult().subscribe(()->index.getAndIncrement());
                    actions1.add(currentRequest);
                    then(actions1, call);
                    sendMessage(currentRequest, Preferences[index.get()], new CoursePrivateState());
                }
                else{//if there is no more courses to register to
                    String[] result={"-","-"};
                    complete(result);//completing with empty String
                }
            }//else if registration to course in index was not successful

        };//new callback
    }//RegisterWithPreferences

    @Override
    protected void start() {
//        StudentPrivateState State = (StudentPrivateState) getState(); //TODO not used
        if (index.get() < Preferences.length) {//if there is a course in current index
            String[] grade = {Grade[index.get()]};
            currentRequest = new ParticipateInCourse(Student, Preferences[index.get()], grade);
            List<Action<String[]>> actions = new ArrayList<>();
            actions.add(currentRequest);
            currentRequest.getResult().subscribe(()->index.getAndIncrement());
            then(actions, call);
            sendMessage(currentRequest, Preferences[index.get()], new CoursePrivateState());
        } else {
            String[] result = {"-", "-"};
            complete(result);//completing with empty String
        }
//    private List<Action<Boolean>> actions(int index){
//
//    }
    }
}
