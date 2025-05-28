package com.kore.handler.udp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import com.kore.exceptions.BadConstructorException;
import com.kore.exceptions.ServerRequestHandlerException;
import com.kore.httpmessage.HttpRequestModel;
import com.kore.httpmessage.HttpResponseModel;
import com.kore.invoker.Invoker;
import com.kore.marshaller.HttpMarshaller;

class UDPRequestHandler implements Runnable {
    private final DatagramPacket clientPacket;
    private final DatagramSocket serverSocket;

    private final Invoker invoker;

    private final HttpMarshaller marshaller;

    UDPRequestHandler(DatagramPacket clientPacket, DatagramSocket serverSocket, Invoker invoker) {
        this.clientPacket = clientPacket;
        this.serverSocket = serverSocket;
        this.invoker = invoker;
        this.marshaller = new HttpMarshaller();
    }

    @Override
    public void run() {
        handle();
    }
    
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
        if (response == null) {
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
        byte[] responseBytes = httpResponse.getBytes();

        DatagramPacket responsePacket = new DatagramPacket(
            responseBytes,
            responseBytes.length,
            clientPacket.getAddress(),
            clientPacket.getPort()
        );        
        
        serverSocket.send(responsePacket);
        serverSocket.close();

    } catch (IOException e) {
        throw new ServerRequestHandlerException("Error sending HTTP response over UDP: " + e.getMessage());
    }
}


    private HttpRequestModel readRequest() {
    try {
        String httpRequest = new String(clientPacket.getData(), 0, clientPacket.getLength());
        return marshaller.deserialize(httpRequest);
    } catch (Exception e) {
        throw new ServerRequestHandlerException("Error reading HTTP request from UDP packet: " + e.getMessage());
    }
}

}
