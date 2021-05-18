package Drone;

import Dronazon.Order;
import com.drone.grpc.DroneService;
import com.drone.grpc.OrderAssignmentGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.sql.SQLOutput;
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

    private double distance(int[]v1, int[]v2){
        return Math.sqrt(
                Math.pow(v2[0] - v1[0], 2) +
                        Math.pow(v2[1] - v1[1], 2)
        );
    }

    private Drone findClosest() {
        Drone closest = null;
        double dist = Double.MAX_VALUE;


        for ( Drone d : drone.dronesList ) {
            double currentDistance = distance(order.startCoordinates, d.coordinates);
            if (d.isAvailable && (closest == null || currentDistance < dist)) {
                dist = currentDistance;
                closest = d;
            }
        }
        return closest;
    }

    public void sendOrder(Drone receiver){
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
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Order assignment response by drone " + receiver.id);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Order assignment response error");
            }

            @Override
            public void onCompleted() {
                System.out.println("Order assignment completed by drone " + receiver.id);
                receiver.isAvailable = true;
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

    public void run() {
        drone.lockDronesList();
        Drone closest = findClosest();
        if (closest == null) {
            System.out.println("No drones available");
            //queue.retryOrder(order);
        }else{
            closest.isAvailable = false;
            sendOrder(closest);
        }
        drone.unlockDronesList();
        queue.removeThread(this);
    }
}
