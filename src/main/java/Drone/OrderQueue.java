package Drone;

import Dronazon.Order;

import java.util.LinkedList;

public class OrderQueue extends Thread{
    private Drone drone;
    private LinkedList<Order> orderQueue;

    public OrderQueue(Drone drone) {
        this.drone = drone;
        this.orderQueue = new LinkedList<>();
    }

    public double distance(int[]v1, int[]v2){
        return Math.sqrt(
                Math.pow(v2[0] - v1[0], 2) +
                        Math.pow(v2[1] - v1[1], 2)
        );
    }

    public Drone findClosest(Order o){
        Drone closest = null;
        double dist = Double.MAX_VALUE;
        for ( Drone d : drone.dronesList ) {
            if (closest == null || distance(o.startCoordinates, d.coordinates) < dist)
                closest = d;
        }
        return closest;
    }

    public void addOrder(Order o){
        orderQueue.add(o);
        System.out.println("Order added to the queue");
    }

    public void run() {
        System.out.println("orderqueue");
    }
}
