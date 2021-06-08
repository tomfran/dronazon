package Drone;

import Dronazon.Order;
import com.drone.grpc.DroneService;
import com.drone.grpc.OrderAssignmentGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class OrderAssignment extends Thread {
    private final Drone drone;
    private final Order order;
    private final OrderQueue queue;

    public OrderAssignment(Drone drone, Order order, OrderQueue queue) {
        this.drone = drone;
        this.order = order;
        this.queue = queue;
    }

    /*
    Send order to another Drone
     */
    public void sendOrder(Drone receiver){
        System.out.println("\nSENDING ORDER:\n\t- order id: " + order.id + "\n\t- drone id: " + receiver.getId() + "\n");

        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(receiver.getIp() + ":" + receiver.getPort())
                        .usePlaintext().build();

        OrderAssignmentGrpc.OrderAssignmentStub stub = OrderAssignmentGrpc.newStub(channel);

        DroneService.OrderRequest req = DroneService.OrderRequest.newBuilder()
                .setId(order.id)
                .setStart(
                        DroneService.Coordinates.newBuilder()
                                .setX(order.endCoordinates[0])
                                .setY(order.endCoordinates[1])
                                .build()
                )
                .setEnd(
                        DroneService.Coordinates.newBuilder()
                                .setX(order.endCoordinates[0])
                                .setY(order.endCoordinates[1])
                                .build()
                )
                .build();

        stub.assignOrder(req, new StreamObserver<DroneService.OrderResponse>() {
            @Override
            public void onNext(DroneService.OrderResponse value) {
                System.out.println("\nORDER COMPLETED:\n\t- order id: " + order.id
                        + "\n\t- drone id: " + receiver.getId() + "\n");
                drone.statisticsMonitor.addStatistic(value);
            }

            @Override
            public void onError(Throwable t) {
                drone.getDronesList().remove(receiver);
                System.out.println("ORDER ASSIGNMENT ERROR, removing drone " + receiver.getId());
                queue.retryOrder(order);
                channel.shutdown();
            }

            @Override
            public void onCompleted() {
                //System.out.println("Order assignment completed by drone " + receiver.id);
                receiver.setAvailable(true);
                channel.shutdown();
            }
        });
        try {
            channel.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //System.out.println("WAS NOT ABLE TO AWAIT TERMINATION");
            e.printStackTrace();
        }

    }

    /*
    Try to find an available drone, if not available read
    the order to the queue
     */
    public void run() {
        //System.out.println("Order assignment");
        Drone closest = this.drone.getDronesList().findClosest(order);
        if (closest == null) {
            //System.out.println("No drones available");
            queue.retryOrder(order);
        }else{
            //System.out.println("Closest drone: " + closest.id);
            sendOrder(closest);
        }
        queue.removeThread(this);
    }
}
