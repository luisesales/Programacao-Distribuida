package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Kore {
	private final int MAX_CONNECTIONS = 150;
    private static int ALIVE_TIMEOUT = 5000;

	public void Start(){
		System.out.println("Kore Middlware Started");

		try (ServerSocket server = new ServerSocket(8080, MAX_CONNECTIONS)) {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
			try{
				while (true) {

					System.out.println("Waiting for client request"+server.getInetAddress());

					Socket remote = server.accept();
					
					executor.execute(new ClientHandler(remote));

				}
			}finally {
                executor.shutdown();
                server.close();
                System.out.println("Kore terminating");
            }

		} catch (IOException ex) {

			ex.printStackTrace();

		}
	}

	public static void main(String args[]) {
		 Kore middleware = new Kore();
		 middleware.Start();
	}
}
