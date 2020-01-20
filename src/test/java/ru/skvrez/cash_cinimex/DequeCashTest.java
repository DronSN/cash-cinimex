/**
 * 
 */
package ru.skvrez.cash_cinimex;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrey
 *
 */
class DequeCashTest {

	List<String> objectList = new LinkedList<>();
	DequeCash<String> dequeCash;

	@BeforeEach
	void setUp() {
		dequeCash = new DequeCash<>();
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
		dequeCash.updateCurrentTime();
		try {
			Thread.sleep(1);
		} catch(InterruptedException ex) {}
		dequeCash.updateCurrentTime();
		long actualCurrentTime = dequeCash.getCurrentTime();
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
		dequeCash.updateTimeToLive(1,arguments.get(1, TimeUnits.class));
		long actualTimeToLive = dequeCash.getTimeToLive();
		assertEquals(expectedTimeToLive, actualTimeToLive,
				"Assertion TimeToLive updating in " + arguments.get(1, TimeUnits.class));
	}

	@Test
	void testUpdateTimeToLiveWhenUsedNullTimeUnitParameter(){
		Exception exception = assertThrows(IllegalArgumentException.class, () ->
				dequeCash.updateTimeToLive(1,null));
		assertEquals("Time units can not be null.", exception.getMessage().trim());
	}

	@Test
	void testUpdateTimeToLiveWhenTimeParameterHaveZeroValue(){
		long expectedTimeToLive = Long.MAX_VALUE;
		dequeCash.updateTimeToLive(0,TimeUnits.MILLISECONDS);
		long actualTimeToLive = dequeCash.getTimeToLive();
		assertEquals(expectedTimeToLive, actualTimeToLive,
				"Assertion TimeToLive for constructor with zero value of time parameter");
	}

	@Test
	void testUpdateTimeToLiveWhenNegativeTimeParameter(){
		Exception exception = assertThrows(IllegalArgumentException.class, () ->
				dequeCash.updateTimeToLive(-1,TimeUnits.MILLISECONDS));
		assertEquals("Time can not be negative.", exception.getMessage().trim());
	}

	@Test
	void testUpdateCheckTimeWhenTimeParameterHaveZeroValue(){
		long expectedCheckTime = Long.MAX_VALUE;
		dequeCash.updateCheckTime(0);
		long actualCheckTime = dequeCash.getCheckTime();
		assertEquals(expectedCheckTime, actualCheckTime,
				"Assertion CheckTime with zero value of time parameter");
	}

	@Test
	void testUpdateCheckTimeWhenValidTimeParameter(){
		long expectedCheckTime = 1000;
		dequeCash.updateCheckTime(1000);
		long actualCheckTime = dequeCash.getCheckTime();
		assertEquals(expectedCheckTime, actualCheckTime,
				"Assertion CheckTime with valid value of time parameter");
	}


	@Test
	void testUpdateCheckTimeWhenNegativeTimeParameter(){
		Exception exception = assertThrows(IllegalArgumentException.class, () ->
				dequeCash.updateCheckTime(-1));
		assertEquals("Parameter checkTime cannot be negative", exception.getMessage().trim());
	}


	@Test
	void testUpdateShouldNotDeleteOldObject() {
		for (String s:objectList){
			dequeCash.putObject(s);
		}
		dequeCash.update(null,null);
		int expectedObjectsNumber = 10;
		int actualObjectsNumber = dequeCash.getObjectsList().size();
		assertEquals(expectedObjectsNumber, actualObjectsNumber,
				"Assertion cash immutable after update invoking");
	}

	@Test
	void testUpdateShouldDeleteOldObject() {
		for (String s:objectList){
			dequeCash.putObject(s);
		}
		try {
			Thread.sleep(10);
		} catch(InterruptedException ex) {}
		dequeCash.updateCheckTime(5);
		dequeCash.updateTimeToLive(5,TimeUnits.MILLISECONDS);
		dequeCash.update(null,null);
		int expectedObjectsNumber = 0;
		int actualObjectsNumber = dequeCash.getObjectsList().size();
		assertEquals(expectedObjectsNumber, actualObjectsNumber,
				"Assertion cash should by empty after update invoking");
	}

	@Test
	void testPutObjectWhenObjectIsValid() {
		String testObject = "Object will put to cash";
		dequeCash.putObject(testObject);
		assumeTrue(dequeCash.getObjectsList().contains(testObject),
				"Assumption object is in list");
	}

	@Test
	void testPutObjectWhenObjectIsNull() {
		String testObject = "Object will put to cash";
		dequeCash.putObject(testObject);
		assumeTrue(dequeCash.getObjectsList().contains(testObject),
				"Assumption null object is in list");
	}

	@Test
	void testPutObjectWhenObjectPresentInCash() {
		for (String s:objectList){
			dequeCash.putObject(s);
		}
		String objectPutAgain = objectList.get(5);
		dequeCash.putObject(objectPutAgain);
		Map<String,Long> mapObjectsList = (Map<String, Long>) dequeCash.getObjectsList();
		String youngestObject = mapObjectsList.entrySet()
				.stream()
				.skip(mapObjectsList.entrySet().size() - 1)
				.findFirst()
				.get()
				.getKey();
		assertEquals(objectPutAgain,youngestObject,
				"Assertion putting present object in cash");
	}

	@Test
	void testGetObjectWhenNullParameterObject() {
		String testObject = "Object will put to cash";
		dequeCash.putObject(testObject);
		Exception exception = assertThrows(IllegalArgumentException.class, () ->
				dequeCash.getObject(null));
		assertEquals("Cash query parameter cannot be null", exception.getMessage().trim());
	}

	@Test
	void testGetObjectWhenDefaultParameterObject() {
		for (String s:objectList) {
			dequeCash.putObject(s);
		}
		String expectedObject = objectList.get(0);
		String actualObject = dequeCash.getObject(new CashQueryParameters()).get();
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
			dequeCash.putObject(s);
		}
		String expectedObject = objectList.get(arguments.getInteger(0));
		String actualObject = dequeCash.getObject(new CashQueryParameters(arguments.getBoolean(1))).get();
		assertEquals(expectedObject, actualObject,
				"Assumption getting object from cash");
	}

	@Test
	void testGetObjectWhenObjectsListIsEmpty(){
		Exception exception = assertThrows(NotInCashException.class, () ->
				dequeCash.getObject(new CashQueryParameters()));
		assertEquals("Object's list is empty. Cannot get object from list",
				exception.getMessage().trim());
	}

	@Test
	void testDeleteOldObjectsWhenAddedFewObjects() {
		for (int i = 0; i < 5; i++) {
			dequeCash.putObject(objectList.get(i));
		}
		try {
			Thread.sleep(10);
		} catch(InterruptedException ex) {
		}

		for (int i = 5; i < 10; i++) {
			dequeCash.putObject(objectList.get(i));
		}
		dequeCash.updateTimeToLive(5,TimeUnits.MILLISECONDS);
		dequeCash.deleteOldObjects();
		int expectedObjectsCount = 5;
		int actualObjectsCount = dequeCash.getObjectsList().size();
		assertEquals(expectedObjectsCount,actualObjectsCount,
				"Assertion number of objects in cash");
		try {
			Thread.sleep(10);
		} catch(InterruptedException ex) {
		}
		dequeCash.deleteOldObjects();
		expectedObjectsCount = 0;
		actualObjectsCount = dequeCash.getObjectsList().size();
		assertEquals(expectedObjectsCount,actualObjectsCount,
				"Assertion number of objects in empty cash");

	}

	@Test
	void testDeleteOldObjectsShouldUpdateCurrentTime(){
		long expectedTime = System.currentTimeMillis();
		dequeCash.deleteOldObjects();
		long actualCurrentTime = dequeCash.getCurrentTime();
		assertEquals(expectedTime, actualCurrentTime,
				"Assertion current time after deleting elder objects");
	}

	@Test
	void testDeleteOldObjectsShouldChangedLastUpdateTime(){
		long expectedLastUpdateTime = System.currentTimeMillis();
		dequeCash.deleteOldObjects();
		long actualLastUpdateTime = dequeCash.getLastUpdate();
		assertEquals(expectedLastUpdateTime, actualLastUpdateTime,
				"Assertion last update time after deleting elder objects");
	}


	@Test
	void testClearWhenAddedFewObjects() {
		for (String s:objectList) {
			dequeCash.putObject(s);
		}
		dequeCash.clear();
		assumeTrue(dequeCash.getObjectsList().isEmpty(),
				"Assumption object list is empty");
	}

	@Test
	void testClearShouldUpdateCurrentTime(){
		for (String s:objectList) {
			dequeCash.putObject(s);
		}
		long expectedTime = System.currentTimeMillis();
		dequeCash.clear();
		long actualCurrentTime = dequeCash.getCurrentTime();
		assertEquals(expectedTime, actualCurrentTime,
				"Assertion current time after cash clearing");
	}

	@Test
	void testClearShouldChangedLastUpdateTime(){
		for (String s:objectList) {
			dequeCash.putObject(s);
		}
		long expectedLastUpdateTime = System.currentTimeMillis();
		dequeCash.clear();
		long actualLastUpdateTime = dequeCash.getLastUpdate();
		assertEquals(expectedLastUpdateTime, actualLastUpdateTime,
				"Assertion last update time after cash clearing");
	}

	@Test
	void testUpdateObjectTimeShouldUpdateTimeInMap() {
		for (String s:objectList) {
			dequeCash.putObject(s);
		}
		try {
			Thread.sleep(1);
		} catch(InterruptedException ex) {}
		dequeCash.updateObjectAddingTime(objectList.get(5));
		long expectedTime = dequeCash.getCurrentTime();
		Map<String,Long> mapObjectsList = (Map<String, Long>) dequeCash.getObjectsList();
		long actualObjectAddingTime = mapObjectsList.get(objectList.get(5));
		assertEquals(expectedTime, actualObjectAddingTime,
				"Assumption updating object adding time");
	}

	@Test
	void testUpdateObjectTimeShouldObjectInsertToEndOfList() {
		for (String s:objectList) {
			dequeCash.putObject(s);
		}
		try {
			Thread.sleep(1);
		} catch(InterruptedException ex) {}
		String expectedObject = objectList.get(5);
		dequeCash.updateObjectAddingTime(objectList.get(5));
		Map<String,Long> mapObjectsList = (Map<String, Long>) dequeCash.getObjectsList();
		String actualObject = mapObjectsList.entrySet()
				.stream()
				.skip(mapObjectsList.entrySet().size() - 1)
				.findFirst()
				.get()
				.getKey();
		assertEquals(expectedObject, actualObject,
				"Assumption updating object in list");
	}

}
