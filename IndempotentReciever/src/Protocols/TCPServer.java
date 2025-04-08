package Protocols;

import Classes.Bank;
import Classes.ProcessPayload;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPServer {
	private static int PORT;

	public TCPServer(int port){
		PORT = port;
		Socket conexao = null;
		ObjectOutputStream output = null;
		ObjectInputStream input = null;
		try {
			System.out.println("TCP Server Instance Started");
			conexao = new Socket("localhost", 8080);
			output = new ObjectOutputStream(conexao.getOutputStream());
			String Msg = "INIT SERVER";			
			output.writeObject(Msg);
			output.flush();
			input = new ObjectInputStream(conexao.getInputStream());
			String msg = (String) input.readObject();
			System.out.println("Retorno do Servidor:"+msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
				output.close();
				conexao.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String args[]) {
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
				BufferedReader input = new BufferedReader(new InputStreamReader(conection.getInputStream()));
				String msg = input.readLine(); 
				System.out.println("Operação recebida:"+msg);
				
				String reply =  processplayload.processData(msg);
				
				PrintWriter output = new PrintWriter(conection.getOutputStream(), true);
				output.println("Server response: " + reply);
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
}