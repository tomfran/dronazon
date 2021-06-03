package Drone;

import Grpc.GrpcServer;
import com.drone.grpc.DroneService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Drone implements Comparable<Drone>{

    // drone fields
    protected int id;
    protected String ip;
    protected int port;
    protected Integer[] coordinates;
    protected Integer battery;
    protected DronesList dronesList;
    protected RestMethods restMethods;
    protected Boolean isAvailable;
    protected Drone successor;
    protected Drone predecessor;

    // master drone fields and orders thread
    protected boolean isMaster;
    private MonitorOrders monitorOrders;
    protected OrderQueue orderQueue;

    // threads for quitting, display info and grpc server
    private GrpcServer grpcServer;
    private PingService pingService;
    private QuitDrone quitDrone;
    private PrintDroneInfo printDroneInfo;

    public Drone(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.battery = 100;
        this.dronesList = new DronesList(this);
        this.coordinates = new Integer[2];
        this.restMethods = new RestMethods(this);
        this.isAvailable = true;
        this.successor = null;
        this.predecessor = null;
    }

    public Drone(int id, String ip, int port, Integer[] coordinates, int battery, boolean isMaster, boolean isAvailable) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.coordinates = coordinates;
        this.battery = battery;
        this.isMaster = isMaster;
        this.isAvailable = isAvailable;
    }

    /*
    Start function, the drone initialize and send others
    It's info, if required it becomes master
     */
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

        // send everyone my info
        dronesList.sendDroneInfo();


        // becomeMaster, it is a separate function
        // as one might become it later
        if (isMaster)
            becomeMaster();

        pingService = new PingService(this);
        pingService.start();

        printDroneInfo = new PrintDroneInfo(this);
        printDroneInfo.start();
    }

    /*
    Calling this function a Drone becomes master, so it
    starts to monitor orders and manage the queue
     */
    public void becomeMaster(){
        System.out.println("Becoming the new master");

        /*

        // request drones infos
        dronesList.requestDronesInfo();
        System.out.println("Other drones info requested");
        // start the order queue
        orderQueue = new OrderQueue(this);
        monitorOrders = new MonitorOrders(this, orderQueue);
        orderQueue.start();
        System.out.println("Order queue started");
        // start the order monitor mqtt client
        monitorOrders.start();
        System.out.println("MQTT client started");

         */

    }

    /*
    Called after a quit command,
    it basically stops everything
     */
    public void stop() {

        restMethods.quit();
        grpcServer.interrupt();

        if (isMaster()) {
            monitorOrders.interrupt();
            monitorOrders.disconnect();
        }

        if (isMaster()) {
            orderQueue.interrupt();
        }

        System.exit(0);
    }

    /*
    Enter the ring overlay network,
    The function computes the predecessor and successor,
    to simplify implementation it's called
    every time a drone enters the system
     */
    public void enterRing(){
        ArrayList<Drone> list = dronesList.getDronesList();
        list.add(this);
        Collections.sort(list);

        int i = list.indexOf(this);

        predecessor = (i == 0)? list.get(list.size()-1) : list.get(i-1);
        successor = (i == list.size()-1)? list.get(0) : list.get(i+1);
    }

    /*
    Delivery simulation, the Drone sleeps for 5 seconds,
    then it sends the delivery response,

    TODO add pollution measurements, and count Drone statistics
     */
    public DroneService.OrderResponse deliver(DroneService.OrderRequest request) {
        Integer[] newPosition = new Integer[]{request.getEnd().getX(), request.getEnd().getY()};
        decreaseBattery();

        DroneService.OrderResponse response = DroneService.OrderResponse.newBuilder()
                .setTimestamp(
                        new java.sql.Timestamp(System.currentTimeMillis()).getTime()
                )
                .setNewPosition(
                        DroneService.Coordinates.newBuilder()
                                .setX(newPosition[0])
                                .setY(newPosition[1])
                                .build()
                )
                .setKm(DronesList.distance(getCoordinates(), newPosition))
                .setPollutionAverage(10)
                .setResidualBattery(getBattery())
                .build();


        setCoordinates(newPosition);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\nDelivery completed: \n\tNew position: " + newPosition[0] + " " + newPosition[1]);
        System.out.println("\tResidual battery " + getBattery() + "\n");
        return response;
    }

    /*
    Used to sort the drone list in enter ring
     */
    @Override
    public int compareTo(Drone o) {
        return this.getId() - o.getId() ;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public Integer getX() {
        return coordinates[0];
    }

    public Integer getY() {
        return coordinates[1];
    }

    /*
    Synchronized methods as in delivery search
    the master access the fields concurrently
     */
    public Integer getBattery() {
        int ret;
        synchronized (battery){
            ret = battery;
        }
        return ret;
    }

    public void decreaseBattery() {
        synchronized (battery){
            battery -= 15;
        }
    }

    public int[] getCoordinates(){
        int[] ret = {-1, -1};
        synchronized (coordinates) {
            ret[0] = coordinates[0];
            ret[1] = coordinates[1];
        }
        return ret;
    }

    public void setCoordinates(Integer[] cord){
        synchronized (coordinates){
            coordinates = cord;
        }
    }

    public boolean isAvailable() {
        boolean ret;
        synchronized (isAvailable){
            ret = (boolean) isAvailable;
        }
        return ret;
    }

    public void setAvailable(boolean b) {
        synchronized (isAvailable){
            isAvailable = b;
        }
    }

    public boolean isMaster() {
        return isMaster;
    }

    public DronesList getDronesList() { return dronesList; }

    public int getPort() {
        return port;
    }

    public String getInfo(){
        return (isMaster()? "MASTER\n" : "") + "Id: " + getId() +
                "\nIp and port: " + getIp() + ":" + getPort();
    }

    public String toString(){
        String ret =  "======== DRONE INFO ========\n" + getInfo();
        ret += "\nBattery level: " + getBattery();

        ret += "\n\nOther known drones: [\n";

        for (Drone d : getDronesList().getDronesList())
            ret += "\n" + d.getInfo() + ", \n";

        ret += "\n]";
        return ret + "\n============================\n";
    }

    public static void main(String[] args) {

        Scanner sc=new Scanner(System.in);

        System.out.println("Insert drone ID and port");
        Drone d1 = new Drone(sc.nextInt(), "localhost", sc.nextInt());
        d1.run();

    }
}
