package Instances;

import Protocols.TCPServer;

public class Instance3 {
    public static void main(String[] args) {
        //UDPServer udp = new UDPServer("9003");
        TCPServer tcp = new TCPServer(8092);
    }
}
