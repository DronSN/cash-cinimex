package ru.skvrez.cash_cinimex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestParameters{

    Cashable<String> cash;
    boolean queryParameter;
    int objectIndex;

    TestParameters(Cashable<String> cashObject, boolean queryParameter, int objectIndex){
        this.cash = cashObject;
        this.queryParameter = queryParameter;
        this.objectIndex = objectIndex;
    }

    public Cashable<String> getCash() {
        return cash;
    }

    public boolean isQueryParameter() {
        return queryParameter;
    }

    public int getObjectIndex() {
        return objectIndex;
    }
}

public class CashTest {

    List<String> objectsList;
    static final int OBJECTS_LIST_COUNT = 10;

    static Stream<TestParameters> parametersProvider() {
        return Stream.of(
                new TestParameters(new MapCash<String>(),true, 0),
                new TestParameters(new MapCash<String>(),false, OBJECTS_LIST_COUNT-1),
                new TestParameters(new DequeCash<String>(),true, 0),
                new TestParameters(new DequeCash<String>(),false, OBJECTS_LIST_COUNT-1));
    }

    @BeforeEach
    void setUp() {
        objectsList = new ArrayList<>();
        for (int i = 0; i <OBJECTS_LIST_COUNT; i++){
            objectsList.add("" + i);
        }
    }

    void pause(int milliseconds){
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
        }
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void testPutGetObjectWhenFewObjectAndDifferentQueryParameter(TestParameters parameters) {
        Cash<String> cash = new Cash(parameters.getCash());
        for (String s:objectsList){
            cash.putObject(s);
        }
        String expectedObject = objectsList.get(parameters.getObjectIndex());
        String actualObject = cash.getObject(new CashQueryParameters(parameters.isQueryParameter()));
        assertEquals(expectedObject,actualObject);
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void testDeleteOldObjects(TestParameters parameters) {
        Cash<String> cash = new Cash(parameters.getCash());
        for (int i=0; i < OBJECTS_LIST_COUNT/2;i++){
            cash.putObject(objectsList.get(i));
        }
        pause(10);
        cash.updateTimeToLive(5, TimeUnits.MILLISECONDS);
        cash.deleteOldObjects();
        for (int i=OBJECTS_LIST_COUNT/2; i < OBJECTS_LIST_COUNT;i++){
            cash.putObject(objectsList.get(i));
        }
        String expectedObject = objectsList.get(OBJECTS_LIST_COUNT/2);
        String actualObject = cash.getObject(new CashQueryParameters());
        assertEquals(expectedObject,actualObject);
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void updateTimeToLive(TestParameters parameters) {
        Cash<String> cash = new Cash(parameters.getCash());
        for (int i=0; i < OBJECTS_LIST_COUNT;i++){
            cash.putObject(objectsList.get(i));
        }
        pause(10);
        cash.updateTimeToLive(5, TimeUnits.MILLISECONDS);
        cash.deleteOldObjects();
        Exception exception = assertThrows(NotInCashException.class, () ->
                cash.getObject(new CashQueryParameters()));
        assertEquals("Object's list is empty. Cannot get object from list", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void testUpdateCheckTime(TestParameters parameters) {
        Cash<String> cash = new Cash(parameters.getCash());;
        long expectedCheckTime = 10;
        cash.updateCheckTime(expectedCheckTime);
        AbstractCash<String> abstractCash = (AbstractCash<String>) cash.getCash();
        long actualCheckTime = abstractCash.getCheckTime();
        assertEquals(expectedCheckTime, actualCheckTime);
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void testClear(TestParameters parameters) {
        Cash<String> cash = new Cash(parameters.getCash());
        for (int i=0; i < OBJECTS_LIST_COUNT;i++){
            cash.putObject(objectsList.get(i));
        }
        cash.clear();
        Exception exception = assertThrows(NotInCashException.class, () ->
                cash.getObject(new CashQueryParameters(parameters.isQueryParameter())));
        assertEquals("Object's list is empty. Cannot get object from list", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void testUpdateObjectAddingTime(TestParameters parameters) {
        Cash<String> cash = new Cash(parameters.getCash());
        String expectedObject = "Object will be added to cash";
        cash.putObject(expectedObject);
        for (int i=0; i < OBJECTS_LIST_COUNT;i++){
            cash.putObject(objectsList.get(i));
        }
        pause(10);
        cash.updateObjectAddingTime(expectedObject);
        String actualObject = cash.getObject(new CashQueryParameters(false));
        assertEquals(expectedObject, actualObject);
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void testUpdateWhenUpdateNotPerform(TestParameters parameters) {
        String expectedObject = "Will be added to cash";
        Cash<String> cash = new Cash(parameters.getCash());
        cash.putObject(expectedObject);
        cash.update(null,null);
        String actualObject = cash.getObject(new CashQueryParameters());
        assertEquals(expectedObject, actualObject);
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    void testUpdateWhenUpdatePerform(TestParameters parameters) {
        String expectedObject = "Will be added to cash";
        Cash<String> cash = new Cash(parameters.getCash());
        cash.putObject(expectedObject);
        cash.updateCheckTime(5);
        cash.updateTimeToLive(5, TimeUnits.MILLISECONDS);
        pause(10);
        cash.update(null,null);
        Exception exception = assertThrows(NotInCashException.class, () ->
                cash.getObject(new CashQueryParameters()));
        assertEquals("Object's list is empty. Cannot get object from list", exception.getMessage());
    }
}