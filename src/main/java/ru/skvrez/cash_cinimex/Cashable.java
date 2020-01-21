package ru.skvrez.cash_cinimex;

import com.sun.istack.internal.NotNull;

/**
 * The object temporarily stores other objects. A cash cannot contain duplicate objects.
 * When adding an existing object, it only changes the time of adding.
 * The identity of objects is verified using the {@code equals()} method.
 * {@code null} object is not allowed.
 *
 * @param <T> the type of object maintained by this cash.
 */
public interface Cashable<T> {

   /**
     * Associates the specified value with the specified object in this cash
     * If the cash previously contained a specified object, the time of adding object
     * will be updated. Adding a {@code null} object is not allowed.
     *
     * @param object object of type <i>T</i> will be added to cash.
     * @throws NullPointerException if object value is {@code null}
     */
    void putObject(T object);

    /**
     * Returns the object from cash.
     *
     * @param parameters is object {@code CashQueryParameters} which containing
     *                   the conditions for selecting items from the cache.
     * @return {@code Optional} container which contains object from cash.
     * @throws NotInCashException if cash is empty or not contains object matching the condition.
     * @throws IllegalArgumentException if {@code parameters} is not valid
     * @throws NullPointerException if {@code parameters} if {@code null}
     */
    T getObject(@NotNull CashQueryParameters parameters);

    /**
     * The method removes the oldest objects from the cache. Deletion of objects will begin
     * immediately after a method call and is independent of the parameter <i>time</i>
     * in method {@code updateCheckTime()}.
     */
    void deleteOldObjects();

    /**
     * Updating the period of time during which objects are stored in the cache.
     *
     * @param time period of storage of objects in the cache.
     *             If value is <i>0</i> then objects cannot delete from cash.
     * @param units units of time for the parameter {@code time}.
     *
     * @throws IllegalArgumentException if {@code time} is negative.
     * @throws NullPointerException if {@code units} is {@code null}
     */
    void updateTimeToLive(long time, @NotNull TimeUnits units);

    /**
     * Updating the time period for deleting the oldest objects from the cache.
     * Start the process of deleting objects begins at the expiration of period <i>time</i>.
     * @param time time period in milliseconds. If <i>time</i> value is <i>0</i> then
     *             deleting process cannot be started.
     *
     * @throws IllegalArgumentException if {@code time} is negative.
     */
    void updateCheckTime(long time);

    /**
     * Delete all objects from the cache.
     */
    void clear();

    /**
     *Changes the cache storage time of a specific object.
     *
     * @param object object for which the cache storage time will change.
     *
     * @throws NotInCashException if cash cannot contains specific object
     * @throws NullPointerException if {@code object} is {@code null}
     */
    void updateObjectAddingTime(@NotNull T object);
}
