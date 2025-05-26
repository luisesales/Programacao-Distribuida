package com.kore.handler.udp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kore.handler.interfaces.ServerRequestHandlerInterface;
import com.kore.invoker.Invoker;

public class TCPServerRequestHandler implements ServerRequestHandlerInterface {
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

        this.executorService = Executors.newCachedThreadPool();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] receiveMessage = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);
                executorService.execute(new UDPRequestHandler(receivePacket,serverSocket, invoker));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    break;
                }
            }
        }
    }
    
    
}

