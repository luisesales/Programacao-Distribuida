package Instances;

import Protocols.*;

public class Instance1 {
    public static void main(String[] args) {
        /*UDPServer udp = new UDPServer("9003");
        udp.InitServer();*/
        TCPServer tcp = new TCPServer(8090);
        tcp.InitServer();
    }
}
