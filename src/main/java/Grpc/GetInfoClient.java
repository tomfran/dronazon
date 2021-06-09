package Grpc;

import Drone.Drone;

import com.drone.grpc.InfoGetterGrpc;
import com.drone.grpc.InfoGetterGrpc.InfoGetterStub;
import com.drone.grpc.DroneService.InfoRequest;
import com.drone.grpc.DroneService.InfoResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class GetInfoClient extends Thread {
    private final Drone senderDrone;
    private final Drone receiverDrone;
    private final int listIndex;

    public GetInfoClient(Drone senderDrone, Drone receiverDrone, int listIndex) {
        this.senderDrone = senderDrone;
        this.receiverDrone = receiverDrone;
        this.listIndex = listIndex;
    }

    public void start() {
        // build channel pointing receiver drone
        //System.out.println("Creating stub " + receiverDrone.getIp() + ":" + receiverDrone.getPort());
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(receiverDrone.getIp() + ":" + receiverDrone.getPort())
                        .usePlaintext().build();

        // create a non blocking stub
        InfoGetterStub stub = InfoGetterGrpc.newStub(channel);

        InfoRequest req = InfoRequest.newBuilder()
                .setId(senderDrone.getId()).build();

        stub.getInfo(req, new StreamObserver<InfoResponse>() {
            @Override
            public void onNext(InfoResponse value) {
                /*

                System.out.println("DRONE RESPONSE");
                System.out.println(value.getId());
                System.out.println(value.getPosition().getX() + ", " + value.getPosition().getY());
                System.out.println(value.getResidualBattery());
                System.out.println(value.getIsMaster());
                 */
                senderDrone.getDronesList().updateDrone(value, listIndex);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("GET INFO ERROR, removing the drone");
                senderDrone.getDronesList().remove(receiverDrone);
            }

            @Override
            public void onCompleted() {
                //System.out.println("GET INFO COMPLETED");
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

}
