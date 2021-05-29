package Drone;

import Dronazon.Order;
import Grpc.GetInfoClient;
import Grpc.SendInfoClient;
import com.drone.grpc.DroneService;

import java.util.ArrayList;

public class DronesList {
    protected Drone drone;
    protected ArrayList<Drone> dronesList;
    // drones list LOCK as a lot of threads need
    // to access to it concurrently
    protected boolean listLock = false;

    public DronesList(Drone drone) {
        this.drone = drone;
        this.dronesList = new ArrayList<Drone>();
    }

    public void requestDronesInfo() {
        // list of threads to then stop them
        ArrayList<GetInfoClient> threadList = new ArrayList<>();

        // create a thread for each drone in the list
        // and start requesting infos
        // after receiving the response each thread proceeds to star the updateDrone
        // procedure, to update the drone information in the droneslist
        int i = 0;
        for ( Drone d : getDronesList() ) {
            threadList.add(
                    new GetInfoClient(drone, d, i)
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
        for ( Drone d : getDronesList() ) {
            threadList.add(
                    new SendInfoClient(drone, d)
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

        System.out.println(drone);
    }

    // add new drone to the list, called after receiving info from a new drone
    public synchronized void addNewDrone(DroneService.SenderInfoRequest value){
        // TODO if a simple drone receive this, he might now need to
        // save all this infos and stick to the simple id, port, ip constructor

        dronesList.add(new Drone(
                value.getId(),
                value.getIp(),
                value.getPort(),
                new int[]{value.getPosition().getX(), value.getPosition().getY()},
                value.getResidualBattery(),
                value.getIsMaster(),
                value.getAvailable()
        ));
    }

    // called when the get info request does not go right,
    // I should start remaking the ring
    public void invalidateDrone(int listIndex) {
        // method used to invalidate a drone entry
        Drone d = getDronesList().get(listIndex);
        d.coordinates[0] = -1;
        d.coordinates[1] = -1;
    }

    // called after a inforesponse
    public void updateDrone(DroneService.InfoResponse value, int listIndex) {
        // concurrent access to the drone list, need sync
        ArrayList<Drone> copy = getDronesList();
        Drone d = copy.get(listIndex);
        d.coordinates[0] = value.getPosition().getX();
        d.coordinates[1] = value.getPosition().getY();
        d.battery = value.getResidualBattery();
        d.isMaster = value.getIsMaster();
        d.isAvailable = value.getAvailable();
    }

    // called when receiving response after a info send
    // need this field in case the master drone fails
    public void updateMasterDrone(DroneService.SenderInfoResponse value){
        int id = value.getId();
        boolean isMaster = value.getIsMaster();
        for ( Drone d : getDronesList() ) {
            if (d.getId() == id)
                d.isMaster = isMaster;
        }
    }

    static Double distance(int[]v1, int[]v2){
        return Math.sqrt(
                Math.pow(v2[0] - v1[0], 2) +
                        Math.pow(v2[1] - v1[1], 2)
        );
    }

    public synchronized Drone findClosest(Order o) {

        Drone closest = null;
        Double dist = Double.MAX_VALUE;
        int maxBattery = 0;
        ArrayList<Drone> list = drone.dronesList.getDronesList();
        list.add(drone);
        for ( Drone d : list ) {
            Double currentDistance = distance(o.startCoordinates, d.coordinates);
            if (d.isAvailable() && (closest == null || currentDistance.compareTo(dist) < 0 ||
                    (currentDistance.compareTo(dist) == 0 && d.getBattery() > maxBattery))) {
                dist = currentDistance;
                maxBattery = d.getBattery();
                closest = d;
            }
        }
        if (closest != null)
            closest.setAvailable(false);

        return closest;
    }


    public synchronized ArrayList<Drone> getDronesList() {
        return new ArrayList<Drone>(dronesList);
    }
}
