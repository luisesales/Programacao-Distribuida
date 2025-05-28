package com.kore.Classes;

import java.util.StringTokenizer;

public class ProcessPayload {
	private Bank bank;
	public ProcessPayload(Bank bank) {
		this.bank = bank;
	}
	public String processData(String msg) {
		System.out.println("Processing Data on Bank");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String operation =null;
		int account = 0;
		int valor = 0;
		
		StringTokenizer tokenizer = new StringTokenizer(msg, ";");
		while (tokenizer.hasMoreElements()) {					
			operation = tokenizer.nextToken();			
			account = Integer.parseInt(tokenizer.nextToken());			
			valor = Integer.parseInt(tokenizer.nextToken().trim());			
		}
		String reply = "operação realizada:" + operation + "-" ;
		String opResult = reply;
		switch (operation) {
		case "create":
			bank.addAccount(account);
			break;
		case "deposit":
			bank.deposit(account, valor);
			break;
		case "balance":
        	opResult = "Saldo é R$"+bank.balance(account);
        	break;
		}
		
		System.out.println(opResult);
		return opResult;
	}
}
