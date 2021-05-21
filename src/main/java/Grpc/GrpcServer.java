package Grpc;

import Drone.Drone;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer extends Thread{
    private Drone drone;

    public GrpcServer(Drone drone) {
        this.drone = drone;
    }

    public void run(){
        Server server = ServerBuilder.forPort(drone.getPort())
                .addService(new InfoGetterImpl(drone))
                .addService(new InfoSenderImpl(drone))
                .addService(new OrderAssignmentImpl(drone))
                .build();

        try {
            server.start();
            System.out.println("Drone " + drone.getId() + " started GRPC server");
        } catch (IOException e) {
            System.out.println("Drone with id " + drone.getId() + " was not able to start grpc server");
            e.printStackTrace();
        }

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            System.out.println("Drone " + drone.getId() + " stopped GRPC server");
        }
    }

}
