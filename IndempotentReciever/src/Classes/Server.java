package Classes;

public class Server {

    private String name;
    private int ip;
    private int port;

    public Server(int ip, int port, String name){
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public String GetName(){
        return this.name;
    }
}
