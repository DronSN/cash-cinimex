package ru.skvrez.cash_cinimex;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		String message1 = "11111111";
		String message2 = "22222222";
		String message3 = "33333333";
		String message4 = "44444444";
		Cashable<String> cash = new Cash<>(1, TimeUnits.MINUTES);
		cash.putObject(message1);
		Thread.sleep(1);
		cash.putObject(message2);
		Thread.sleep(1);
		cash.putObject(message3);
		Thread.sleep(1);
		cash.putObject(message4);
		Thread.sleep(1);
		System.out.println(cash.getObject(new CashQueryParameters(false)));

	}

}
