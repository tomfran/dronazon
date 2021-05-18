package Grpc;

import Drone.Drone;
import com.drone.grpc.DroneService;
import com.drone.grpc.DroneService.OrderRequest;
import com.drone.grpc.DroneService.OrderResponse;
import com.drone.grpc.OrderAssignmentGrpc.OrderAssignmentImplBase;
import io.grpc.stub.StreamObserver;

public class OrderAssignmentImpl extends OrderAssignmentImplBase {
    private final Drone drone;

    public OrderAssignmentImpl(Drone drone) {
        this.drone = drone;
    }

    @Override
    public void assignOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        System.out.println("GRPC Order assignment received at drone " + drone.getId());
        OrderResponse response = OrderResponse.newBuilder()
                .setKm(100)
                .setResidualBattery(10)
                .setTimestamp(36721863)
                .setNewPosition(DroneService.Coordinates.newBuilder()
                        .setX(-1)
                        .setY(-1)
                        .build()
                )
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
