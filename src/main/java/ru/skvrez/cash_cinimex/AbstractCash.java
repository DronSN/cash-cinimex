package ru.skvrez.cash_cinimex;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

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
        StringBuilder exceptionOutput = new StringBuilder();
        boolean isWrong = false;
        if (units == null) {
            exceptionOutput.append("Time units can not be null. ");
            isWrong = true;
        }
        if (time < 0) {
            exceptionOutput.append("Time can not be negative.");
            isWrong = true;
        }
        if (isWrong) {
            throw new IllegalArgumentException(exceptionOutput.toString());
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
    abstract public void putObject(T object);

    @Override
    abstract public Optional<T> getObject(CashQueryParameters parameters);

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
    abstract public void updateObjectAddingTime(T object) throws NotInCashException;

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

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
        deleteOldObjects();
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
