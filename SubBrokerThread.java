import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

/**
 * Broker's thread that sends data to subscriber
 */
public class SubBrokerThread extends Thread {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket connection;
    private String topic;
    private String lastRead = null;
    private String lastReadBusPosition;

    public SubBrokerThread(Socket connection) {
        this.connection = connection;
    }

    public void run() {

        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            //Reading topic requested from subscriber!
            topic = (String) in.readObject();
            System.out.println("SubBrokerThread received : " + topic);
            int temp = Integer.parseInt(topic);
            topic = temp + "";
            //This incrementation means that on the subscriptions array it is shown that there is one more subscriber active on this topic
            Broker.subscriptions[temp]++;

            //Topic is add to the list of active activeTopics
            Broker.activeTopics.add(topic);

            while (true){
                try {
                    sleep((long)(Math.random() * 1000));

                    //if the condition is satisfied, that means that there is new data for the topic asked for
                    if (!Broker.topicBusPositions[temp].isEmpty()){
                        //We store the last bus position we have seen so it won't be sent again
                        lastReadBusPosition = Broker.topicBusPositions[temp].get(0);

                        if (!lastReadBusPosition.equals(lastRead)){
                            lastRead = lastReadBusPosition;

                            //Sends latest bus position
                            out.writeObject(lastRead);
                            out.flush();
                        }
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //Closing everything
        } finally {
            try {
                //Removes user from subscribed topic
                Iterator itr = Broker.activeTopics.iterator();
                while (itr.hasNext()){
                    String x = (String) itr.next();
                    if (x.equals(topic)){
                        itr.remove();
                    }
                }
                //Makes reduces subscribers on subscriptions array by 1
                Broker.subscriptions[Integer.parseInt(topic)]--;

                //Closing connection
                out.close();
                in.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}