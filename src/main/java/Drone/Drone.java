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
    protected int[] coordinates;
    protected int battery;
    protected DronesList dronesList;
    protected RestMethods restMethods;
    protected boolean isAvailable;
    protected Drone successor;
    protected Drone predecessor;

    // master drone fields and orders thread
    protected boolean isMaster;
    private MonitorOrders monitorOrders;
    protected OrderQueue orderQueue;

    // threads for quitting and grpc server
    private GrpcServer grpcServer;
    private QuitDrone quitDrone;

    public Drone(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.battery = 100;
        this.dronesList = new DronesList(this);
        this.coordinates = new int[2];
        this.restMethods = new RestMethods(this);
        this.isAvailable = true;
        this.successor = null;
        this.predecessor = null;
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

        // send everyone my informations
        dronesList.sendDroneInfo();

        // send successor and precedessor

        // becomeMaster, it is a separate function
        // as one might become it later
        if (isMaster)
            becomeMaster();

    }

    /*
    Calling this function a Drone becomes master, so it
    starts to monitor orders and manage the queue
     */
    public void becomeMaster(){
        // request drones infos
        dronesList.requestDronesInfo();
        System.out.println("Info requested");
        // start the order queue
        orderQueue = new OrderQueue(this);
        monitorOrders = new MonitorOrders(this, orderQueue);

        orderQueue.start();
        System.out.println("Queue started");
        // start the order monitor mqtt client
        monitorOrders.start();
    }

    /*
    Called after a quit command,
    it basically stops everything
     */
    public void stop() {
        restMethods.quit();
        if (orderQueue != null) {
            orderQueue.interrupt();
            System.out.println("Drone " + id + " stopped order queue");
        }

        if (monitorOrders != null) {
            monitorOrders.interrupt();
            System.out.println("Drone " + id + " stopped order monitor");
        }


        quitDrone.interrupt();
        System.out.println("Drone " + id + " stopped quit monitor");
        grpcServer.interrupt();
        System.out.println("Drone " + id + " stopped grpc monitor");
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
        int [] newPosition = new int[]{request.getEnd().getX(), request.getEnd().getY()};
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

    public int getX() {
        return coordinates[0];
    }

    public int getY() {
        return coordinates[1];
    }

    /*
    Synchronized methods as in delivery search
    the master access the fileds concurrently
     */
    public synchronized int getBattery() {
        return battery;
    }

    public synchronized void decreaseBattery() {
        battery -= 15;
    }

    public synchronized int[] getCoordinates(){
        return coordinates;
    }

    public synchronized void setCoordinates(int[] cord){
        coordinates = cord;
    }

    public synchronized boolean isAvailable() {
        return isAvailable;
    }

    public synchronized void setAvailable(boolean b) {
        this.isAvailable = b;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public DronesList getDronesList() { return dronesList; }

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

        for (Drone d : dronesList.getDronesList())
            ret += "\n" + d.getInfo() + ", \n\n";

        ret += "]";
        if(predecessor != null && successor != null) {
            ret += "\n Pred: " + predecessor.getInfo();
            ret += "\n Succ: " + successor.getInfo();
        }
        return ret;
    }

    public static void main(String[] args) {

        Scanner sc=new Scanner(System.in);

        System.out.println("Insert drone ID and port");
        Drone d1 = new Drone(sc.nextInt(), "localhost", sc.nextInt());
        d1.run();

    }
}
