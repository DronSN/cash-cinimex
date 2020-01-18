package ru.skvrez.cash_cinimex;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class MapCash<T> extends AbstractCash<T> {

	private Map<T, Long> objectsList = new HashMap<>();

	public MapCash(){
		super();
	}

	public MapCash(long time, TimeUnits units){
		super(time, units);
	}

	@Override
	public void putObject(T object) {
        updateCurrentTime();
		deleteOldObjects();
		objectsList.put(object, currentTime);
	}

	@Override
	public Optional<T> getObject(CashQueryParameters parameters) {
		T object = null;
		if(parameters.isOldestElement()) {
			long minTime = Long.MAX_VALUE;
			for(Map.Entry<T, Long> entry:objectsList.entrySet()) {
				long entryValue = entry.getValue();
				if (entryValue < minTime) {
					minTime = entryValue;
					object = entry.getKey();
				}
			}
		} else {
			long maxTime = 0;
			for(Map.Entry<T, Long> entry:objectsList.entrySet()) {
				long entryValue = entry.getValue();
				if (entryValue > maxTime) {
					maxTime = entryValue;
					object = entry.getKey();
				}
			}
		}
		return Optional.of(object);
	}

	@Override
	public void deleteOldObjects() {
		if(currentTime - lastUpdate > checkTime) {
			objectsList.entrySet()
					.removeIf(entry -> entry.getValue() + timeToLive > currentTime);
			updateCurrentTime();
			lastUpdate = currentTime;
		}
	}

	@Override
	public void clear() {
		objectsList.clear();
		updateCurrentTime();
		lastUpdate = currentTime;
	}

	@Override
	public void updateObjectTimeToLive(T object) throws NotInCashException {
		if(objectsList.containsKey(object)){
			putObject(object);
		} else {
			throw new NotInCashException();
		}
	}
}
