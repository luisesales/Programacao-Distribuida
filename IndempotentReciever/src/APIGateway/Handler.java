package APIGateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class Handler implements Runnable {

    private final Socket socket;

    public Handler(Socket socket , APIGateway gateway) {

        this.socket = socket;

    }

    @Override

    public void run() {

        System.out.println("\nHandler Started for " +

                this.socket);

        handleRequest(this.socket);

        System.out.println("Handler Terminated for "

                + this.socket + "\n");

    }

    public void handleRequest(Socket socket) {

        try (BufferedReader in = new BufferedReader(

                new InputStreamReader(socket.getInputStream()));) {

            String headerLine = in.readLine();

            if(headerLine.equals("INIT SERVER")){
                System.out.println("Starting to Add Server " + socket);
                gateway.AddServer(Integer.parseInt(socket.getInetAddress().toString()),);
            }

            StringTokenizer tokenizer = new StringTokenizer(headerLine);

            String httpMethod = tokenizer.nextToken();

            if (httpMethod.equals("GET")) {

                System.out.println("Get method processed");

                String httpQueryString = tokenizer.nextToken();

                StringBuilder responseBuffer = new StringBuilder();

                responseBuffer

                        .append("<html><h1>WebServer Home Page.... </h1><br>")

                        .append("<b>Bem vindo ao Meu web server! </b><BR>")

                        .append("</html>");

                sendResponse(socket, 200, responseBuffer.toString());

            } else {

                System.out.println("The HTTP method is not recognized");

                sendResponse(socket, 405, "Method Not Allowed");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void sendResponse(Socket socket, int statusCode, String responseString) {

        String statusLine;

        String serverHeader = "Server: WebServer\r\n";

        String contentTypeHeader = "Content-Type: text/html\r\n";

        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {

            if (statusCode == 200) {

                statusLine = "HTTP/1.0 200 OK" + "\r\n";

                String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";

                out.writeBytes(statusLine);

                out.writeBytes(serverHeader);

                out.writeBytes(contentTypeHeader);

                out.writeBytes(contentLengthHeader);

                out.writeBytes("\r\n");

                out.writeBytes(responseString);

            } else if (statusCode == 405) {

                statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";

                out.writeBytes(statusLine);

                out.writeBytes("\r\n");

            } else {

                statusLine = "HTTP/1.0 404 Not Found" + "\r\n";

                out.writeBytes(statusLine);

                out.writeBytes("\r\n");

            }

            out.close();
            socket.close();

        } catch (IOException ex) {

            ex.printStackTrace();

        }

    }

}

