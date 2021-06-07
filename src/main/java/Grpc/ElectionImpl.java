package Grpc;

import Drone.Drone;
import com.drone.grpc.DroneService.ElectionRequest;
import com.drone.grpc.DroneService.ElectionResponse;
import com.drone.grpc.ElectionGrpc.ElectionImplBase;
import io.grpc.stub.StreamObserver;

public class ElectionImpl extends ElectionImplBase {
    private final Drone drone;

    public ElectionImpl(Drone drone) {
        this.drone = drone;
    }

    @Override
    public void elect(ElectionRequest request, StreamObserver<ElectionResponse> responseObserver) {
        boolean master = false;
        // if this drone is being elected
        if (request.getElected() && request.getId() == drone.getId()){
            master  = true;
        } else {
            if (drone.isParticipant() && request.getId() < drone.getId()){
                System.out.println("Election already in process, ignoring the message " +
                        "by " + request.getId());
            } else {
                drone.enterRing();
                drone.forwardElection(buildResponse(request));
                // forward election
            }
        }

        if (master) {
            drone.becomeMaster();
        }

        System.out.println("Responding to previous drone");
        responseObserver.onNext(ElectionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    private ElectionRequest buildResponse(ElectionRequest request) {
        // if I receive the elected message simply forward it
        if (request.getElected()) {
            drone.getDronesList().setNewMaster(request.getId());
            drone.setParticipant(false);
            return request;
        }

        int chosenId, battery;
        boolean elected = false;

        if( request.getId() == drone.getId() || drone.isMaster()){
            chosenId = drone.getId();
            battery = drone.getBattery();
            elected = true;
        } else {
            // if the requester has higher battery or equal battery and higher id,
            // he wins against this drone
            if ((request.getBattery() > drone.getBattery()) ||
                    (request.getBattery() == drone.getBattery()
                            && request.getId() > drone.getId())){
                chosenId = request.getId();
                battery = request.getBattery();
            } else {
                chosenId = drone.getId();
                battery = drone.getBattery();
            }
            drone.setParticipant(true);
        }

        System.out.println("Election message to send: ");
        System.out.println("\tid: " + chosenId);
        System.out.println("\tbattery: " + battery);
        System.out.println("\telected: " + elected);


        return ElectionRequest.newBuilder()
                .setId(chosenId)
                .setBattery(battery)
                .setElected(elected)
                .build();
    }
}
