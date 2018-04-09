import java.io.IOException;

public class ServerListener extends  Thread {
    public void run() {
        while(true) {
            try {
                String msg = (String) Client.sInput.readObject();
                System.out.println(msg);
                System.out.print("> ");
            }
            catch(IOException e) {
                System.out.println("Server has closed the connection: " + e );
                break;
            }
            catch(ClassNotFoundException e2) {
            }
        }
    }
}

