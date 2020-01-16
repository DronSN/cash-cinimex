package ru.skvrez.cash_cinimex;

import java.util.Optional;

public interface Cashable<T> {
	
	public void putObject(T object);
	
	public Optional<T> getObject(CashQueryParameters parameters);
	
	public void deleteOldObjects();
	
	public void updateTimeToLive(Long milliseconds);
	
	public void clear();
	
	public void updateObjectTimeToLive(T object);
	

}
