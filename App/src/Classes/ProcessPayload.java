package Classes;

import java.util.StringTokenizer;

public class ProcessPayload {
	private Bank bank;
	public ProcessPayload(Bank bank) {
		this.bank = bank;
	}
	public String processData(String msg) {
		
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
		String reply = "operation realizada:" + operation + "-" ;
		String opResult = reply;
		switch (operation) {
		case "criar":
			bank.addAccount(account);
			break;
		case "depositar":
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
