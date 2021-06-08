package Drone;

import Grpc.ElectClient;
import Grpc.GrpcServer;
import Simulators.Measurement;
import Simulators.PollutionSensor;
import com.drone.grpc.DroneService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Drone implements Comparable<Drone>{

    /*
    Drone fields and required locks
     */
    protected int id;
    protected String ip;
    protected int port;
    protected DronesList dronesList;
    protected RestMethods restMethods;

    protected int[] coordinates;
    private final Object coordinatesLock;

    protected int battery;
    private final Object batteryLock;

    protected boolean isAvailable;
    private final Object isAvailableLock;

    protected boolean isParticipant;
    private final Object participantLock;

    protected Drone successor;

    protected double totKm;
    private final Object totKmLock;
    protected int totDeliveries;
    private final Object totDeliveriesLock;

    /*
    Master fields, and required locks
     */
    protected boolean isMaster;
    private final Object masterLock;

    private MonitorOrders monitorOrders;
    protected OrderQueue orderQueue;
    protected StatisticsMonitor statisticsMonitor;

    /*
    GRPC server, ping, quit, print and pollution threads.
     */
    private GrpcServer grpcServer;
    private PingService pingService;
    private QuitDrone quitDrone;
    private PrintDroneInfo printDroneInfo;
    private PollutionSensor pollutionSensor;

    public Drone(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        battery = 100;
        dronesList = new DronesList(this);
        coordinates = new int[2];
        restMethods = new RestMethods(this);
        isAvailable = true;
        successor = null;
        totKm = 0;
        totDeliveries = 0;
        batteryLock = new Object();
        coordinatesLock = new Object();
        isAvailableLock = new Object();
        participantLock = new Object();
        masterLock = new Object();
        totDeliveriesLock = new Object();
        totKmLock = new Object();
        isParticipant = false;
    }

    public Drone(int id, String ip, int port, int[] coordinates, int battery, boolean isMaster, boolean isAvailable) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.coordinates = coordinates;
        this.battery = battery;
        this.isMaster = isMaster;
        this.isAvailable = isAvailable;
        batteryLock = new Object();
        coordinatesLock = new Object();
        isAvailableLock = new Object();
        participantLock = new Object();
        masterLock = new Object();
        totDeliveriesLock = new Object();
        totKmLock = new Object();
    }

    /*
    Start function, the drone initialize and send others
    it's info, if required it becomes master
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

        pollutionSensor = new PollutionSensor(this);
        pollutionSensor.start();
    }

    /*
    Calling this function a Drone becomes master, so it
    starts to monitor orders and manage the queue
     */
    public synchronized void becomeMaster(){
        setParticipant(false);
        setMaster(true);
        System.out.println("\nBECOMING THE NEW MASTER:");
        // request drones infos
        dronesList.requestDronesInfo();
        System.out.println("\t- Other drones info requested");
        // start the order queue
        orderQueue = new OrderQueue(this);
        monitorOrders = new MonitorOrders(this, orderQueue);
        orderQueue.start();
        System.out.println("\t- Order queue started");
        // start the order monitor mqtt client
        monitorOrders.start();
        System.out.println("\t- MQTT client started\n\n");
        statisticsMonitor = new StatisticsMonitor(this);
        statisticsMonitor.start();
    }

    /*
    Called after a quit command, ti stops everything making sure
    an election nor a delivery is in progress, when a drone is
    master it also empty the order queue and send the stats to the REST API.
     */
    public void stop() {
        System.out.println("\n\nQUIT RECEIVED:");

        /*
        Disconnect mqtt client to not receive new orders
         */
        if (isMaster())
            monitorOrders.disconnect();

        /*
        Wait if there is an election in progress
         */
        while(isParticipant()){
            System.out.println("\t- Election in progress, can't quit now...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
        A delivery is in progress, need to wait
         */
        while(!isAvailable()){
            System.out.println("\t- Delivery in progress, can't quit now...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (isMaster()){
            // this make sure to run orderqueue until it's empty
            orderQueue.setExit(true);
            /*
            if orders are still in the queue, notifyAll, as
            there might be a produce that's stuck.
            Then wait on the queue, there will be a notify when all the
            current deliveries are finished
             */
            if(!orderQueue.isEmpty()) {
                System.out.println(orderQueue);
                try {
                    synchronized (orderQueue.queueLock) {
                        orderQueue.queueLock.notifyAll();
                        orderQueue.queueLock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("\t- All orders have been assigned\n" +
                    "\t- Sending statistics to the REST API...");

            synchronized (statisticsMonitor.statisticLock) {
                try {
                    statisticsMonitor.statisticLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("\t- STATS SENT");
        }

        grpcServer.interrupt();
        System.out.println("\t- GRPC server interrupted");
        restMethods.quit();
        System.out.println("\t- REST API delete sent");
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

        successor = (i == list.size()-1)? list.get(0) : list.get(i+1);
    }

    public synchronized void forwardElection(DroneService.ElectionRequest electionRequest){
        if (!isMaster()) {
            //System.out.println("Forwarding election");
            ElectClient c = new ElectClient(this, electionRequest);
            c.start();
        }
        //else {
            //System.out.println("Already master");
        //}
    }

    /*
    Start the election in case of a missed ping response
    by the master
     */
    public synchronized void startElection(){
        if (!isParticipant()) {
            setParticipant(true);
            forwardElection(DroneService.ElectionRequest.newBuilder()
                    .setId(getId())
                    .setBattery(getBattery())
                    .setElected(false)
                    .build());
        }
    }

    /*
    Delivery simulation, the Drone sleeps for 5 seconds,
    then it sends the delivery response,
     */
    public DroneService.OrderResponse deliver(DroneService.OrderRequest request) {
        setAvailable(false);
        int[] orderStartPosition = new int[]{request.getEnd().getX(), request.getEnd().getY()};
        int[] orderEndPosition = new int[]{request.getEnd().getX(), request.getEnd().getY()};
        decreaseBattery();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double deliveryKm = DronesList.distance(getCoordinates(), orderStartPosition) +
                DronesList.distance(orderStartPosition, orderEndPosition);

        DroneService.OrderResponse.Builder response = DroneService.OrderResponse.newBuilder()
                .setId(getId())
                .setTimestamp(
                        new Timestamp(System.currentTimeMillis()).getTime()
                )
                .setNewPosition(
                        DroneService.Coordinates.newBuilder()
                                .setX(orderEndPosition[0])
                                .setY(orderEndPosition[1])
                                .build()
                )
                .setKm(deliveryKm)
                .setResidualBattery(getBattery());

        for (Measurement m : pollutionSensor.getDeliveryPollution()) {
            response.addMeasurements(DroneService.Measurement.newBuilder()
                    .setAvg(m.getValue()).build());
        }

        setCoordinates(orderEndPosition);
        incrementTotKm(deliveryKm);
        incrementTotDeliveries();

        System.out.println("\nDELIVERY COMPLETED: \n\t- New position: [" + orderEndPosition[0] + ", " + orderEndPosition[1] + "]");
        System.out.println("\t- Residual battery: " + getBattery() + "%\n");
        setAvailable(true);

        return response.build();
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

    /*
    Synchronized methods as in delivery search
    the master access the fields concurrently
     */
    public int getBattery() {
        int ret;
        synchronized (batteryLock){
            ret = battery;
        }
        return ret;
    }

    public void decreaseBattery() {
        synchronized (batteryLock){
            battery -= 15;
        }
    }

    public int[] getCoordinates(){
        int[] ret;
        synchronized (coordinatesLock) {
            ret = coordinates;
        }
        return ret;
    }

    public void setCoordinates(int[] cord){
        synchronized (coordinatesLock) {
            coordinates = cord;
        }
    }

    public int getX() {
        int ret;
        synchronized (coordinatesLock) {
            ret = coordinates[0];
        }
        return ret;
    }

    public int getY() {
        int ret;
        synchronized (coordinatesLock) {
            ret = coordinates[1];
        }
        return ret;
    }

    public boolean isAvailable() {
        boolean ret;
        synchronized (isAvailableLock){
            ret = (boolean) isAvailable;
        }
        return ret;
    }

    public void setAvailable(boolean b) {
        synchronized (isAvailableLock){
            isAvailable = b;
        }
    }

    public void setParticipant(boolean b) {
        synchronized (participantLock) {
            isParticipant = b;
        }
    }

    public boolean isParticipant() {
        boolean ret;
        synchronized (participantLock) {
            ret = isParticipant;
        }
        return ret;
    }

    public boolean isMaster() {
        boolean ret;
        synchronized (masterLock) {
            ret = isMaster;
        }
        return ret;
    }

    public void setMaster(boolean b) {
        synchronized (masterLock) {
            isMaster = b;
        }
    }

    public DronesList getDronesList() { return dronesList; }

    public int getPort() {
        return port;
    }

    public void incrementTotKm(double d) {
        synchronized (totKmLock) {
            totKm += d;
        }
    }

    public double getTotKm() {
        double ret;
        synchronized (totKmLock) {
            ret = totKm;
        }
        return ret;
    }

    public void incrementTotDeliveries(){
        synchronized (totDeliveriesLock) {
            totDeliveries += 1;
        }
    }

    public int getTotDeliveries(){
        int ret;
        synchronized (totDeliveriesLock) {
            ret = totDeliveries;
        }
        return ret;
    }


    public String getInfo(){
        return (isMaster()? "MASTER" : "WORKER") + "\n\t- Id: " + getId() +
                "\n\t- Address: " + getIp() + ":" + getPort();
    }

    public String toString(){
        String ret =  "\n======== DRONE INFO ========\n\n" + getInfo();
        ret += "\n\t- Battery level: " + getBattery() + "%";
        ret += "\n\t- Total km: " + getTotKm();
        ret += "\n\t- Total deliveries: " + getTotDeliveries();

        /*
        ret += "\n\nOther known drones: [\n";

        for (Drone d : getDronesList().getDronesList())
            ret += "\n- " + d.getInfo() + ", \n";

        ret += "\n]";
        */
        return ret + "\n============================\n";
    }

    public Drone getSuccessor(){
        return successor;
    }

    public static void main(String[] args) {

        /*
        Scanner sc=new Scanner(System.in);

        System.out.println("Insert drone ID and port");
        Drone d = new Drone(sc.nextInt(), "localhost", sc.nextInt());
        */

        Random rd = new Random();
        Drone d = new Drone(rd.nextInt(1000), "localhost", 10000 + rd.nextInt(30000));
        d.run();
    }
}
