package ru.skvrez.cash_cinimex;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class DequeCash<T> extends AbstractCash<T> {

	Deque<Node> objectsDeque = new LinkedList<>();;

	public DequeCash(){
		super();
	}

	public DequeCash(long time, TimeUnits units){
		super(time, units);
	}

	class Node{
		T object;
		long addTime;
		
		Node(T object, long time){
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
	
	@Override
	public void putObject(T object) {
        updateCurrentTime();
		deleteOldObjects();
		Node findedObjectNode = findObjectNode(object); 
		if(findedObjectNode == null) {
			Node node = new Node(object, currentTime);
			objectsDeque.add(node);
		} else {
			findedObjectNode.setAddTime(currentTime);
		}
	}

	@Override
	public Optional<T> getObject(CashQueryParameters parameters) {
		T object = null;
		if(parameters != null) {
			if(parameters.isOldestElement()) {
				object = objectsDeque.peekFirst().getObject(); 
			} else {
				object = objectsDeque.peekLast().getObject();
			}
		}
		return Optional.of(object);
	}

	@Override
	public void deleteOldObjects() {
		if(currentTime - lastUpdate > checkTime) {		
			if(objectsDeque.size() > 0) {
				Node node = objectsDeque.peekFirst();
				while(node != null &&
						node.getAddTime() + timeToLive > currentTime) {
					objectsDeque.pollFirst();
					node = objectsDeque.peekFirst();
				}
			}
			updateCurrentTime();
			lastUpdate = currentTime;
		}
	}

	@Override
	public void clear() {
		objectsDeque.clear();
		updateCurrentTime();
		lastUpdate = currentTime;
	}

	@Override
	public void updateObjectAddingTime(T object) throws NotInCashException {
		boolean isFinded = false;
		for(Node node:objectsDeque) {
			T dequeObject = node.getObject();
			if(dequeObject.equals(object)) {
				node.setAddTime(currentTime);
				isFinded = true;
				break;
			}
		}
		if (!isFinded) {
			throw new NotInCashException();
		}
	}
	
	private Node findObjectNode(T object) {
		Node result=null;
		for(Node node:objectsDeque) {
			if(node.getObject().equals(object)) {
				result = node;
				break;
			}
		}
		return result;
	}
	LinkedList<Node> getObjectsList() {
		return (LinkedList)objectsDeque;
	}

}
