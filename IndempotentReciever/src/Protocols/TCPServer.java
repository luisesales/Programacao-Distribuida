package Protocols;

import Classes.Bank;
import Classes.ProcessPayload;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPServer {
	private static int PORT;

	public TCPServer(int port){
		PORT = port;
	}
	public void RunServer(){		
		ObjectOutputStream output = null;
		BufferedReader input = null;
		System.out.println("TCP Server Bank Started");
		ServerSocket server = null;
		Bank bank = new Bank();
		ProcessPayload processplayload = new ProcessPayload(bank);
		try {
			server = new ServerSocket(PORT, 300);
		} catch (IOException e2) {
			e2.printStackTrace();
		}	
		while (true) {
			try {
				Socket conection = server.accept();
				input = new BufferedReader(new InputStreamReader(conection.getInputStream()));
				String msg = input.readLine();
				String[] request = msg.split(";"); 
				String reply = "";
				System.out.println("Operação recebida:"+msg);
				if(msg.equals("HEARTBEAT")){					
					output = new ObjectOutputStream(conection.getOutputStream());
					reply = "HEATBEATREPLY";								
					output.writeObject(reply);
					output.flush();
					
					String reply = (String) input.readObject();
					System.out.println("Gateway response: " + reply);;
					input = new ObjectInputStream(conexao.getInputStream());
					String reply = (String) input.readObject();
					System.out.println("Gateway response: " + reply);
				}
				else if(msg.equals("REQUEST")){
					reply =  processplayload.processData(msg);				
					
				}
				PrintWriter output = new PrintWriter(conection.getOutputStream(), true);
				output.println("Gateway response: " + reply);
				output.flush();
				conection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void InitServer(){
		Socket conexao = null;
		ObjectOutputStream output = null;
		ObjectInputStream input = null;
		try {
			System.out.println("TCP Server Instance Started");
			conexao = new Socket("localhost", 8081);
			output = new ObjectOutputStream(conexao.getOutputStream());
			String request = "INIT SERVER;localhost;"+PORT;			
			output.writeObject(request);
			output.flush();
			input = new ObjectInputStream(conexao.getInputStream());
			String reply = (String) input.readObject();
			System.out.println("Gateway response: " + reply);
			RunServer();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}