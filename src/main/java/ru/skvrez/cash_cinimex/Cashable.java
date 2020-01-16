package ru.skvrez.cash_cinimex;

import java.util.Optional;

public interface Cashable<T> {
	
	void putObject(T object);
	
	Optional<T> getObject(CashQueryParameters parameters);
	
	void deleteOldObjects();
	
	void updateTimeToLive(Long time, TimeUnits units);
	
	void clear();
	
	void updateObjectTimeToLive(T object) throws NotInCashException;
}
