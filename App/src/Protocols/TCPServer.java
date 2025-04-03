package Protocols;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import Classes.Bank;
import Classes.ProcessPayload;

public class TCPServer {
	public static void main(String args[]) {
		System.out.println("TCP Server Bank Started");
		ServerSocket server = null;
		Bank bank = new Bank();
		ProcessPayload processplayload = new ProcessPayload(bank);
		try {
			server = new ServerSocket(8080, 300);
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
			}
		}
	}
}