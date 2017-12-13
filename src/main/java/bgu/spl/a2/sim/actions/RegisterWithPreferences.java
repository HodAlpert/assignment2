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
        call = new callback() {
            @Override
            public void call() {
                if (currentRequest.getResult().get()) {//if current Request to register to course is successful
                    String[] result={Preferences[index.get()-1],Grade[index.get()-1]};
                    //it means that registration to course that was sent before index was inced was successful
                    complete(result);//complete result will be the {course,grade}
                }//if
                else {//if registration to course in index was not successful
                    if(index.get()<Preferences.length) {
                        currentRequest = new ParticipateInCourse(Student, Preferences[index.get()], Grade[index.get()]);
                        List<Action<Boolean>> actions1 = new ArrayList<>();
                        sendMessage(currentRequest, Preferences[index.get()], new CoursePrivateState());
                        then(actions1, call);
                        index.getAndIncrement();
                    }
                    else{//if there is no more courses to register to
                        String[] result={"-","-"};
                        complete(result);//completing with empty String
                    }
                }//else if registration to course in index was not successful

            }// public void call()
        };//new callback
    }//RegisterWithPreferences

    @Override
    protected void start() {
        StudentPrivateState State = (StudentPrivateState) getState();
        if (index.get() < Preferences.length) {//if there a course in current index
            Action<Boolean> registerToCourse = new ParticipateInCourse(Student, Preferences[index.get()], Grade[index.get()]);
            List<Action<Boolean>> actions = new ArrayList<>();
            sendMessage(registerToCourse, Preferences[index.get()], new CoursePrivateState());
            then(actions, call);
            index.getAndIncrement();
        } else {
            String[] result = {"-", "-"};
            complete(result);//completing with empty String
        }
//    private List<Action<Boolean>> actions(int index){
//
//    }
    }
}
