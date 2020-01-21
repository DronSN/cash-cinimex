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

    public MapCash() {
        super();
    }

    public MapCash(long time, TimeUnits units) {
        super(time, units);
    }

    @Override
    public void putObject(T object) {
        super.putObject(object);
        updateCurrentTime();
        if (objectsList.containsKey(object)) {
            objectsList.remove(object);
        }
        objectsList.put(object, currentTime);
    }

    private T returnEldestObject(){
        return objectsList.entrySet()
                .stream()
                .findFirst()
                .get()
                .getKey();
    }

    private T returnNewestObject(){
        return objectsList.entrySet()
                .stream()
                .skip(objectsList.entrySet().size() - 1)
                .findFirst()
                .get()
                .getKey();
    }

    private T processCashQueryParameters(CashQueryParameters parameters){
        T object = null;
        if (parameters.isOldestElement()) {
            object = returnEldestObject();
        } else {
            object = returnNewestObject();
        }
        return object;
    }

    @Override
    public T getObject(CashQueryParameters parameters) {
        if (parameters == null) {
            throw new NullPointerException("Cash query parameter cannot be null");
        }
        if (objectsList.isEmpty()) {
            throw new NotInCashException("Object's list is empty. Cannot get object from list");
        }
        return processCashQueryParameters(parameters);
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
    public void updateObjectAddingTime(T object){
        super.updateObjectAddingTime(object);
        if (objectsList.containsKey(object)) {
            putObject(object);
        } else {
            throw new NotInCashException("Object not found in cache");
        }
    }

    Map<T, Long> getObjectsList() {
        return objectsList;
    }
}
