package Grpc;

import Drone.Drone;
import com.drone.grpc.DroneService.SenderInfoRequest;
import com.drone.grpc.DroneService.Coordinates;
import com.drone.grpc.DroneService.SenderInfoResponse;

import com.drone.grpc.InfoSenderGrpc;
import com.drone.grpc.InfoSenderGrpc.InfoSenderStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class SendInfoClient extends Thread {
    private final Drone senderDrone;
    private final Drone receiverDrone;

    public SendInfoClient(Drone senderDrone, Drone receiverDrone) {
        this.senderDrone = senderDrone;
        this.receiverDrone = receiverDrone;
    }

    public void start(){
        // build channel pointing receiver drone
        //System.out.println("Creating stub " + receiverDrone.getIp() + ":" + receiverDrone.getPort());
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(receiverDrone.getIp() + ":" + receiverDrone.getPort())
                        .usePlaintext().build();

        // create a non blocking stub
        InfoSenderStub stub = InfoSenderGrpc.newStub(channel);

        SenderInfoRequest req = SenderInfoRequest.newBuilder()
                .setId(senderDrone.getId())
                .setIp(senderDrone.getIp())
                .setPort(senderDrone.getPort())
                .setResidualBattery(senderDrone.getBattery())
                .setIsMaster(senderDrone.isMaster())
                .setPosition(
                        Coordinates.newBuilder()
                            .setX(senderDrone.getX())
                            .setY(senderDrone.getY())
                            .build()
                )
                .setAvailable(senderDrone.isAvailable())
                .build();

        stub.sendInfo(req, new StreamObserver<SenderInfoResponse>() {
            @Override
            public void onNext(SenderInfoResponse value) {
                /*

                System.out.println("DRONE RESPONSE");
                System.out.println(value.getId());
                System.out.println(value.getPosition().getX() + ", " + value.getPosition().getY());
                System.out.println(value.getResidualBattery());
                System.out.println(value.getIsMaster());
                 */
                senderDrone.updateMasterDrone(value);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("SEND INFO RESPONSE ERROR");
            }

            @Override
            public void onCompleted() {
                System.out.println("SEND INFO COMPLETED");
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
