package Grpc;

import Drone.Drone;
import com.drone.grpc.DroneService;
import com.drone.grpc.PingGrpc;
import com.drone.grpc.PingGrpc.PingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class AliveClient extends Thread{
    private final Drone senderDrone;
    private final Drone receiverDrone;

    public AliveClient(Drone senderDrone, Drone receiverDrone) {
        this.senderDrone = senderDrone;
        this.receiverDrone = receiverDrone;
    }
    /*
        Send a ping to the receiver drone,
        if an error occurs the receiver drone
        is remove from the list, also, if he was the
        master an election is started
         */
    public void start() {
        //System.out.println("Ping service started to drone: " + receiverDrone.getId());
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(receiverDrone.getIp() + ":" + receiverDrone.getPort())
                        .usePlaintext().build();

        PingStub stub = PingGrpc.newStub(channel);

        DroneService.PingRequest req = DroneService.PingRequest.newBuilder().build();

        stub.alive(req, new StreamObserver<DroneService.PingResponse>() {
            @Override
            public void onNext(DroneService.PingResponse value) {
                //System.out.println("Ping Response received from drone " + receiverDrone.getId());
            }

            @Override
            public void onError(Throwable t) {
                channel.shutdown();
                //System.out.println("PING ERROR: drone " + receiverDrone.getId() + " is offline");
                senderDrone.getDronesList().remove(receiverDrone);

                if (receiverDrone.isMaster()) {
                    //System.out.println("MASTER DOWN: starting election");
                    senderDrone.startElection();
                }
            }

            @Override
            public void onCompleted() {
                channel.shutdown();
            }
        });

        try {
            channel.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("WAS NOT ABLE TO AWAIT TERMINATION");
            //e.printStackTrace();
        }
    }
}
