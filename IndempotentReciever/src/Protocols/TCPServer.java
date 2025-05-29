package Protocols;

import Classes.Bank;
import Classes.ProcessPayload;
import Classes.RequestValidator;
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
		PrintWriter output = null;
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
					output = new PrintWriter(connection.getOutputStream(),true);
					input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String msg = input.readLine();					
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
					output.println(reply);
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
		PrintWriter output = null;
		BufferedReader input = null;
		try {
			System.out.println("TCP Server Instance Started");
			connection = new Socket("localhost", 8080);
			output = new PrintWriter(connection.getOutputStream(),true);
			String request = "INIT_SERVER;"+connection.getInetAddress().getHostAddress()+";"+PORT;			
			output.println(request);
			output.flush();
			input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String reply = input.readLine();
			System.out.println("Gateway response: " + reply);	
			input.close();
			output.close();
			connection.close();
			RunServer();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
}