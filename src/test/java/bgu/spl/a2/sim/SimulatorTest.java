package bgu.spl.a2.sim;

import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;

/**
 * @author nadav.
 */
public class SimulatorTest  {
//    @Rule
//    public Timeout globalTimeout = Timeout.seconds(10); // 10 seconds max per method tested
    @Test
    public void main() {
        //todo change the name of the file in the next line for the name you have
        Simulator.main(new String[]{"input.json"});
        try ( InputStream fin = new FileInputStream("result.ser");
              ObjectInputStream ois = new ObjectInputStream(fin)){
            HashMap data = (HashMap) ois.readObject();
            data.forEach((actor ,state)->{
                System.out.println(actor+": ");
                System.out.print("History: ");
                ((PrivateState)state).getLogger().forEach((String s) -> {System.out.print(s+", ");});
                System.out.println("");
                if (state instanceof DepartmentPrivateState) {
                    System.out.print("Courses: ");
                    ((DepartmentPrivateState)state).getCourseList().forEach((String s) -> {System.out.print(s+", ");});
                    System.out.print('\n'+"Students: ");
                    ((DepartmentPrivateState)state).getStudentList().forEach((String s) -> {System.out.print(s+", ");});
                    System.out.println("");
                    if (actor.equals("CS")){
                        assertTrue("Department"+actor+": should have 3 open course action in logger", Collections.frequency(((PrivateState)state).getLogger(),"Open Course")==3);
                        assertTrue("Department"+actor+": should have 2 Add student action in logger", Collections.frequency(((PrivateState)state).getLogger(),"Add Student")==2);
                        assertTrue("Department"+actor+": should have 1 Administrative Check in logger", Collections.frequency(((PrivateState)state).getLogger(),"Administrative Check")==1);
                        assertTrue("Department"+actor+": not all courses appear in courselist " +
                                " should have: Intro To CS, Data Structures, SPL",
                                ((DepartmentPrivateState)state).getCourseList().containsAll(new ArrayList<String>(Arrays.asList("Intro To CS","Data Structures","SPL"))));
                        assertTrue("Department"+actor+": not all students appear in studentslist "+
                                " should have: 123456789,5959595959",
                                ((DepartmentPrivateState)state).getStudentList().containsAll(new ArrayList<String>(Arrays.asList("123456789","5959595959"))));
                    }
                    else if (actor.equals("Math")){
                        assertTrue("Department"+actor+": should have 1 Add student action in logger", Collections.frequency(((PrivateState)state).getLogger(),"Add Student")==1);
                        assertTrue("Department"+actor+": courselist should be empty",
                                ((DepartmentPrivateState)state).getCourseList().isEmpty());
                        assertTrue("Department"+actor+": not all students appear in studentslist "+
                                        " should have: 132424353",
                                ((DepartmentPrivateState)state).getStudentList().containsAll(new ArrayList<String>(Arrays.asList("132424353"))));
                    }
                } else if (state instanceof StudentPrivateState) {
                    System.out.print("Grades: ");
                    ((StudentPrivateState)state).getGrades().forEach((String s,Integer grade) -> {System.out.print(s+": "+grade+", ");});
                    System.out.print('\n'+"Signature: ");
                    System.out.println(((StudentPrivateState)state).getSignature());
                    if (actor.equals("123456789")){
                        assertTrue("student "+ actor+": should be registered to Intro To CS with grade 77",
                                ((StudentPrivateState)state).getGrades().containsKey("Intro To CS")&&((StudentPrivateState)state).getGrades().get("Intro To CS")==77);
                        assertTrue("student "+ actor+": signiture should be 999283",((StudentPrivateState)state).getSignature()==999283);
                    }
                    else if (actor.equals("5959595959")){
                        assertTrue("student "+ actor+": should be registered to Intro To CS with grade 94",
                                ((StudentPrivateState)state).getGrades().containsKey("Intro To CS")&&((StudentPrivateState)state).getGrades().get("Intro To CS")==94);
                        assertTrue("student "+ actor+": should be registered to SPL with grade 100",
                                ((StudentPrivateState)state).getGrades().containsKey("SPL")&&((StudentPrivateState)state).getGrades().get("SPL")==100);
                        assertTrue("student "+ actor+": signiture should be 999283",((StudentPrivateState)state).getSignature()==999283);
                    }
                    else if (actor.equals("132424353")){
                        assertTrue("student "+ actor+": grades should be empty",
                                ((StudentPrivateState)state).getGrades().isEmpty());
                        assertTrue("student "+ actor+": signiture should be 0",((StudentPrivateState)state).getSignature()==0);
                    }
                }
                else {
                    System.out.print("prequisites: ");
                    ((CoursePrivateState)state).getPrequisites().forEach((String s) -> {System.out.print(s+", ");});
                    System.out.print('\n'+"students: ");
                    ((CoursePrivateState)state).getRegStudents().forEach((String s) -> {System.out.print(s+", ");});
                    System.out.print('\n'+"Registered: ");
                    System.out.println(((CoursePrivateState)state).getRegistered());
                    System.out.print("available spaces: ");
                    System.out.println(((CoursePrivateState)state).getAvailableSpots());
                    if(actor.equals("Intro To CS")){
                        assertTrue("Course "+actor+": should have 2 Participate In Course action in logger", Collections.frequency(((PrivateState)state).getLogger(),"Participate In Course")==2);
                        assertTrue("Course "+actor+": availableSpots should be 198",((CoursePrivateState)state).getAvailableSpots()==198);
                        assertTrue("Course "+actor+": should have 2 registered students",((CoursePrivateState)state).getRegistered()==2);
                        assertTrue("Course "+actor+": not all students appear in regStudents list "+
                                        " should have: 123456789, 5959595959",
                                ((CoursePrivateState)state).getRegStudents().containsAll(new ArrayList<String>(Arrays.asList("123456789","5959595959"))));
                        assertTrue("Course "+actor+": should have no prerequisites",
                                ((CoursePrivateState)state).getPrequisites().isEmpty());
                    }
                    else if(actor.equals("SPL")){
                        assertTrue("Course "+actor+": should have 2 Participate In Course action in logger", Collections.frequency(((PrivateState)state).getLogger(),"Participate In Course")==2);
                        assertTrue("Course "+actor+": availableSpots should be 0",((CoursePrivateState)state).getAvailableSpots()==0);
                        assertTrue("Course "+actor+": should have 1 registered students",((CoursePrivateState)state).getRegistered()==1);
                        assertTrue("Course"+actor+": not all students appear in regStudents list "+
                                        " should have: 5959595959",
                                ((CoursePrivateState)state).getRegStudents().contains("5959595959"));
                        assertTrue("Course"+actor+": not all prerequisites appear, should have Intro To CS",
                                ((CoursePrivateState)state).getPrequisites().contains("Intro To CS"));
                    }
                    else if(actor.equals("Data Structures")){
                        assertTrue("Course "+actor+": should have 1 Participate In Course action and" +
                                " 1 Unregister in logger", ((PrivateState)state).getLogger().containsAll(new ArrayList<String>(Arrays.asList("Participate In Course","Unregister"))));
                        assertTrue("Course "+actor+": availableSpots should be 100",((CoursePrivateState)state).getAvailableSpots()==100);
                        assertTrue("Course "+actor+": should have 0 registered students",((CoursePrivateState)state).getRegistered()==0);
                        assertTrue("Course"+actor+": should have no regStudents",
                                ((CoursePrivateState)state).getRegStudents().isEmpty());
                        assertTrue("Course"+actor+": not all prerequisites appear, should have Intro To CS",
                                ((CoursePrivateState)state).getPrequisites().contains("Intro To CS"));
                    }
                }
                System.out.println("----------------");
            });

            System.out.println(data.keySet());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

        }
    }
}