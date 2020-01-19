package ru.skvrez.cash_cinimex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class MapCashTest {

    List<String> objectList = new LinkedList<>();
    MapCash<String> mapCash;

    @BeforeEach
    void setUp() {
        mapCash = new MapCash<>();
        for (int i = 0; i < 10; i++){
            objectList.add("" + i);
        }
    }

    @ParameterizedTest
    //Convert list time units to milliseconds
    @CsvSource({
        "1, MILLISECONDS",
        "1000, SECONDS",
        "60000, MINUTES",
        "3600000, HOURS"
    })
    void testConstructorWhenParametersPassed(ArgumentsAccessor arguments) {
    	MapCash<String> constructorWithParam = 
    			new MapCash<>(1, arguments.get(1,TimeUnits.class));
    	long expectedTimeToLive = arguments.getLong(0);
    	long expectedCheckTime  = 1_000_000;
        long actualTimeToLive = constructorWithParam.getTimeToLive();
        long actualCheckTime = constructorWithParam.getCheckTime();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for constructor with parameters in " +
                        arguments.get(1,TimeUnits.class));
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime  for constructor with parameters in " +
                        arguments.get(1,TimeUnits.class));
    }
   
    @Test
    void testConstructorWhenItHaveNoParameters(){
        MapCash<String> defaultConstructor = new MapCash<>();
        long expectedTimeToLive = 1_000_000;
        long expectedCheckTime  = 1_000_000;
        long actualTimeToLive = defaultConstructor.getTimeToLive();
        long actualCheckTime = defaultConstructor.getCheckTime();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for default constructor");
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime for default constructor");
    }

    @Test
    void testConstructorWhenItHaveNullUnitParameter(){
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new MapCash<>(1,null));
        assertEquals("Time units can not be null.", exception.getMessage().trim());
    }

    @Test
    void testConstructorWhenItHaveNegativeTimeParameter(){
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new MapCash<>(-1,TimeUnits.MILLISECONDS));
        assertEquals("Time can not be negative.", exception.getMessage().trim());
    }

    @Test
    void testConstructorWhenTimeParameterHaveZeroValue(){
        MapCash<String> constructorWithParam = new MapCash<>(0,TimeUnits.MILLISECONDS);
        long expectedTimeToLive = Long.MAX_VALUE;
        long expectedCheckTime  = 1_000_000;
        long actualTimeToLive = constructorWithParam.getTimeToLive();
        long actualCheckTime = constructorWithParam.getCheckTime();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for constructor with zero value of time parameter");
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime for constructor with zero value of time parameter");
    }

    @Test
    void testUpdateCurrentTimeWhenItChangedAfterPause(){
        long expectedCurrentTime = System.currentTimeMillis();
        mapCash.updateCurrentTime();
        try {
            Thread.sleep(1);
        } catch(InterruptedException ex) {}
        mapCash.updateCurrentTime();
        long actualCurrentTime = mapCash.getCurrentTime();
        assertNotEquals(expectedCurrentTime,actualCurrentTime,
                "Assertion current time updating");
    }

    @ParameterizedTest
    //Convert list time units to milliseconds
    @CsvSource({
        "1, MILLISECONDS",
        "1000, SECONDS",
        "60000, MINUTES",
        "3600000, HOURS"
    })
    void testUpdateTimeToLiveWhenUsedDifferentTimeUnits(ArgumentsAccessor arguments) {
        long expectedTimeToLive = arguments.getLong(0);
        mapCash.updateTimeToLive(1,arguments.get(1, TimeUnits.class));
        long actualTimeToLive = mapCash.getTimeToLive();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive updating in " + arguments.get(1, TimeUnits.class));
    }

    @Test
    void testUpdateTimeToLiveWhenUsedNullTimeUnitParameter(){
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                mapCash.updateTimeToLive(1,null));
        assertEquals("Time units can not be null.", exception.getMessage().trim());
    }

    @Test
    void testUpdateTimeToLiveWhenTimeParameterHaveZeroValue(){
        long expectedTimeToLive = Long.MAX_VALUE;
        mapCash.updateTimeToLive(0,TimeUnits.MILLISECONDS);
        long actualTimeToLive = mapCash.getTimeToLive();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for constructor with zero value of time parameter");
    }

    @Test
    void testUpdateTimeToLiveWhenNegativeTimeParameter(){
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
               mapCash.updateTimeToLive(-1,TimeUnits.MILLISECONDS));
        assertEquals("Time can not be negative.", exception.getMessage().trim());
    }

    @Test
    void update() {
    }

    @Test
    void testPutObjectWhenObjectIsValid() {
        String testObject = "Object will put to cash";
        mapCash.putObject(testObject);
        assumeTrue(mapCash.getObjectsList().containsKey(testObject),
                "Assumption object is in list");
    }

    @Test
    void testPutObjectWhenObjectIsNull() {
        String testObject = "Object will put to cash";
        mapCash.putObject(testObject);
        assumeTrue(mapCash.getObjectsList().containsKey(testObject),
                "Assumption null object is in list");
    }

    @Test
    void testGetObjectWhenNullParameterObject() {
        String testObject = "Object will put to cash";
        mapCash.putObject(testObject);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                mapCash.getObject(null));
        assertEquals("Cash query parameter cannot be null", exception.getMessage().trim());
    }

    @Test
    void testGetObjectWhenDefaultParameterObject() {
        for (String s:objectList) {
            mapCash.putObject(s);
        }
        String expectedObject = objectList.get(0);
        String actualObject = mapCash.getObject(new CashQueryParameters()).get();
        assertEquals(expectedObject, actualObject,
                "Assumption getting first object from list");
    }

    @ParameterizedTest
    //Convert list time units to milliseconds
    @CsvSource({
            // (0) is first object from list
            // (true) is constructor parameter for first (oldest) element returning
            "0, true",
            // (9) is last object from list
            // (false) is constructor parameter for last (newest) element returning
            "9, false"
    })
    void testGetObjectWhenSetParameterObject(ArgumentsAccessor arguments) {
        for (String s:objectList) {
            mapCash.putObject(s);
        }
        String expectedObject = objectList.get(arguments.getInteger(0));
        String actualObject = mapCash.getObject(new CashQueryParameters(arguments.getBoolean(1))).get();
        assertEquals(expectedObject, actualObject,
                "Assumption getting object from cash");
    }

    @Test
    void testGetObjectWhenObjectsListIsEmpty(){
        Exception exception = assertThrows(NotInCashException.class, () ->
                mapCash.getObject(new CashQueryParameters()));
        assertEquals("Object's list is empty. Cannot get object from list",
                exception.getMessage().trim());
    }

    @Test
    void deleteOldObjects() {
        for (int i = 0; i < 5; i++) {
            mapCash.putObject(objectList.get(i));
        }
        try {
            Thread.sleep(10);
        } catch(InterruptedException ex) {
        }
        mapCash.updateTimeToLive(5,TimeUnits.MILLISECONDS);
        mapCash.setCheckTime(1);
        for (int i = 5; i < 10; i++) {
            mapCash.putObject(objectList.get(i));
        }
        mapCash.deleteOldObjects();
        mapCash.getObjectsList().entrySet().stream().forEach(x-> System.out.println(x));
    }

    @Test
    void testClearWhenAddedFewObjects() {
        for (String s:objectList) {
            mapCash.putObject(s);
        }
        mapCash.clear();
        assumeTrue(mapCash.getObjectsList().isEmpty(),
                "Assumption object list is empty");
    }

    @Test
    void testClearShouldUpdateCurrentTime(){
        for (String s:objectList) {
            mapCash.putObject(s);
        }
        long expectedTime = System.currentTimeMillis();
        mapCash.clear();
        long actualCurrentTime = mapCash.getCurrentTime();
        assertEquals(expectedTime, actualCurrentTime,
                "Assertion current time after cash clearing");
    }

    @Test
    void testClearShouldChangedLastUpdateTime(){
        for (String s:objectList) {
            mapCash.putObject(s);
        }
        long expectedLastUpdateTime = System.currentTimeMillis();
        mapCash.clear();
        long actualLastUpdateTime = mapCash.getLastUpdate();
        assertEquals(expectedLastUpdateTime, actualLastUpdateTime,
                "Assertion last update time after cash clearing");
    }

    @Test
    void testUpdateObjectTimeShouldUpdateTimeInMap() {
        for (String s:objectList) {
            mapCash.putObject(s);
        }
        try {
            Thread.sleep(1);
        } catch(InterruptedException ex) {}
        mapCash.updateObjectAddingTime(objectList.get(5));
        long expectedTime = mapCash.getCurrentTime();
        Map<String,Long> mapObjectsList = mapCash.getObjectsList();
        long actualObjectAddingTime = mapObjectsList.get(objectList.get(5));
        assertEquals(expectedTime, actualObjectAddingTime,
                "Assumption updating object adding time");
    }

    @Test
    void testUpdateObjectTimeShouldObjectInsertToEndOfList() {
        for (String s:objectList) {
            mapCash.putObject(s);
        }
        try {
            Thread.sleep(1);
        } catch(InterruptedException ex) {}
        String expectedObject = objectList.get(5);
        mapCash.updateObjectAddingTime(objectList.get(5));
        Map<String,Long> mapObjectsList = mapCash.getObjectsList();
        String actualObject = mapObjectsList.entrySet()
                .stream()
                .skip(mapObjectsList.entrySet().size() - 1)
                .findFirst()
                .get()
                .getKey();
        mapObjectsList.values().stream().forEach(x-> System.out.println(x));
        assertEquals(expectedObject, actualObject,
                "Assumption updating object in list");
    }
}