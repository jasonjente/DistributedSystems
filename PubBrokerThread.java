import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Broker's thread that handles data sent from publisher
 */
public class PubBrokerThread extends Thread {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket connection;

    public PubBrokerThread(Socket connection) {
        this.connection = connection;
    }

    public void run() {

        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            //Reading data given from publisher!!
            while (true){
                //The data given by publisher in format topic latitude longitude
                String topic = (String) in.readObject();

                //This is the topic
                int tempTopic = new Scanner(topic).useDelimiter("\\D+").nextInt();

                //Checks if there is at least one active subscriber
                if (Broker.subscriptions[tempTopic] >= 1){
                    while (!Broker.topicBusPositions[tempTopic].isEmpty()){
                        //We wait 1 sec to make sure that the last bus position has been sent to the subscribers before adding the new one
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Broker.topicBusPositions[tempTopic].remove(0);
                    }
                    //Adding the last bus position to be sent to the subscribers
                    Broker.topicBusPositions[tempTopic].add(topic);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //Closing everything
        } finally {
            try {
                out.close();
                in.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}