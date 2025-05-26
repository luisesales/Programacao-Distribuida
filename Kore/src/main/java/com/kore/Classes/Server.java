package com.kore.Classes;

import java.net.InetAddress;

public class Server {

    private String name;
    private String ip;
    private int port;

    public Server(String ip, int port, String name){
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getIP(){
        return this.ip;
    }

    public int getPort(){
        return this.port;
    }

    public InetAddress getInetAddress(){
        try {
            return InetAddress.getByName(ip);                        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
