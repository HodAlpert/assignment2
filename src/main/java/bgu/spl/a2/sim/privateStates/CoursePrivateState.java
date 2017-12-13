package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private String course;
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
		this.course="";
		registered = 0;
		this.regStudents = new LinkedList<>();
		prequisites=new ArrayList<String>();
	}

	public String getCourse() { return course; }

	public void setCourse(String course) { this.course = course; }

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
