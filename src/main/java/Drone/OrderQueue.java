package Drone;

import Dronazon.Order;
import com.drone.grpc.DroneService;
import java.util.LinkedList;

public class OrderQueue extends Thread{
    private final Drone drone;
    private final LinkedList<Order> orderQueue;
    private final LinkedList<OrderAssignment> threadList;

    protected final Object queueLock;
    private final Object threadLock;

    private boolean exit = false;
    private final Object exitLock;

    public OrderQueue(Drone drone) {
        this.drone = drone;
        this.orderQueue = new LinkedList<>();
        this.threadList = new LinkedList<>();
        queueLock = new Object();
        threadLock = new Object();
        exitLock = new Object();
    }

    /*
    Queue consume
     */
    public Order consume() throws InterruptedException {
        if(!getExit()) {
            boolean empty = true;

            while (empty) {
                synchronized (queueLock) {
                    queueLock.wait();
                    empty = orderQueue.isEmpty();
                }
            }
        }
        Order o = orderQueue.getFirst();
        orderQueue.removeFirst();
        return o;
    }

    /*
    Re-add an order to the top of the queue
     */
    public synchronized void retryOrder(Order o){
        synchronized (queueLock){
            orderQueue.addFirst(o);
            queueLock.notifyAll();
        }
    }

    /*
    Add a new order to the queue, this will notify the consumer
     */
    public void produce(Order o){
        synchronized (queueLock){
            orderQueue.add(o);
            queueLock.notify();
        }
    }

    /*
    Remove delivery assignment thread from the thread list
     */
    public void removeThread(OrderAssignment t){
        synchronized (threadLock) {
            threadList.remove(t);
            t.interrupt();
            threadLock.notify();
        }
    }

    /*
    Add a thread to the thread list and start it
     */
    public void addThread(OrderAssignment t){
        synchronized (threadLock){
            threadList.add(t);
            t.start();
        }
    }

    /*
    Getter and setter to manage quit command received at master
     */
    public boolean getExit() {
        boolean ret;
        synchronized (exitLock){
            ret = exit;
        }
        return ret;
    }

    public void setExit(boolean b) {
        synchronized (exitLock){
            exit = b;
        }
    }

    public boolean isEmpty(){
        boolean ret;
        synchronized (queueLock) {
            ret = orderQueue.isEmpty();
        }
        return ret;
    }

    public void run() {
        try {
            // if I set exit, when the queue is empty I quit
            while (!getExit() || !isEmpty()) {
                Order next = consume();
                addThread(new OrderAssignment(drone, next, this));
            }
            /*
            A notify is called when a thread is removed from the list,
            if the thread list is empty I can quit
             */
            synchronized (threadLock){
                while(!threadList.isEmpty()){
                    threadLock.wait();
                }
            }
            /*
            This will wake up the master waiting in the stop method()
            He can then proceed to quit
             */
            synchronized (queueLock) {
                queueLock.notifyAll();
            }
        } catch (InterruptedException e){
            System.out.println("Interrupted received at order queue");
        }
    }

    public String toString(){
        String ret = "\nOrders left to be assigned:";
        synchronized (orderQueue) {
            for (Order o : orderQueue) {
                ret += "\n\t- " + o.id;
            }
        }
        return ret + "\n\n";
    }
}
