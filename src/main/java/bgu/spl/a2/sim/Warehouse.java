package bgu.spl.a2.sim;

import java.util.concurrent.ConcurrentHashMap;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 * implementation- singleton
 */
public class Warehouse {
    private static class WarehouseHolder {
        private static Warehouse instance = new Warehouse();
    }
    public static Warehouse getInstance(){
        return WarehouseHolder.instance;
    }

    private ConcurrentHashMap<String,Computer> computers;

    private Warehouse(){
        this.computers= new ConcurrentHashMap<>();
    }

    public void addComputer(Computer computer){
        computers.put(computer.computerType,computer);
    }
    public Computer getComputer(String Type){
        return computers.get(Type);
    }

	
}

