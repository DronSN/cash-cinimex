package ru.skvrez.cash_cinimex;

public class CashQueryParameters {
	private boolean oldestElement;
	 CashQueryParameters(){
		 oldestElement = true;
	 }
	 
	 CashQueryParameters(boolean oldest){
		 oldestElement = oldest;
	 }
	 
	public boolean isOldestElement() {
		return oldestElement;
	}
	public void setOldestElement(boolean oldestElement) {
		this.oldestElement = oldestElement;
	}
	 
}
