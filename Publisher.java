import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Publisher {

    //List of the buses we will work with, each time a bus is created it's stored here
    private List<Bus> buses = new ArrayList<>();

    private static  String path2 = "Dataset/busLines.txt";  //buslines.txt
    private static String path3 = "Dataset/RouteCodes.txt"; //Routecodes.txt

    public static void main(String[] args) {
        new Publisher().openServer();
    }

    /**
     * Establishes connection and starts thread that reads & sends data to broker
     */
    private void openServer() {
        Socket requestSocket = null;

        try {
            //2000 connects with broker
            requestSocket = new Socket("127.0.0.1", 2000);

            readAll();

            Thread publisherThread = new PublisherThread(requestSocket, buses);
            publisherThread.start();

            //In case that the servers aren't up
        } catch (ConnectException e){
            System.out.println("Searching for ports...");
            openServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads everything apart from bus positions the text files so we can have basic information about the buses
     * This basic information is stored by the Bus class
     */
    private void readAll(){
        readBusLines();
        readRouteCodes();
    }

    /**
     * Reads bus Lines data
     */
    private void readBusLines(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(path2));
            String str;
            while((str = br.readLine()) != null) {
                String[] line = str.split("\\,");
                for (int i = 0; i < line.length; i++){
                    line[i] = line[i].trim();
                }

                String LineCode = line[0];
                String LineId = line[1];    //Topic
                String LineName = line[2];

                //LineCode is and should be unique
                if (checkIfExists(LineCode) != -1){
                    System.out.println("Already exists");
                } else {
                    Bus bus = new Bus();

                    bus.topic.setTopic(LineId);
                    bus.value.setLineCode(LineCode);
                    bus.value.setLineName(LineName);

                    buses.add(bus);
                }

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Reads route code data
     */
    private void readRouteCodes(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(path3));
            String str;
            while((str = br.readLine()) != null) {

                String[] line = str.split("\\,");


                for (int i = 0; i < line.length; i++){
                    line[i] = line[i].trim();
                }

                String RouteCode = line[0];
                String LineCode = line[1];
                String RouteType = line [2];
                String LineName = line[3];

                int i = checkIfExists(LineCode);
                //Bus already exists in our data so we just update the information
                if (i != -1){
                    buses.get(i).value.setRouteCode(RouteCode);
                    buses.get(i).value.setRouteType(RouteType);

                } else {
                    //Bus doesn't exist so we create a new one and store given information
                    Bus bus = new Bus();

                    bus.value.setLineCode(LineCode);
                    bus.value.setLineName(LineName);
                    bus.value.setRouteCode(RouteCode);
                    bus.value.setRouteType(RouteType);
                    buses.add(bus);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * A simple method that checks if a bus already exists in our data based on the unique lineCode
     * @return it's position in our list if it exists, otherwise -1
     */
    private int checkIfExists(String lineCode){
        for (int i = 0; i < buses.size(); i++){
            if (buses.get(i).value.getLineCode().equals(lineCode)){
                return i;
            }
        }
        return -1;
    }

}