package com.kore.handler.tcp;

import com.kore.exceptions.ServerRequestHandlerException;
import com.kore.marshaller.HttpMarshaller;
import com.kore.invoker.Invoker;
import com.kore.exceptions.BadConstructorException;
import com.kore.exceptions.BadConstructorException;
import com.kore.httpmessage.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class TCPRequestHandler implements Runnable {
    private final Socket clientSocket;

    private final Invoker invoker;

    private final HttpMarshaller marshaller;

    TCPRequestHandler(Socket clientSocket, Invoker invoker) {
        this.clientSocket = clientSocket;
        this.invoker = invoker;
        this.marshaller = new HttpMarshaller();
    }

    @Override
    public void run() {
        handle();
    }

    @Override
    public void handle() {
        HttpRequestModel request = readRequest();
        if (request == null) {
            sendResponse(null);
            return;
        }

        HttpResponseModel response;
        try {
            response = invoker.invoke(request);
        } catch (InvocationTargetException | IllegalAccessException |
                 BadConstructorException e) {
            throw new ServerRequestHandlerException(e.getMessage());
        }

        sendResponse(response);
    }

    private void sendResponse(HttpResponseModel response) {
        try {
            if(response == null) {
                response = new HttpResponseModel();
                response.setStatusCode(500);
                response.setStatusMessage("Internal Server Error");
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Content-Length", String.valueOf(
                    response.getBody() != null ? response.getBody().getBytes().length : 0));
            response.setHeaders(headers);
            
            
            String httpResponse = marshaller.serialize(response);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            writer.write(httpResponse);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ServerRequestHandlerException("Error sending HTTP response: " + e.getMessage());
        }
    }

    private HttpRequestModel readRequest() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            StringBuilder requestBuilder = new StringBuilder();
            String inputLine = reader.readLine();

            if (inputLine == null || inputLine.isEmpty()) {
                return null;
            }

            requestBuilder.append(inputLine).append("\r\n");
            
            int contentLength = 0;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                requestBuilder.append(body);
            }

            String httpRequest = requestBuilder.toString();
            return marshaller.deserialize(httpRequest);
        } catch (IOException e) {
            throw new ServerRequestHandlerException("Error reading HTTP request" + e.getMessage());
        }
    }
}