package main;

import java.util.ArrayList;
import java.util.List;

import port.Port;
import ship.Ship;
import warehouse.Container;

public class Main {

	private static int count = 1;

    public static void main(String[] args) throws InterruptedException {
        int warehousePortSize = 15;
        List<Container> containerList = new ArrayList<>(warehousePortSize);
        for (int i = 0; i < warehousePortSize; i++) {
            containerList.add(new Container(i));
        }

        Port port = new Port(2, 90);
        port.setContainersToWarehouse(containerList);

        int warehouseShipSize = 50;

        List<Ship> fleet = new ArrayList<>(20);
        for (int i = 0; i < 3; i++) {
            fleet.add(prepareShip(port, warehouseShipSize));
        }

        for (Ship ship: fleet) {
            new Thread(ship).start();
        }

        Thread.sleep(3000);

        for (Ship ship: fleet) {
            ship.stopThread();
        }

    }

    private static Ship prepareShip(Port port, int warehouseShipSize) {
        List<Container> containerList;
        containerList = new ArrayList<>(warehouseShipSize);
        for (int i = 0; i < warehouseShipSize; i++) {
            containerList.add(new Container(i + 60));
        }
        Ship ship = new Ship("Ship" + count++, port, 90);
        ship.setContainersToWarehouse(containerList);
        return ship;
    }

}
