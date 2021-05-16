package Drone;

import GrpcDrone.GrpcGetInfoClientDrone;
import GrpcDrone.GrpcServerDrone;
import com.drone.grpc.DroneService;

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

    // master drone fields and orders thread
    protected boolean isMaster;
    private MonitorOrders monitorOrders;

    // threads
    private GrpcServerDrone grpcServer;
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
        // make rest request
        restMethods.initialize();

        // start quit service
        quitDrone = new QuitDrone(this);
        quitDrone.start();

        // start grpc server to respond
        grpcServer = new GrpcServerDrone(this);
        grpcServer.start();

        System.out.println(this);

        // becomeMaster, it is a separate function
        // as one might become it later
        if (isMaster)
            becomeMaster();

    }

    public void becomeMaster(){
        monitorOrders = new MonitorOrders(this);
        //monitorOrders.start();

        try {
            System.out.println("MASTER DORME PER 10 SECONDI, id " + id);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("SLEEP FALLITo");
            e.printStackTrace();
        }
        // request drones infos
        requestDronesInfo();

    }

    public void requestDronesInfo() {
        // list of threads to then stop them
        ArrayList<GrpcGetInfoClientDrone> threadList = new ArrayList<>();

        // create a thread for each drone in the list
        // and start requesting infos
        // after receiving the response each thread proceeds to star the updateDrone
        // procedure, to update the drone information in the droneslist
        int i = 0;
        for ( Drone d : dronesList ) {
            threadList.add(
                    new GrpcGetInfoClientDrone(this, d, i)
            );

            threadList.get(i).start();
            i++;
        }

        for ( GrpcGetInfoClientDrone t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(this);
    }

    // todo, send infomation to everyone after registration
    public void sendDroneInfo(){}

    // todo, add a new registered drone
    public void addNewDrone(){}

    public void invalidateDrone(int listIndex) {
        // method used to invalidate a drone entry
        Drone d = dronesList.get(listIndex);
        d.coordinates[0] = -1;
        d.coordinates[1] = -1;
    }

    public synchronized void updateDrone(DroneService.InfoResponse value, int listIndex) {
        // concurrent access to the drone list, need sync
        Drone d = dronesList.get(listIndex);
        d.coordinates[0] = value.getPosition().getX();
        d.coordinates[1] = value.getPosition().getY();
        d.battery = value.getResidualBattery();
        d.isMaster = value.getIsMaster();
    }

    public void stop() {
        restMethods.quit();
        if (monitorOrders != null) {
            monitorOrders.disconnect();
            System.out.println("Drone " + id + " stopped order monitor");
        }
        quitDrone.interrupt();
        System.out.println("Drone " + id + " stopped quit monitor");
        grpcServer.interrupt();
        System.out.println("Drone " + id + " stopped grpc monitor");
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getX() {
        return coordinates[0];
    }

    public int getY() {
        return coordinates[1];
    }

    public int getBattery() {
        return battery;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public int getPort() {
        return port;
    }

    public String getInfo(){
        return (isMaster? "MASTER" : "SIMPLE") + " DRONE" + " Id: " + id +
                "\nIp and port: " + this.ip + ":" + port +
                "\nCoordinates: (" + this.getX() + ", " + this.getY() + ")" +
                "\nBattery: " + this.battery;
    }

    public String toString(){
        String ret = getInfo() + "\nDrones list: [\n";

        for (Drone d : dronesList)
            ret += "\n" + d.getInfo() + ", \n\n";

        ret += "]";
        return ret;
    }

    public static void main(String[] args) {
        Drone d1 = new Drone(1, "localhost", 5000);
        Drone d2 = new Drone(2, "localhost", 6000);
        Drone d3 = new Drone(3, "localhost", 7000);
        Drone d4 = new Drone(4, "localhost", 8000);

        d1.isMaster = false;
        d2.isMaster = false;
        d3.isMaster = false;
        d4.isMaster = true;

        d1.run();
        d2.run();
        d3.run();
        d4.run();
    }
}
