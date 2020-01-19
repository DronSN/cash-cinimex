package ru.skvrez.cash_cinimex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testConstructorWhenItHaveParameterTimeInMilliseconds(){
        MapCash<String> constructorWithParam = new MapCash<>(1000,TimeUnits.MILLISECONDS);
        long expectedTimeToLive = 1000;
        long expectedCheckTime  = 1_000_000;
        long actualTimeToLive = constructorWithParam.getTimeToLive();
        long actualCheckTime = constructorWithParam.getCheckTime();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for constructor with parameters in MILLISECONDS");
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime for constructor with parameters in MILLISECONDS");
    }

    @Test
    void testConstructorWhenItHaveParameterTimeInSeconds(){
        MapCash<String> constructorWithParam = new MapCash<>(10,TimeUnits.SECONDS);
        long expectedTimeToLive = 10_000;
        long expectedCheckTime  = 1_000_000;
        long actualTimeToLive = constructorWithParam.getTimeToLive();
        long actualCheckTime = constructorWithParam.getCheckTime();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for constructor with parameters in SECONDS");
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime for constructor with parameters in SECONDS");
    }

    @Test
    void testConstructorWhenItHaveParameterTimeInMinutes(){
        MapCash<String> constructorWithParam = new MapCash<>(1,TimeUnits.MINUTES);
        long expectedTimeToLive = 60_000;
        long expectedCheckTime  = 1_000_000;
        long actualTimeToLive = constructorWithParam.getTimeToLive();
        long actualCheckTime = constructorWithParam.getCheckTime();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for constructor with parameters in MINUTES");
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime for constructor with parameters in MINUTES");
    }

    @Test
    void testConstructorWhenItHaveParameterTimeInHours(){
        MapCash<String> constructorWithParam = new MapCash<>(1,TimeUnits.HOURS);
        long expectedTimeToLive = 3_600_000;
        long expectedCheckTime  = 1_000_000;
        long actualTimeToLive = constructorWithParam.getTimeToLive();
        long actualCheckTime = constructorWithParam.getCheckTime();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive for constructor with parameters in HOURS");
        assertEquals(expectedCheckTime, actualCheckTime,
                "Assertion CheckTime for constructor with parameters in HOURS");
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

    @Test
    void testUpdateTimeToLiveWhenUsedMillisecondsUnit() {
        long expectedTimeToLive = 1000;
        mapCash.updateTimeToLive(1000,TimeUnits.MILLISECONDS);
        long actualTimeToLive = mapCash.getTimeToLive();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive updating in MILLISECONDS");
    }

    @Test
    void testUpdateTimeToLiveWhenUsedSecondsUnit() {
        long expectedTimeToLive = 1_000_000;
        mapCash.updateTimeToLive(1000,TimeUnits.SECONDS);
        long actualTimeToLive = mapCash.getTimeToLive();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive updating in SECONDS");
    }

    @Test
    void testUpdateTimeToLiveWhenUsedMinutesUnit() {
        long expectedTimeToLive = 60_000;
        mapCash.updateTimeToLive(1,TimeUnits.MINUTES);
        long actualTimeToLive = mapCash.getTimeToLive();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive updating in MINUTES");
    }

    @Test
    void testUpdateTimeToLiveWhenUsedHoursUnit() {
        long expectedTimeToLive = 3_600_000;
        mapCash.updateTimeToLive(1,TimeUnits.HOURS);
        long actualTimeToLive = mapCash.getTimeToLive();
        assertEquals(expectedTimeToLive, actualTimeToLive,
                "Assertion TimeToLive updating in HOURS");
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