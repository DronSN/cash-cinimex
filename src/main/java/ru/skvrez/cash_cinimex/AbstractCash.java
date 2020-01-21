package ru.skvrez.cash_cinimex;

import java.util.Observable;
import java.util.Observer;

abstract class AbstractCash<T> implements Cashable<T>, Observer {

    private final long DEFAULT_TIME_TO_LIVE = 1_000_000;
    private final long DEFAULT_CHECK_TIME = 1_000_000;
    protected long timeToLive;
    protected long currentTime = System.currentTimeMillis();
    protected long checkTime;
    protected long lastUpdate = System.currentTimeMillis();


    public AbstractCash() {
        timeToLive = DEFAULT_TIME_TO_LIVE;
        checkTime = DEFAULT_CHECK_TIME;
    }

    public AbstractCash(long time, TimeUnits units) {
        timeToLiveValidating(time, units);
        timeToLiveSetting(time, units);
        checkTime = DEFAULT_CHECK_TIME;
    }

    protected long calcTimeToMillisec(long time, TimeUnits units) {
        switch (units) {
            case SECONDS:
                return time * 1000;
            case MINUTES:
                return time * 1000 * 60;
            case HOURS:
                return time * 1000 * 60 * 60;
            default:
                return time;
        }
    }

    private void timeToLiveValidating(long time, TimeUnits units) {
        if (units==null){
            throw new NullPointerException("Time units can not be null.");
        }
        if (time < 0) {
            throw new IllegalArgumentException("Time can not be negative.");
        }
    }

    private void timeToLiveSetting(long time, TimeUnits units) {
        if (time == 0) {
            timeToLive = Long.MAX_VALUE;
        } else {
            timeToLive = calcTimeToMillisec(time, units);
        }
    }

    @Override
    public void putObject(T object){
        if (object == null){
            throw new NullPointerException("Cash object cannot be null");
        }
    }

    @Override
    abstract public T getObject(CashQueryParameters parameters);

    @Override
    abstract public void deleteOldObjects();

    @Override
    public void updateTimeToLive(long time, TimeUnits units) {
        timeToLiveValidating(time, units);
        timeToLiveSetting(time, units);
    }

    @Override
    abstract public void clear();

    @Override
    public void updateObjectAddingTime(T object){
        if (object == null){
            throw new NullPointerException("Cash object cannot be null");
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        updateCurrentTime();
        if (lastUpdate + checkTime < currentTime) {
            deleteOldObjects();
            updateCurrentTime();
            lastUpdate = currentTime;
        }
    }

    protected void updateCurrentTime() {
        currentTime = System.currentTimeMillis();
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public long getCheckTime() {
        return checkTime;
    }

    @Override
    public void updateCheckTime(long checkTime) {
        if (checkTime < 0) {
            throw new IllegalArgumentException("Parameter checkTime cannot be negative");
        } else if (checkTime == 0) {
            this.checkTime = Long.MAX_VALUE;
        } else {
            this.checkTime = checkTime;
        }
        updateCurrentTime();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getTimeToLive() {
        return timeToLive;
    }
}
