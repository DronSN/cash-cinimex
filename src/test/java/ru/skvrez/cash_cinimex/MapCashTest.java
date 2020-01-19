package ru.skvrez.cash_cinimex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.LinkedList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MapCashTest {

    List<String> objectList = new LinkedList<>();
    MapCash<String> mapCash;

    @BeforeEach
    void setUp() {
        mapCash = new MapCash<>();
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
                "Assertion TimeToLive for constructor with parameters in " + arguments.get(1,TimeUnits.class));
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime  for constructor with parameters in " + arguments.get(1,TimeUnits.class));        
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
    void update() {
    }

    @Test
    void putObject() {
    }

    @Test
    void getObject() {
    }

    @Test
    void deleteOldObjects() {
    }

    @Test
    void clear() {
    }

    @Test
    void updateObjectTimeToLive() {
    }
}