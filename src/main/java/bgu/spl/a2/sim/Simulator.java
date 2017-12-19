/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	
	public static ActorThreadPool actorThreadPool;
	
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
		actorThreadPool.start();
    }
	
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool = myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	* returns list of private states
	*/
	public static HashMap<String,PrivateState> end(){
		try {
			actorThreadPool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return (HashMap<String, PrivateState>) actorThreadPool.getActors();

	}


	public static void main(String [] args){ //TODO return int
		JSONParser parser = new JSONParser();
		Warehouse warehouse = Warehouse.getInstance();

		try {
			Object obj = parser.parse(new FileReader(args[0]));

			JSONObject input = (JSONObject) obj;

			long threads = (long) input.get("threads");
			attachActorThreadPool(new ActorThreadPool((int) threads));

			JSONArray Computers = (JSONArray) input.get("Computers");
			JSONArray Phase1 = (JSONArray) input.get("Phase 1");
			JSONArray Phase2 = (JSONArray) input.get("Phase 2");
			JSONArray Phase3 = (JSONArray) input.get("Phase 3");

			createComputers(warehouse,Computers);

			actorThreadPool.start();
			submitPhase(Phase1);
			submitPhase(Phase2);
			submitPhase(Phase3);

			HashMap<String,PrivateState> output = end();
			FileOutputStream out = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(output);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void createComputers(Warehouse werehouse, JSONArray Computers){
		for (Object curr : Computers) {
			JSONObject jsonLineItem = (JSONObject) curr;
			String Type = (String) jsonLineItem.get("Type");
			String successSig = (String) jsonLineItem.get("Sig Success");
			String failSig = (String) jsonLineItem.get("Sig Fail");

			werehouse.addComputer(new Computer(Type,Long.parseLong(successSig),Long.parseLong(failSig)));
		}
	}

	private static void submitPhase(JSONArray Phase){
		CountDownLatch latch = new CountDownLatch(Phase.size());
		for (Object curr : Phase) {
			JSONObject jsonLineItem = (JSONObject) curr;
			Action action = getAction(jsonLineItem);
			if(action instanceof OpenCourse)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Department"),new DepartmentPrivateState());
			else if(action instanceof AddStudent)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Department"),new DepartmentPrivateState());
			else if(action instanceof ParticipateInCourse)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Course"),new CoursePrivateState());
			else if(action instanceof Unregister)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Course"),new CoursePrivateState());
			else if(action instanceof RegisterWithPreferences)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Student"),new StudentPrivateState());
			else if(action instanceof CloseCourse)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Department"),new DepartmentPrivateState());
			else if(action instanceof AddSpaces)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Course"),new CoursePrivateState());
			else if(action instanceof AdministrativeCheck)
				actorThreadPool.submit(action,(String)jsonLineItem.get("Department"),new DepartmentPrivateState());
			action.getResult().subscribe(() -> latch.countDown());
		}

	}

	private static Action getAction(JSONObject jsonLineItem){
		if(jsonLineItem.get("Action").equals("Open Course"))
			return new OpenCourse((String) jsonLineItem.get("Department"),(String)jsonLineItem.get("Course"),(String)jsonLineItem.get("Space"),toArray((JSONArray)jsonLineItem.get("Prerequisites")));
		else if(jsonLineItem.get("Action").equals("Add Student"))
			return new AddStudent((String)jsonLineItem.get("Department"),(String)jsonLineItem.get("Student"));
		else if(jsonLineItem.get("Action").equals("Participate In Course"))
			return new ParticipateInCourse((String)jsonLineItem.get("Student"),(String)jsonLineItem.get("Course"),toArray((JSONArray)jsonLineItem.get("Grade")));
		else if(jsonLineItem.get("Action").equals("Unregister"))
			return new Unregister((String)jsonLineItem.get("Student"),(String)jsonLineItem.get("Course"));
		else if(jsonLineItem.get("Action").equals("Register With Preferences"))
			return new RegisterWithPreferences((String)jsonLineItem.get("Student"),toArray((JSONArray)jsonLineItem.get("Preferences")),toArray((JSONArray)jsonLineItem.get("Grade")));
		else if(jsonLineItem.get("Action").equals("Close Course"))
			return new CloseCourse((String)jsonLineItem.get("Department"),(String)jsonLineItem.get("Course"));
		else if(jsonLineItem.get("Action").equals("Add Spaces"))
			return new AddSpaces((String)jsonLineItem.get("Course"),Integer.parseInt((String)jsonLineItem.get("Number")));
		else if(jsonLineItem.get("Action").equals("Administrative Check"))
			return new AdministrativeCheck((String)jsonLineItem.get("Department"),toArray((JSONArray)jsonLineItem.get("Students")),(String)jsonLineItem.get("Computer"),toArrayList((JSONArray)jsonLineItem.get("Conditions")));

		return null; // if input is incorrect
	}

	private static String[] toArray(JSONArray jsonArray){
		String[] output = new String[jsonArray.size()];
		for(int i=0;i<output.length;i++)
			output[i] = jsonArray.get(i).toString();
		return output;
	}
	private static ArrayList<String> toArrayList(JSONArray jsonArray){
		ArrayList<String> output = new ArrayList<>();
		for(int i=0;i<jsonArray.size();i++)
			output.add(jsonArray.get(i).toString());
		return output;
	}
}
