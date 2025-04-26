package Protocols;

import Classes.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
	private final int PORT;
	private Bank bank;
	private ProcessPayload payload;
	public UDPServer(String port) {
		PORT = Integer.parseInt(port);
	}

	public void InitServer(){
		
		
		DatagramSocket serverSocket = null;
		DatagramPacket sendPacket;
		try {
			serverSocket = new DatagramSocket(PORT);			
			InetAddress inetAddress = InetAddress.getByName("localhost");
			String request = "INIT_SERVER;";
			byte[] sendMessage;
			sendMessage = request.getBytes();
			sendPacket = new DatagramPacket(
						sendMessage, sendMessage.length,
						inetAddress, 8080);
			serverSocket.send(sendPacket);
			byte[] receivemessage = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receivemessage, receivemessage.length);
			serverSocket.receive(receivePacket);
			String message = new String(receivePacket.getData());
			String reply = payload.processData(message);
			System.out.println("Gateway response: " + reply);
			serverSocket.close();				
			RunServer();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public void RunServer() {
		bank = new Bank();
		payload = new ProcessPayload(bank);
		String operacao =null;
		int conta = 0;
		int valor = 0;
		System.out.println("UDP Server Bank started");
		try {
			DatagramSocket serversocket = new DatagramSocket(PORT);			
			while (true) {
				byte[] receivemessage = new byte[1024];
				DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
				serversocket.receive(receivepacket);
				String msg = new String(receivepacket.getData());				
				String reply = new String();
				System.out.println("Operação recebida:"+msg);
				if(msg.equals("HEARTBEAT")){										
					reply = "OK"												;					
				}
				else if(RequestValidator.ValidateRequest(msg)){
					msg = msg.replace("REQUEST;","");
					reply = payload.processData(msg);
				}
				else{
					reply = "ERROR;Requisição Inválida";
				}	
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