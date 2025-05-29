package Classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Bank {
	private static final String BANK_FILE = "bank.txt";
	private HashMap<Integer,Integer> accounts = new HashMap<>();
	public void addAccount(int numAccount) {
		accounts.put(numAccount, 0);
	}
	public void deposit(int numAccount, int value) {
		int selected = accounts.get(numAccount);
		accounts.put(numAccount, selected+value);
	}
	public int balance(int numAccount) {
		return accounts.get(numAccount);
	}

	public void saveBank(String request){
		synchronized (this){
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(BANK_FILE, true))) {
				writer.write(request);
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

