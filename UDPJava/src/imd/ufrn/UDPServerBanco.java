package imd.ufrn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.StringTokenizer;

public class UDPServerBanco {
	private Banco banco;
	public UDPServerBanco(String port) {
		banco = new Banco();
		String operacao =null;
		int conta = 0;
		int valor = 0;
		System.out.println("UDP Server Bank started");
		try {
			DatagramSocket serversocket = new DatagramSocket(Integer.parseInt(port));
			String resultadoOp;
			while (true) {
				byte[] receivemessage = new byte[1024];
				DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
				serversocket.receive(receivepacket);
				String message = new String(receivepacket.getData());
				resultadoOp=message;
				StringTokenizer tokenizer = new StringTokenizer(message, ";");
			    while (tokenizer.hasMoreElements()) {
			        operacao = tokenizer.nextToken();
			        conta = Integer.parseInt(tokenizer.nextToken());
			        valor = Integer.parseInt(tokenizer.nextToken().trim());
			    }
			    switch (operacao) {
			        case "criar":
			        	banco.addConta(conta);
			        	break;
			        case "depositar": 
			        	banco.depositar(conta, valor);
			        	break;
			        case "saldo":
			        	resultadoOp = "R$"+banco.saldo(conta);
			        	break;
			        }
				System.out.println("Operacao realizada:"+operacao+ "-"+ banco.saldo(conta) +"-"+ receivepacket.getAddress());
				String reply = "Confirmo Recebimento de:"+resultadoOp;
				byte[] replymsg = reply.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(replymsg,replymsg.length,
						receivepacket.getAddress(),receivepacket.getPort());
				serversocket.send(sendPacket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("UDP Bank server terminating");
	}
	public static void main(String[] args) {
		new UDPServerBanco(args[0]);
	}
}
