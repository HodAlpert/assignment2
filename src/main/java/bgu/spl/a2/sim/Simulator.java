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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	
	public static ActorThreadPool actorThreadPool;
	
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){

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
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}


	public static void main(String [] args){ //TODO return int
		JSONParser parser = new JSONParser();
		Warehouse warehouse = new Warehouse();

		try {
			Object obj = parser.parse(new FileReader(args[0]));

			JSONObject input = (JSONObject) obj;
			long threads = (long) input.get("threads");
			JSONArray Computers = (JSONArray) input.get("Computers");
			JSONArray Phase1 = (JSONArray) input.get("Phase 1");
			JSONArray Phase2 = (JSONArray) input.get("Phase 2");
			JSONArray Phase3 = (JSONArray) input.get("Phase 3");

			createComputers(warehouse,Computers);

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

	private static List<Action> createPhase(JSONArray Phase){
		List<Action> output = new LinkedList<>();
		for (Object curr : Phase) {
			JSONObject jsonLineItem = (JSONObject) curr;
			output.add(getAction(jsonLineItem));
		}
		return output;
	}

	private static Action getAction(JSONObject jsonLineItem){
		if(jsonLineItem.get("Action").equals("Open Course"))
			return new OpenCourse((String)jsonLineItem.get("Department"),(String)jsonLineItem.get("Course"),(String)jsonLineItem.get("Space"),(String[])jsonLineItem.get("Prerequisites"));
		if(jsonLineItem.get("Action").equals("Add Student"))
			return new AddStudent((String)jsonLineItem.get("Department"),(String)jsonLineItem.get("Student"));
		if(jsonLineItem.get("Action").equals("Participate In Course"))
			return new ParticipateInCourse((String)jsonLineItem.get("Student"),(String)jsonLineItem.get("Course"),(String[])jsonLineItem.get("Grade"));
		if(jsonLineItem.get("Action").equals("Unregister"))
			return new Unregister((String)jsonLineItem.get("Student"),(String)jsonLineItem.get("Course"));
		if(jsonLineItem.get("Action").equals("Register With Preferences"))
			return new RegisterWithPreferences((String)jsonLineItem.get("Student"),(String[])jsonLineItem.get("Preferences"),(String[])jsonLineItem.get("Grade"));
		if(jsonLineItem.get("Action").equals("Close Course"))
			return new CloseCourse((String)jsonLineItem.get("Department"),(String)jsonLineItem.get("Course"));
		if(jsonLineItem.get("Action").equals("Add Spaces"))
			return new AddSpaces((String)jsonLineItem.get("Course"),Integer.parseInt((String)jsonLineItem.get("Number")));
//		if(jsonLineItem.get("Action").equals("Administrative Check")) TODO Administrative Check
//			return new AdministrativeCheck((String)jsonLineItem.get("Department"),(String[])jsonLineItem.get("Students"),(String)jsonLineItem.get("Computer"),(String[])jsonLineItem.get("Conditions"));

		return null; // if input is incorrect
	}
}
