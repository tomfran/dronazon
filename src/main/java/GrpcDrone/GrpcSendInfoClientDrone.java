package GrpcDrone;

import Drone.Drone;
import com.drone.grpc.DroneService;
import com.drone.grpc.DroneService.SenderInfoRequest;
import com.drone.grpc.DroneService.Coordinates;
import com.drone.grpc.DroneService.SenderInfoResponse;

import com.drone.grpc.InfoSenderGrpc;
import com.drone.grpc.InfoSenderGrpc.InfoSenderStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcSendInfoClientDrone extends Thread {
    private final Drone senderDrone;
    private final Drone receiverDrone;

    public GrpcSendInfoClientDrone(Drone senderDrone, Drone receiverDrone) {
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

    }

}
