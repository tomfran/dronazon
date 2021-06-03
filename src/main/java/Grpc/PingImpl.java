package Grpc;

import Drone.Drone;
import com.drone.grpc.PingGrpc.PingImplBase;
import com.drone.grpc.DroneService.PingRequest;
import com.drone.grpc.DroneService.PingResponse;

import io.grpc.stub.StreamObserver;

public class PingImpl  extends PingImplBase{
    private final Drone drone;

    public PingImpl(Drone drone) {
        this.drone = drone;
    }

    @Override
    public void alive(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        //System.out.println("Ping received");
        PingResponse response = PingResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}