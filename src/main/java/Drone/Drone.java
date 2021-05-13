package Drone;

import java.util.ArrayList;

public class Drone {

    // drone fields
    protected int id;
    protected String ip;
    protected int port;
    protected int[] coordinates;
    protected int battery;
    protected ArrayList<Drone> dronesList;
    protected RestMethods restMethods;

    // rest api base
    public static String restBaseAddress = "http://localhost:1337/drones/";

    //master drone fields
    protected boolean isMaster;
    private MonitorOrders monitorOrders;
    private QuitDrone quitDrone;

    public Drone(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.battery = 100;
        this.dronesList = new ArrayList<>();
        this.coordinates = new int[2];
        this.restMethods = new RestMethods(this);
    }

    public void run(){
        restMethods.initialize();

        // start orders monitor, separate function
        // as a simple drone might need to start the service later
        if (isMaster)
            startOrderMonitor();

        // start quit service
        quitDrone = new QuitDrone(this);
        quitDrone.start();
        System.out.println(this);
    }

    public void stop(){
        restMethods.quit();
        if (monitorOrders != null) {
            monitorOrders.disconnect();
            System.out.println("Drone " + id + " stopped order monitor");
        }
        quitDrone.interrupt();
        System.out.println("Drone " + id + " stopped quit monitor");
    }

    public void startOrderMonitor(){
        monitorOrders = new MonitorOrders(this);
        monitorOrders.start();
    }

    public String getInfo(){
        return (isMaster? "MASTER" : "SIMPLE") + " DRONE" + " Id: " + id +
                "\nIp and port: " + this.ip + ":" + port;
    }

    public String toString(){
        String ret = getInfo() + "\nDrones list: [\n";

        for (Drone d : dronesList)
            ret += "\n" + d.getInfo() + ", \n\n";

        ret += "]";
        return ret;
    }

    public static void main(String[] args) {
        Drone d1 = new Drone(3, "localhost", 5000);
        Drone d2 = new Drone(4, "localhost", 6000);

        d1.run();
        //d2.run();
    }
}
