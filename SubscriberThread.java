import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SubscriberThread extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket connection;
    private String input;

    public SubscriberThread(Socket connection, String input) {
        this.connection = connection;
        this.input = input;
    }

    public void run() {

        try {
            System.out.println("Connected to server, waiting for information for bus: " + input);
            out = new ObjectOutputStream(connection.getOutputStream());
            out.writeObject(input);
            out.flush();

            in = new ObjectInputStream(connection.getInputStream());
            String answer = (String) in.readObject();
            while (!answer.equals("exit")){
                System.out.println("test");
                System.out.println(answer);
                try {
                    sleep(5000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                answer = (String) in.readObject();
            }

            in.close();
            out.close();
            connection.close();

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

}
