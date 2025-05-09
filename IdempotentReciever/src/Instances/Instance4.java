package Instances;

import Protocols.TCPServer;

public class Instance4 {
    public static void main(String[] args) {
        //UDPServer udp = new UDPServer("9003");
        TCPServer tcp = new TCPServer(8093);
        tcp.InitServer();
    }
}
