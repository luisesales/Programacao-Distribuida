package Classes;

import java.util.HashMap;

public class Bank {
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
}
