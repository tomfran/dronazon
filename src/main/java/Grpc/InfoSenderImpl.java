package Grpc;

import Drone.Drone;
import com.drone.grpc.InfoSenderGrpc.InfoSenderImplBase;
import com.drone.grpc.DroneService.SenderInfoRequest;
import com.drone.grpc.DroneService.SenderInfoResponse;

import io.grpc.stub.StreamObserver;

public class InfoSenderImpl extends InfoSenderImplBase {
    private final Drone drone;

    public InfoSenderImpl(Drone drone) {
        this.drone = drone;
    }

    @Override
    public void sendInfo(SenderInfoRequest request, StreamObserver<SenderInfoResponse> responseObserver) {
        System.out.println("GRPC Send info received at drone " + drone.getId());
        drone.getDronesList().addNewDrone(request);
        drone.enterRing();
        SenderInfoResponse response = SenderInfoResponse.newBuilder()
                .setId(drone.getId())
                .setIsMaster(drone.isMaster())
                .build();

        System.out.println("\n New drone list: \n");
        System.out.println(drone + "\n");

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
