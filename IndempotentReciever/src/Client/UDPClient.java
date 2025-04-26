package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

class UDPClient {
	public UDPClient() {
		System.out.println("UDP Client Bank Started");
		Scanner scanner = new Scanner(System.in);
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			DatagramPacket sendPacket;
			InetAddress inetAddress = InetAddress.getByName("localhost");
			
			byte[] sendMessage;
			byte[] receivemessage = new byte[1024];
						
			//Enviar solicitação para abrir conta
			String message = "REQUEST;create;1;1000";
			sendMessage = message.getBytes();
			sendPacket = new DatagramPacket(
						sendMessage, sendMessage.length,
						inetAddress, 8080);
			clientSocket.send(sendPacket);
			
			//Receber resposta do Servidor - Conta Aberta
			DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
			clientSocket.receive(receivepacket);
			message = new String(receivepacket.getData());
			System.out.println(message);
			
			//Enviar solicitação para depositar
			message = "REQUEST;deposit;1;1000";
			sendMessage = message.getBytes();
			sendPacket = new DatagramPacket(
							sendMessage, sendMessage.length,
							inetAddress, 8080);
			clientSocket.send(sendPacket);
			
			//Receber resposta do Servidor - Deposito realizado
			Arrays.fill(receivemessage, (byte)0);
			receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
			clientSocket.receive(receivepacket);
			message = new String(receivepacket.getData());
			System.out.println(message);
			
			//Enviar solicitação para obter saldo
			message = "REQUEST;balance;1;0";
			sendMessage = message.getBytes();
			sendPacket = new DatagramPacket(
							sendMessage, sendMessage.length,
							inetAddress, 8080);
			clientSocket.send(sendPacket);
			
			//Receber resposta do Servidor - Saldo obtido
			Arrays.fill(receivemessage, (byte)0);
			clientSocket.receive(receivepacket);
			message = new String(receivepacket.getData());
			System.out.println(message);
			clientSocket.close();
		} catch (IOException ex) {
		}
		System.out.println("UDP Client Terminating ");
	}
	public static void main(String args[]) {
		new UDPClient();
	}
}