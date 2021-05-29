package Drone;

import Dronazon.Order;
import RestServer.beans.Statistic;
import com.drone.grpc.DroneService;

import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;

public class OrderQueue extends Thread{
    private final Drone drone;
    private final LinkedList<Order> orderQueue;
    private final LinkedList<OrderAssignment> threadList;

    private boolean queueLock = false;
    private boolean threadListLock = false;


    public OrderQueue(Drone drone) {
        this.drone = drone;
        this.orderQueue = new LinkedList<>();
        this.threadList = new LinkedList<>();
    }

    public synchronized Order consume() {
        //System.out.println("entering consume");
        while( queueLock || orderQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Drone " + drone.getId() + " stopped queue consume");
                //e.printStackTrace();
            }
        }

        // critical region
        queueLock = true;
        Order o = orderQueue.getFirst();
        orderQueue.removeFirst();
        queueLock = false;
        //System.out.println("Order retrieved from the queue");
        notify();
        return o;
    }

    public synchronized void retryOrder(Order o){
        while (queueLock) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        queueLock = true;
        orderQueue.addFirst(o);
        queueLock = false;
        System.out.println("Order reinserted");
        notify();
    }

    public synchronized void produce(Order o){
        //System.out.println("entering produce");
        while (queueLock) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Producer stopped");
                e.printStackTrace();
            }
        }

        // critical region
        queueLock = true;
        orderQueue.add(o);
        queueLock = false;
        //System.out.println("Order added to the queue");
        notify();
    }

    public synchronized void removeThread(OrderAssignment t){
        while(threadListLock){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // critical region
        threadListLock = true;
        threadList.remove(t);
        t.interrupt();
        threadListLock = false;
        notify();
    }

    public synchronized void addThread(OrderAssignment t){
        while(threadListLock){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // critical region
        threadListLock = true;
        threadList.add(t);
        t.start();
        threadListLock = false;
        notify();
    }

    public synchronized void addStatistic(DroneService.OrderResponse value){
        System.out.println("!!!! To implement statistics");
    }

    public void run() {
        while (true) {
            Order next = consume();
            addThread(new OrderAssignment(drone, next, this));
        }
    }
}
