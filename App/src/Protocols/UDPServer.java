package Protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.StringTokenizer;

import Classes.*;

public class UDPServer {
	private final int PORT;
	private Bank bank;
	private ProcessPayload payload;
	public UDPServer(String port) {
		PORT = Integer.parseInt(port);
	}
	public void run() {
		bank = new Bank();
		payload = new ProcessPayload(bank);
		String operacao =null;
		int conta = 0;
		int valor = 0;
		System.out.println("UDP Server Bank started");
		try {
			DatagramSocket serversocket = new DatagramSocket(PORT);
			String opResult;
			while (true) {
				byte[] receivemessage = new byte[1024];
				DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
				serversocket.receive(receivepacket);
				String message = new String(receivepacket.getData());
				String reply = payload.processData(message);
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
}