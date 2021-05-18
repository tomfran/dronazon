package Drone;

import Grpc.GetInfoClient;
import Grpc.SendInfoClient;
import Grpc.GrpcServer;
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
    protected boolean isAvailable;
    // drones list LOCK as a lot of threads need
    // to access to it concurrently
    protected boolean listLock = false;

    // rest api base
    public static String restBaseAddress = "http://localhost:1337/drones/";

    // master drone fields and orders thread
    protected boolean isMaster;
    private MonitorOrders monitorOrders;
    protected OrderQueue orderQueue;

    // threads
    private GrpcServer grpcServer;
    private QuitDrone quitDrone;

    public Drone(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.battery = 100;
        this.dronesList = new ArrayList<>();
        this.coordinates = new int[2];
        this.restMethods = new RestMethods(this);
        this.isAvailable = true;
    }

    public Drone(int id, String ip, int port, int[] coordinates, int battery, boolean isMaster, boolean isAvailable) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.coordinates = coordinates;
        this.battery = battery;
        this.isMaster = isMaster;
        this.isAvailable = isAvailable;
    }

    public void run(){

        // make rest request
        if (!restMethods.initialize()){
            System.out.println("An error occurred initializing drone with these specs " + getInfo());
            return;
        }

        // start quit service
        quitDrone = new QuitDrone(this);
        quitDrone.start();

        // start grpc server to respond
        grpcServer = new GrpcServer(this);
        grpcServer.start();

        // send everyone my informations
        sendDroneInfo();

        // becomeMaster, it is a separate function
        // as one might become it later
        if (isMaster)
            becomeMaster();
    }

    public void becomeMaster(){
        // request drones infos
        requestDronesInfo();
        System.out.println("Info requested");
        // start the order queue
        orderQueue = new OrderQueue(this);
        monitorOrders = new MonitorOrders(this, orderQueue);

        orderQueue.start();
        System.out.println("Queue started");
        // start the order monitor mqtt client
        monitorOrders.start();
    }

    public synchronized void lockDronesList(){
        while(listLock) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Drone with id " + id + " was not able to wait drone list lock");
                e.printStackTrace();
            }
        }
        listLock = true;
    }

    public synchronized void unlockDronesList(){
        listLock = false;
        notify();
    }

    public void requestDronesInfo() {
        // list of threads to then stop them
        ArrayList<GetInfoClient> threadList = new ArrayList<>();

        // create a thread for each drone in the list
        // and start requesting infos
        // after receiving the response each thread proceeds to star the updateDrone
        // procedure, to update the drone information in the droneslist
        int i = 0;
        for ( Drone d : dronesList ) {
            threadList.add(
                    new GetInfoClient(this, d, i)
            );

            threadList.get(i).start();
            i++;
        }

        for ( GetInfoClient t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println(this);
    }

    public void sendDroneInfo(){
        // list of threads to then stop them
        ArrayList<SendInfoClient> threadList = new ArrayList<>();

        /*
        create a thread for each drone in the list
        and start sending infos
        after receiving the response each thread proceeds to star the updateMaster
        to find out who's the master drone, it will come in handy in case
        the master fails
         */

        int i = 0;
        for ( Drone d : dronesList ) {
            threadList.add(
                    new SendInfoClient(this, d)
            );

            threadList.get(i).start();
            i++;
        }

        for ( SendInfoClient t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(this);

    }

    // add new drone to the list, called after receiving info from a new drone
    public void addNewDrone(DroneService.SenderInfoRequest value){
        // TODO if a simple drone receive this, he might now need to
        // save all this infos and stick to the simple id, port, ip constructor

        lockDronesList();
        dronesList.add(new Drone(
                value.getId(),
                value.getIp(),
                value.getPort(),
                new int[]{value.getPosition().getX(), value.getPosition().getY()},
                value.getResidualBattery(),
                value.getIsMaster(),
                value.getAvailable()
        ));
        unlockDronesList();
    }

    // called when the get info request does not go right,
    // I should start remaking the ring
    public void invalidateDrone(int listIndex) {
        // method used to invalidate a drone entry
        lockDronesList();
        Drone d = dronesList.get(listIndex);
        d.coordinates[0] = -1;
        d.coordinates[1] = -1;
        unlockDronesList();
    }

    // called after a inforesponse
    public synchronized void updateDrone(DroneService.InfoResponse value, int listIndex) {
        // concurrent access to the drone list, need sync
        lockDronesList();
        Drone d = dronesList.get(listIndex);
        d.coordinates[0] = value.getPosition().getX();
        d.coordinates[1] = value.getPosition().getY();
        d.battery = value.getResidualBattery();
        d.isMaster = value.getIsMaster();
        d.isAvailable = value.getAvailable();
        unlockDronesList();
    }

    // called when receiving response after a info send
    // need this field in case the master drone fails
    public synchronized void updateMasterDrone(DroneService.SenderInfoResponse value){
        lockDronesList();
        int id = value.getId();
        boolean isMaster = value.getIsMaster();
        for ( Drone d : dronesList ) {
            if (d.getId() == id)
                d.isMaster = isMaster;
        }
        unlockDronesList();
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getPort() {
        return port;
    }

    public String getInfo(){
        return (isMaster? "MASTER" : "SIMPLE") + " DRONE" + " Id: " + id +
                "\nIp and port: " + this.ip + ":" + port +
                "\nCoordinates: (" + this.getX() + ", " + this.getY() + ")" +
                "\nBattery: " + this.battery +
                "\nAvailable: " + this.isAvailable;
    }

    public String toString(){
        String ret = getInfo() + "\nDrones list: [\n";

        for (Drone d : dronesList)
            ret += "\n" + d.getInfo() + ", \n\n";

        ret += "]";
        return ret;
    }

    public static void main(String[] args) throws InterruptedException {
        Drone d1 = new Drone(1, "localhost", 5000);
        Drone d2 = new Drone(2, "localhost", 6000);
        Drone d3 = new Drone(3, "localhost", 7000);
        Drone d4 = new Drone(4, "localhost", 8000);

        d1.run();
        d2.run();
        d3.run();
        d4.run();
    }
}
