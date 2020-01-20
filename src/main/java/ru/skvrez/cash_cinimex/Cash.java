package ru.skvrez.cash_cinimex;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class Cash<T> implements Cashable<T>, Observer {

    private Cashable cash;

    public Cash(){
        cash = new MapCash();
    }

    public Cash(long time, TimeUnits units){
        cash = new MapCash(time, units);
    }

    public Cash(Cashable cashObject){
        cash = cashObject;
    }

    public Cash(Cashable cashObject, long time, TimeUnits units){
        cash = cashObject;
        cashObject.updateTimeToLive(time, units);
    }

    @Override
    public void putObject(T object) {
        cash.putObject(object);
    }

    @Override
    public Optional<T> getObject(CashQueryParameters parameters) {
        return cash.getObject(parameters);
    }

    @Override
    public void deleteOldObjects() {
        cash.deleteOldObjects();
    }

    @Override
    public void updateTimeToLive(long time, TimeUnits units) {
        cash.updateTimeToLive(time, units);
    }

    @Override
    public void updateCheckTime(long time) {
        cash.updateCheckTime(time);
    }

    @Override
    public void clear() {
        cash.clear();
    }

    @Override
    public void updateObjectAddingTime(T object) throws NotInCashException {
        cash.updateObjectAddingTime(object);
    }

    @Override
    public void update(Observable o, Object arg) {
        Observer observerCash = (Observer) cash;
        observerCash.update(o,arg);
    }
}
