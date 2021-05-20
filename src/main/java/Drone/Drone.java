package Drone;

import Grpc.GetInfoClient;
import Grpc.SendInfoClient;
import Grpc.GrpcServer;
import com.drone.grpc.DroneService;

import java.util.ArrayList;
import java.util.Scanner;

public class Drone {

    // drone fields
    protected int id;
    protected String ip;
    protected int port;
    protected int[] coordinates;
    protected int battery;
    protected DronesList dronesList;
    protected RestMethods restMethods;
    protected boolean isAvailable;

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
        this.dronesList = new DronesList(this);
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
        dronesList.sendDroneInfo();

        // becomeMaster, it is a separate function
        // as one might become it later
        if (isMaster)
            becomeMaster();
    }

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

    public void stop() {
        restMethods.quit();
        if (monitorOrders != null) {
            monitorOrders.disconnect();
            System.out.println("Drone " + id + " stopped order monitor");
        }
        if (orderQueue != null) {
            orderQueue.interrupt();
            System.out.println("Drone " + id + " stopped order queue");
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

    public synchronized void setAvailable(boolean b) {
        this.isAvailable = b;
    }

    public DronesList getDronesList() {return dronesList; }

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
        return ret;
    }

    public DroneService.OrderResponse deliver(DroneService.OrderRequest request) {
        int [] newPosition = new int[]{request.getEnd().getX(), request.getEnd().getY()};
        battery -= 15;

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
                .setKm(OrderAssignment.distance(coordinates, newPosition))
                .setPollutionAverage(10)
                .setResidualBattery(battery)
                .build();

        coordinates = newPosition;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\nDelivery completed: \n\tNew position: " + newPosition[0] + " " + newPosition[1]);
        System.out.println("\tResidual battery " + battery + "\n");
        return response;
    }

    public static void main(String[] args) throws InterruptedException {

        Scanner sc=new Scanner(System.in);

        System.out.println("Insert drone ID and port");
        Drone d1 = new Drone(sc.nextInt(), "localhost", sc.nextInt());
        d1.run();
    }


}
