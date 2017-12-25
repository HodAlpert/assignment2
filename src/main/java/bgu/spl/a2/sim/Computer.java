package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

	protected String computerType;
	private long failSig;
	private long successSig;
	private SuspendingMutex mutex;

	/**
	 * @param computerType type of the computer
	 * @param successSig signature that should be given if student pass the check
	 * @param failSig signature that should be given if student fail the check
	 */
	public Computer(String computerType, long successSig, long failSig) {

		this.computerType = computerType;
		this.successSig=successSig;
		this.failSig=failSig;
		this.mutex = new SuspendingMutex(this);
	}
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		for(String course: courses)
			if (course==null|| coursesGrades.get(course)==null || !coursesGrades.containsKey(course)|| coursesGrades.get(course) < 56)
				return this.failSig;
		return this.successSig;
	}

	public SuspendingMutex getMutex() {
		return mutex;
	}
}
