package ru.skvrez.cash_cinimex;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

abstract class AbstractCash<T> implements Cashable<T>, Observer {

	private final long DEFAULT_TIME_TO_LIVE = 1_000_000;
	private final long DEFAULT_CHECK_TIME   = 1_000_000;
	protected long timeToLive;
	protected long currentTime = System.currentTimeMillis();
	protected long checkTime;
	protected long lastUpdate  = System.currentTimeMillis();

	
	public AbstractCash(){
		timeToLive = DEFAULT_TIME_TO_LIVE;
		checkTime  = DEFAULT_CHECK_TIME;
	}
	
	public AbstractCash(long time, TimeUnits units){
		timeToLive = calcTimeToMillisec(time, units);
	}
	
	protected long calcTimeToMillisec(long time, TimeUnits units) {
		switch(units) {
			case SECONDS: return time * 1000;
			case MINUTES: return time * 1000 * 60;
			case HOURS:   return time * 1000 * 60 * 60;
			default:      return time;
		}
	}
	
	@Override
	abstract public void putObject(T object);

	@Override
	abstract public Optional<T> getObject(CashQueryParameters parameters);

	@Override
	abstract public void deleteOldObjects();

	@Override
	public void updateTimeToLive(Long time, TimeUnits units) {
		timeToLive = calcTimeToMillisec(time, units);		
	}

	@Override
	abstract public void clear();

	@Override
	abstract public void updateObjectTimeToLive(T object) throws NotInCashException;

	@Override
	public void update(Observable arg0, Object arg1) {
		updateCurrentTime();
		deleteOldObjects();		
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

	public void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
		updateCurrentTime();
		deleteOldObjects();		
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public long getTimeToLive() {
		return timeToLive;
	}
}
