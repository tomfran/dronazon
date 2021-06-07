package Grpc;

import Drone.Drone;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer extends Thread{
    private Drone drone;
    private Server server;

    public GrpcServer(Drone drone) {
        this.drone = drone;
    }

    public void run(){
        server = ServerBuilder.forPort(drone.getPort())
                .addService(new InfoGetterImpl(drone))
                .addService(new InfoSenderImpl(drone))
                .addService(new OrderAssignmentImpl(drone))
                .addService(new PingImpl(drone))
                .addService(new ElectionImpl(drone))
                .build();

        try {
            server.start();
            System.out.println("GRPC server started");
        } catch (IOException e) {
            System.out.println("ERROR WHILE STARTING GRPC SERVER");
            e.printStackTrace();
        }

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            server.shutdown();
            System.out.println("GRPC server stopped ");
        }
    }

}
