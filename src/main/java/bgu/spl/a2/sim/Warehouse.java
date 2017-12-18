package bgu.spl.a2.sim;

import java.util.LinkedList;
import java.util.List;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {

    private List<Computer> computers;

    public Warehouse(){
        this.computers= new LinkedList<>();
    }

    public void addComputer(Computer computer){
        this.computers.add(computer);
    }

	
}
