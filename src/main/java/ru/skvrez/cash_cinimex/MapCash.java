package ru.skvrez.cash_cinimex;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MapCash<T> extends AbstractCash<T> {

	private final Map<T, Long> objectsList = new LinkedHashMap<T, Long>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<T, Long> eldest) {
			long addTime = (long) eldest.getValue();
			return addTime + timeToLive < currentTime;
		}
	};

	public MapCash(){
		super();
	}

	public MapCash(long time, TimeUnits units){
		super(time, units);
	}

	@Override
	public void putObject(T object) {
        updateCurrentTime();
		if (objectsList.containsKey(object)){
			objectsList.remove(object);
		}
        objectsList.put(object, currentTime);
	}

	@Override
	public Optional<T> getObject(CashQueryParameters parameters) {
		if (parameters == null){
			throw new IllegalArgumentException("Cash query parameter cannot be null");
		}
		if (objectsList.isEmpty()){
			throw new NotInCashException("Object's list is empty. Cannot get object from list");
		}
		T object = null;
		if(parameters.isOldestElement()) {
			object = objectsList.entrySet()
					.stream()
					.findFirst()
					.get()
					.getKey();
		} else {
			object = objectsList.entrySet()
					.stream()
					.skip(objectsList.entrySet().size() - 1)
					.findFirst()
					.get()
					.getKey();
		}
		return Optional.of(object);
	}

	@Override
	public void deleteOldObjects() {
		updateCurrentTime();
		objectsList.entrySet()
					.removeIf(entry ->
							entry.getValue() + timeToLive < currentTime);
		updateCurrentTime();
		lastUpdate = currentTime;
	}

	@Override
	public void clear() {
		objectsList.clear();
		updateCurrentTime();
		lastUpdate = currentTime;
	}

	@Override
	public void updateObjectAddingTime(T object) throws NotInCashException {
		if(objectsList.containsKey(object)){
			putObject(object);
		} else {
			throw new NotInCashException("Object not found in cache");
		}
	}

	Map<T, Long> getObjectsList() {
		return objectsList;
	}
}
