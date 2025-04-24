package Protocols;

import Classes.Bank;
import Classes.IdempotencyStore;
import Classes.ProcessPayload;
import Classes.RequestValidator;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;


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
			while (true) {
				try {				
					Socket connection = server.accept();
					output = new ObjectOutputStream(connection.getOutputStream());
					input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String msg = input.readLine();
					String[] request = msg.split(";"); 
					String reply = new String();
					System.out.println("Operação recebida:"+msg);
					if(msg.equals("HEARTBEAT")){										
						reply = "OK"												;					
					}
					else if(RequestValidator.ValidateRequest(msg)){	
						msg = msg.replace("REQUEST;","");										
						reply =  processplayload.processData(msg);									
					}
					else{
						reply = "ERROR;Requisição Inválida";
					}											
					output.writeObject(reply);
					output.flush();				
							
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						input.close();
						output.close();										
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			}
		} catch (IOException e2) {
		e2.printStackTrace();
		}/*finally{
			try {
				if(server != null)
				server.close();					
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}*/	
	}

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
		
	}
}