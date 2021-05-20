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

    static double distance(int[]v1, int[]v2){
        return Math.sqrt(
                Math.pow(v2[0] - v1[0], 2) +
                        Math.pow(v2[1] - v1[1], 2)
        );
    }

    private Drone findClosest() {

        Drone closest = null;
        double dist = Double.MAX_VALUE;


        for ( Drone d : drone.dronesList.getDronesList() ) {
            double currentDistance = distance(order.startCoordinates, d.coordinates);
            System.out.println(d.getId() + " " + d.isAvailable());
            if (d.isAvailable() && (closest == null || currentDistance < dist)) {
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
                System.out.println("Order assignment response by drone " + receiver.id);
                queue.addStatistic(value);
                /*
                System.out.println(value.getKm());
                System.out.println(value.getResidualBattery());
                System.out.println(value.getNewPosition());
                System.out.println(value.getPollutionAverage());
                System.out.println(value.getTimestamp());
                */
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Order assignment response error");
            }

            @Override
            public void onCompleted() {
                System.out.println("Order assignment completed by drone " + receiver.id);
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

    public void run() {
        System.out.println("Order assignment");
        drone.getDronesList().lockDronesList();
        Drone closest = findClosest();
        if (closest == null) {
            System.out.println("No drones available");
            queue.retryOrder(order);
        }else{
            System.out.println("Closest drone: " + closest.id);
            closest.setAvailable(false);
            sendOrder(closest);
        }
        drone.getDronesList().unlockDronesList();
        queue.removeThread(this);
    }
}
