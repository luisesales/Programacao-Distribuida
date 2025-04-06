package Classes;

import java.net.InetAddress;

public class Server {

    private String name;
    private int ip;
    private int port;

    public Server(int ip, int port, String name){
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getIP(){
        return this.ip;
    }

    public int getPort(){
        return this.port;
    }

    public InetAddress getInetAddress(){
        try {
            // Converte o inteiro em um array de bytes
            byte[] ipBytes = new byte[] {
                (byte) ((ip >> 24) & 0xFF),
                (byte) ((ip >> 16) & 0xFF),
                (byte) ((ip >> 8) & 0xFF),
                (byte) (ip & 0xFF)
            };

            // Retorna o array de bytes em InetAddress
            return InetAddress.getByAddress(ipBytes);
                        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
