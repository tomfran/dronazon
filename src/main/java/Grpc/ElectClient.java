package Grpc;

import Drone.Drone;
import com.drone.grpc.DroneService;
import com.drone.grpc.ElectionGrpc;
import com.drone.grpc.ElectionGrpc.ElectionStub;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class ElectClient extends Thread{
    private final Drone senderDrone;
    private Drone receiverDrone;
    private final DroneService.ElectionRequest request;

    public ElectClient (Drone senderDrone, DroneService.ElectionRequest request) {
        this.senderDrone = senderDrone;
        this.request = request;
    }

    /*
    Send the election request to the next, if he
    is dead retry to forward to the next one
     */

    @Override
    public synchronized void start() {
        senderDrone.enterRing();
        receiverDrone = senderDrone.getSuccessor();

        ElectClient pointer = this;

        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(receiverDrone.getIp() + ":" + receiverDrone.getPort())
                .usePlaintext().build();

        ElectionStub stub = ElectionGrpc.newStub(channel);


        Context newContext = Context.current().fork();
        Context origContext = newContext.attach();
        try{
            stub.elect(request, new StreamObserver<DroneService.ElectionResponse>() {
                @Override
                public void onNext(DroneService.ElectionResponse value) {
                    //System.out.println("Election response by drone " + receiverDrone.getId());
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("\nELECTION ERROR: caused by " + receiverDrone.getId());
                    senderDrone.forwardElection(request);
                    channel.shutdown();
                }

                @Override
                public void onCompleted() {
                    channel.shutdown();
                }
            });

            try {
                channel.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Was not able to await termination in election");
                e.printStackTrace();
            }
        } finally {
            newContext.detach(origContext);
        }

    }
}
