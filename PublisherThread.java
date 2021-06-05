import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic publisher thread
 * Reads bus positions and sends them to broker
 */
public class PublisherThread extends Thread  {
    ObjectOutputStream out;
    ObjectInputStream in;
    private Socket connection;

    private static String path1 = "Dataset/busPositions.txt";
    private List<Bus> buses;

    public PublisherThread(Socket connection, List<Bus> buses) {
        this.connection = connection;
        this.buses = buses;
    }

    /**
     * Starts sending data to already established connection
     */
    public void run()  {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            try{
                BufferedReader br = new BufferedReader(new FileReader(path1));
                String str;
                while((str = br.readLine()) != null) {
                    String[] line = str.split("\\,");
                    for (int i = 0; i < line.length; i++){
                        line[i] = line[i].trim();
                    }

                    String LineCode = line[0];
                    String routeCode = line[1];
                    String vehicleID = line[2];
                    String latitude = line[3];
                    String longitude = line [4];
                    String timeStampOfBusPosition = line[5];

                    //Checks if current bus is already on our list
                    int i = checkIfExists(LineCode);
                    if (i != -1){
                        buses.get(i).value.setRouteCode(routeCode);
                        buses.get(i).value.setVehicleID(vehicleID);
                        buses.get(i).value.setLatitude(latitude);
                        buses.get(i).value.setLongitude(longitude);
                        buses.get(i).value.setTimeStampOfBusPosition(timeStampOfBusPosition);

                        //Sends data of current bus
                        out.writeObject(buses.get(i).info());
                        out.flush();
                    } else {
                        Bus bus = new Bus();

                        //Keeps the last information taken
                        bus.value.setRouteCode(routeCode);
                        bus.value.setVehicleID(vehicleID);
                        bus.value.setLatitude(latitude);
                        bus.value.setLongitude(longitude);
                        bus.value.setTimeStampOfBusPosition(timeStampOfBusPosition);
                        buses.add(bus);

                        //Sends data of current bus
                        out.writeObject(bus);
                        out.flush();
                    }

                    //This is made to emulate real time bus moving times
                    sleep((long)(Math.random() * 2000));

                }
            }catch(IOException | InterruptedException e){
                e.printStackTrace();
            }
            //When exit is send to broker he stops receiving data from publisher
            out.writeObject("exit");
            out.flush();

            //Closes connection
            out.close();
            in.close();
            connection.close();
        } catch (IOException e) {
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