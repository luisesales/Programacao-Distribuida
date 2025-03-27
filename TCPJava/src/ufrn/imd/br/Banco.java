package ufrn.imd.br;

import java.util.HashMap;

public class Banco {
	private HashMap<Integer,Integer> contas = new HashMap<>();
	public void addConta(int numconta) {
		contas.put(numconta, 0);
	}
	public void depositar(int numconta, int valor) {
		int atual = contas.get(numconta);
		contas.put(numconta, atual+valor);
	}
	public int saldo(int numconta) {
		return contas.get(numconta);
	}
}

