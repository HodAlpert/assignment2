package bgu.spl.a2.sim;

import org.junit.Test;

public class WarehouseTest {

    @Test
    public void getInstance() {
        Warehouse w1=Warehouse.getInstance();
        Computer comp = new Computer("A",11,22);
        w1.addComputer(comp);
        Warehouse w2 = Warehouse.getInstance();
    }
}