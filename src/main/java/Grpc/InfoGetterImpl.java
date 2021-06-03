package Grpc;

import Drone.Drone;
import com.drone.grpc.InfoGetterGrpc.InfoGetterImplBase;
import com.drone.grpc.DroneService.InfoRequest;
import com.drone.grpc.DroneService.InfoResponse;
import com.drone.grpc.DroneService.Coordinates;
import io.grpc.stub.StreamObserver;

public class InfoGetterImpl extends InfoGetterImplBase {
    private final Drone drone;

    public InfoGetterImpl(Drone drone) {
        this.drone = drone;
    }

    @Override
    public void getInfo(InfoRequest request, StreamObserver<InfoResponse> responseObserver) {
        //System.out.println("GRPC received at drone " + drone.getId());

        Coordinates cord = Coordinates.newBuilder()
                .setX(drone.getX())
                .setY(drone.getY())
                .build();

        InfoResponse response = InfoResponse.newBuilder()
                .setId(drone.getId())
                .setResidualBattery(drone.getBattery())
                .setPosition(cord)
                .setIsMaster(drone.isMaster())
                .setAvailable(drone.isAvailable())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
