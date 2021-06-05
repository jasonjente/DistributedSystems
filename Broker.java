import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Broker {

    //Holds the active activeTopics
    static List<String> activeTopics = new ArrayList<String>();
    //Holds number of active subscriptions on each topic, if it's 0 the publisher doesn't send data
    static int[] subscriptions = new int[200];
    //If subscriptions[i] > 0 then the bus position of bus with topic i is stored on topicBusPosition[i] so it can be sent later to subscriber
    static List<String>[] topicBusPositions = new ArrayList[200];

    /**
     * Starts a thread that receives data from producer and a thread that sends data to subscriber
     */
    public void startServers(){
        //k should be equal to biggest topic + 1
        int k = 200;
        for (int i = 0; i < k; i++){
            topicBusPositions[i] = new ArrayList<String>();
            subscriptions[i] = 0;
        }

        Thread PubServers = new Thread(){
            public void run(){
                new Broker().openPublisherServer();
            }
        };
        Thread SubServers = new Thread(){
            public void run(){
                new Broker().openSubscriberServer();
            }
        };

        PubServers.start();
        SubServers.start();
    }

    /**
     * Opens server that sends data to subscribers via thread
     */
    private void openSubscriberServer() {
        ServerSocket subscriberSocket1 = null;

        try {
            //Create the Server Socket.
            System.out.println("Subscriber Server is waiting for connections...");

            subscriberSocket1 = new ServerSocket(4000);

            while (true) {
                Socket connection = null;

                //Accept Connection and start Thread.
                connection = subscriberSocket1.accept();
                System.out.println("Subscriber Connected!");
                //Starts dedicated thread based on the connection with the subscriber
                Thread subBrokerThread = new SubBrokerThread(connection);
                subBrokerThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                subscriberSocket1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens server that receives data from publishers via thread
     */
    private void openPublisherServer(){
        ServerSocket publisherSocket = null;
        try {
            //Create the Server Socket.
            System.out.println("Publisher Server is waiting for connections...");

            publisherSocket = new ServerSocket(2000);
            while (true) {
                Socket connection = null;
                //Accept Connection and start Thread.
                connection = publisherSocket.accept();
                System.out.println("Publisher Connected!");
                //Starts dedicated thread based on the connection with the publisher
                Thread pubBrokerThread = new PubBrokerThread(connection);
                pubBrokerThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                publisherSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}