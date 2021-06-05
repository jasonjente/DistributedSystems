import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Subscriber extends Thread {

    private boolean hasAsked = false;
    private String input = "exit";

    public static void main(String args[]) {
        //Add the start point as the first argument and the destination as the second
        new Subscriber().openServer();

    }

    private void openServer(){
        Socket requestSocket = null;
        try {

            if (!hasAsked){
                Scanner scan = new Scanner(System.in);
                System.out.println("Type the bus you want to see information of: ");
                System.out.println("Type exit if you want to quit.");
                input = scan.next();
                if (input.equals("exit")){
                    System.exit(2);
                }
                System.out.println("Asking for bus: " + input);
                hasAsked = true;
            }


            //port 4000 connects with broker
            requestSocket = new Socket("127.0.0.1", 4000);

            Thread subscriberThread = new SubscriberThread(requestSocket, input);
            subscriberThread.start();


        } catch (ConnectException e){
            System.out.println("Searching for ports...");
            try {
                sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            openServer();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}