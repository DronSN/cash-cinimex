package ru.skvrez.cash_cinimex;

import com.sun.istack.internal.NotNull;

import java.util.Optional;

public interface Cashable<T> {
	
	void putObject(@NotNull T object);
	
	Optional<T> getObject(@NotNull CashQueryParameters parameters);
	
	void deleteOldObjects();
	
	void updateTimeToLive(long time, @NotNull TimeUnits units);

	void updateCheckTime(long time);
	
	void clear();
	
	void updateObjectAddingTime(@NotNull T object) throws NotInCashException;
}
