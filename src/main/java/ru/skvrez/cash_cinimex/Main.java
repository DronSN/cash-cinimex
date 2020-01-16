package ru.skvrez.cash_cinimex;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		String message1 = "11111111";
		String message2 = "22222222";
		String message3 = "33333333";
		String message4 = "44444444";
		Cashable<String> cash = new MapCash<>();
		cash.putObject(message1);
		Thread.sleep(1000);
		cash.putObject(message2);
		cash.putObject(message3);
		cash.putObject(message4);
		System.out.println(cash.getObject(new CashQueryParameters(false)));

	}

}
