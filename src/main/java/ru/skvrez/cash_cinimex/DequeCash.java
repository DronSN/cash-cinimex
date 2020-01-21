package ru.skvrez.cash_cinimex;

import java.util.*;

public class DequeCash<T> extends AbstractCash<T> {

    Deque<Node> objectsDeque = new LinkedList<>();
    ;

    public DequeCash() {
        super();
    }

    public DequeCash(long time, TimeUnits units) {
        super(time, units);
    }

    class Node {
        T object;
        long addTime;

        Node(T object, long time) {
            this.object = object;
            this.addTime = time;
        }

        public T getObject() {
            return object;
        }

        public void setObject(T object) {
            this.object = object;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }
    }

    Node getObjectNode(T object) {
        for (Node node : objectsDeque) {
            if (node.getObject().equals(object)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void putObject(T object) {
        updateCurrentTime();
        deleteIfPresent(object);
        Node node = new Node(object, currentTime);
        objectsDeque.add(node);
    }

    @Override
    public Optional<T> getObject(CashQueryParameters parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Cash query parameter cannot be null");
        }
        if (objectsDeque.isEmpty()) {
            throw new NotInCashException("Object's list is empty. Cannot get object from list");
        }
        T object = null;
        if (parameters.isOldestElement()) {
            object = objectsDeque.peekFirst().getObject();
        } else {
            object = objectsDeque.peekLast().getObject();
        }
        return Optional.of(object);
    }

    @Override
    public void deleteOldObjects() {
        updateCurrentTime();
        if (objectsDeque.size() > 0) {
            Node node = objectsDeque.peekFirst();
            while (node != null &&
                    node.getAddTime() + timeToLive < currentTime) {
                objectsDeque.pollFirst();
                node = objectsDeque.peekFirst();
            }
        }
        updateCurrentTime();
        lastUpdate = currentTime;
    }

    @Override
    public void clear() {
        objectsDeque.clear();
        updateCurrentTime();
        lastUpdate = currentTime;
    }

    private boolean deleteIfPresent(T object) {
        boolean isPresent = false;
        Iterator<DequeCash<T>.Node> iterator = objectsDeque.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getObject().equals(object)) {
                iterator.remove();
                isPresent = true;
                break;
            }
        }
        return isPresent;
    }

    @Override
    public void updateObjectAddingTime(T object) throws NotInCashException {
        if (!deleteIfPresent(object)) {
            throw new NotInCashException();
        } else {
            Node node = new Node(object, currentTime);
            objectsDeque.add(node);
        }
    }

    private Node findObjectNode(T object) {
        Node result = null;
        for (Node node : objectsDeque) {
            if (node.getObject().equals(object)) {
                result = node;
                break;
            }
        }
        return result;
    }

    LinkedList<Node> getObjectsList() {
        return (LinkedList) objectsDeque;
    }

}
