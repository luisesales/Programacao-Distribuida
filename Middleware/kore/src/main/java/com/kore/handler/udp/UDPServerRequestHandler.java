package com.kore.handler.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kore.handler.interfaces.ServerRequestHandlerInterface;
import com.kore.invoker.Invoker;

public class UDPServerRequestHandler implements ServerRequestHandlerInterface {
    private DatagramSocket serverSocket;

    private final ExecutorService executorService;

    public void start(int port) {
        try {
            this.serverSocket = new DatagramSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public UDPServerRequestHandler(int port, Invoker invoker){
        start(port);

        this.executorService = Executors.newVirtualThreadPerTaskExecutor();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] receiveMessage = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);
                System.out.println("Waiting for UDP Datagram Packets");
                serverSocket.receive(receivePacket);
                executorService.execute(new UDPRequestHandler(receivePacket,serverSocket, invoker));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    break;
                }
            }finally{
                serverSocket.close();
            }
        }
    }
    
    
}

