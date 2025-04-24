package Protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

import Classes.*;

public class UDPServer {
	private final int PORT;
	private Bank bank;
	private ProcessPayload payload;
	public UDPServer(String port) {
		PORT = Integer.parseInt(port);
	}

	public void InitServer(){
		
		/*
		 public void InitServer(){
		Socket connection = null;
		ObjectOutputStream output = null;
		ObjectInputStream input = null;
		try {
			System.out.println("TCP Server Instance Started");
			connection = new Socket("localhost", 8080);
			output = new ObjectOutputStream(connection.getOutputStream());
			String request = "INIT_SERVER;"+connection.getInetAddress().getHostAddress()+";"+PORT;			
			output.writeObject(request);
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			String reply = (String) input.readObject();
			System.out.println("Gateway response: " + reply);		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			try {
				input.close();
				output.close();
			    connection.close();
				RunServer();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		 */
		DatagramSocket serverSocket;
		DatagramPacket sendPacket;
		try {
			serverSocket = new DatagramSocket(PORT);			
			InetAddress inetAddress = InetAddress.getByName("localhost");
			String request = "INIT_SERVER;"+serverSocket.getInetAddress().getHostAddress()+";"+PORT;
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				receivePacket;
				output.close();
			    connection.close();
				RunServer();
			} catch (IOException e) {
				e.printStackTrace();
			}	
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