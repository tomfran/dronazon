package GrpcDrone;

import Drone.Drone;
import com.drone.grpc.InfoGetterGrpc.InfoGetterImplBase;
import com.drone.grpc.DroneService.InfoRequest;
import com.drone.grpc.DroneService.InfoResponse;
import com.drone.grpc.DroneService.Coordinates;
import io.grpc.stub.StreamObserver;

import java.sql.SQLOutput;

public class InfoGetterImpl extends InfoGetterImplBase {
    private Drone drone;

    public InfoGetterImpl(Drone drone) {
        System.out.println("InfogetterIMPL constructor " + drone.getId());
        this.drone = drone;
    }

    @Override
    public void getInfo(InfoRequest request, StreamObserver<InfoResponse> responseObserver) {
        System.out.println("GRPC received at drone " + drone.getId());

        Coordinates cord = Coordinates.newBuilder()
                .setX(drone.getX())
                .setY(drone.getY())
                .build();

        InfoResponse response = InfoResponse.newBuilder()
                .setId(drone.getId())
                .setResidualBattery(drone.getBattery())
                .setPosition(cord)
                .setIsMaster(drone.isMaster())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
