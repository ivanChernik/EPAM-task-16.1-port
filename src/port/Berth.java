package port;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import warehouse.Container;
import warehouse.Warehouse;

public class Berth {

	private int id;
	private Warehouse portWarehouse;

	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}

	public int getId() {
		return id;
	}

	public boolean add(Warehouse shipWarehouse, int numberOfConteiners)
			throws InterruptedException {
		boolean result = false;

		Lock portWarehouseLock = portWarehouse.getLock();

		portWarehouseLock.lock();
		result = movingFromShip(shipWarehouse, numberOfConteiners);
		portWarehouseLock.unlock();

		return result;

	}

	private boolean movingFromShip(Warehouse shipWarehouse,
			int numberOfContainers) throws InterruptedException {

		if (shipWarehouse.getRealSize() >= numberOfContainers) {
			List<Container> containers = shipWarehouse
					.getContainer(numberOfContainers);
			portWarehouse.addContainer(containers);
			return true;
		}

		return false;
	}

	private boolean movingToShip(Warehouse shipWarehouse, int numberOfContainers)
			throws InterruptedException {
		List<Container> containers = portWarehouse
				.getContainer(numberOfContainers);
		boolean result = shipWarehouse.addContainer(containers);
		
		return result;
				
	}

	public boolean get(Warehouse shipWarehouse, int numberOfConteiners)
			throws InterruptedException {
		boolean result = false;

		Lock portWarehouseLock = portWarehouse.getLock();

		portWarehouseLock.lock();
		
		result = movingToShip(shipWarehouse, numberOfConteiners);
		
		portWarehouseLock.unlock();

		return result;
	}

}
