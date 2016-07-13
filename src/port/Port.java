package port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import ship.Ship;
import warehouse.Container;
import warehouse.Warehouse;

public class Port {
	private final static Logger logger = Logger.getRootLogger();

	// lock instead synchronized
	private Lock lock;
	private BlockingQueue<Berth> berthList;
	private Warehouse portWarehouse;
	private Map<Ship, Berth> usedBerths;

	public Port(int berthSize, int warehouseSize) {
		lock = new ReentrantLock();
		portWarehouse = new Warehouse(warehouseSize);

		berthList = new ArrayBlockingQueue<Berth>(berthSize);

		for (int i = 0; i < berthSize; i++) {
			berthList.add(new Berth(i, portWarehouse));
		}
		usedBerths = new HashMap<Ship, Berth>();
		logger.debug("Порт создан.");
	}

	public void setContainersToWarehouse(List<Container> containerList) {
		portWarehouse.addContainer(containerList);
	}

	public boolean lockBerth(Ship ship) {
		boolean result = false;
		Berth berth = null;

		lock.lock();
		try {
			berth = berthList.take();
			result = true;
		} catch (InterruptedException e) {
			logger.debug("Кораблю " + ship.getName() + " отказано в швартовке.");
			return false;
		}
		
		usedBerths.put(ship, berth);
		lock.unlock();

		return result;
	}

	public boolean unlockBerth(Ship ship) {
		Berth berth = usedBerths.get(ship);

		//lock.lock();
		

		try {
			berthList.put(berth);
			usedBerths.remove(ship);
		} catch (InterruptedException e) {
			logger.debug("Корабль " + ship.getName()
					+ " не смог отшвартоваться.");
			return false;
		}
		
		//lock.unlock();

		return true;
	}

	public Berth getBerth(Ship ship) throws PortException {

		Berth berth = usedBerths.get(ship);
		if (berth == null) {
			throw new PortException("Try to use Berth without blocking.");
		}
		return berth;
	}
}
