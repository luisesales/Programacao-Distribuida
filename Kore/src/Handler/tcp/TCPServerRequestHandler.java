package handler.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import handler.interfaces.ServerRequestHandlerInterface;
import invoker.Invoker;

public class TCPServerRequestHandler implements ServerRequestHandlerInterface {
    private ServerSocket serverSocket;

    private final ExecutorService executorService;
    
    public TCPServerRequestHandler(int port, Invoker invoker){
        start(port);

        this.executorService = Executors.newCachedThreadPool();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new TCP_RequestHandler(clientSocket, invoker));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    break;
                }
            }finally{
                serverSocket.close();
            }
        }
    }
    
    public void start(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
