package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		regStudents=new ArrayList<String>();
		prequisites=new ArrayList<String>();
	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}

	public void setPrerequisites(List<String> prerequisites){
		this.prequisites = prerequisites;
	}
	public void setRegStudents(List<String> regStudents){
		this.regStudents=regStudents;
	}
	public void setRegistered(Integer registered){
		this.registered=registered;
	}
	public void setAvailableSpots (Integer availableSpots){
		this.availableSpots=availableSpots;
	}
}
