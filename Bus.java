public class Bus{

    Topic topic;
    Value value;

    public Bus(){
        topic = new Topic();
        value = new Value();
    }

    public Bus(Topic topic) {
        this.topic = topic;
    }

    public void printBus(){
        System.out.println("\n\n#############################################");
        System.out.println("Bus with LineId(Topic): " + topic.getTopic() + " LineCode: " + value.getLineCode() + " LineName: " + value.getLineName());
        System.out.println(" RouteCode: " + value.getRouteCode() + " VehicleId " + value.getVehicleID() + " Current Latitude: " + value.getLatitude() + " Current Longitude: " + value.getLongitude());
        System.out.println(" TimeStamp Of Bus Location: " + value.getTimeStampOfBusPosition() + " RouteType: " + value.getRouteType());
    }

    public String toString(){
        String bus;
        bus = "Bus with LineId(Topic): " + topic.getTopic() + " LineCode: " + value.getLineCode() + " LineName: " + value.getLineName() + " RouteCode: " + value.getRouteCode() + " VehicleId " + value.getVehicleID() + " Current Latitude: " + value.getLatitude() + " Current Longitude: " + value.getLongitude() + " TimeStamp Of Bus Location: " + value.getTimeStampOfBusPosition() + " RouteType: " + value.getRouteType();
        return bus;
    }

    public String info(){
        String bus;
        bus = topic.getTopic() + " " + value.getLatitude() + " " + value.getLongitude();
        return bus;
    }

}