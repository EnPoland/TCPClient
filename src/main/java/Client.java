import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    protected static ObjectInputStream sInput;
    protected static ObjectOutputStream sOutput;
    private Socket socket;
    private String server, username;
    private int port;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        }catch (Exception ex){
            return false;
        }
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(msg);

        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            System.out.println("Exception creating new Input/output Streams: " + e);
            return false;
        }
        new ServerListener().start();
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            System.out.println(("Exception doing login : " + eIO));
            disconnect();
            return false;
        }
        return true;
    }

    void sendMessage(Message msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }

    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {}

    }

    public static void main(String[] args) {
        int portNumber = 8888;
        String serverAddress = "localhost";
        String userName = "anon";
        Scanner scan = new Scanner(System.in);

            System.out.println("Enter the username: ");

        userName = scan.nextLine();
        switch(args.length) {
            case 3:
                serverAddress = args[2];
            case 2:
                try {
                    portNumber = Integer.parseInt(args[1]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Try this > java Client [username] [portNumber] [serverAddress]");
                    return;
                }
            case 1:
                userName = args[0];
            case 0:
                break;
            default:
                System.out.println("Try this > java Client [username] [portNumber] [serverAddress]");
                return;
        }

        Client client = new Client(serverAddress, portNumber, userName);
        if(!client.start())
            return;
        System.out.println("Hi");
        System.out.println("Type '@username<space>yourmessage' to send message to some client");
        System.out.println("Type 'LIST' to see list of active clients");
        System.out.println("Type 'LOGOUT' to logoff from server");

        while (true){
            System.out.print("> ");
            String msg = scan.nextLine();
            if(msg.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new Message(Message.LOGOUT, ""));
                break;
            }
            else if(msg.equalsIgnoreCase("LIST")) {
                client.sendMessage(new Message(Message.LIST, ""));
            }
            else {
                client.sendMessage(new Message(Message.MESSAGE, msg));
            }
        }
        scan.close();
        client.disconnect();
    }

}
