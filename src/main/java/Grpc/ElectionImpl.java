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
        System.out.println("\nElection message received:");
        System.out.println("\t- id: " + request.getId());
        System.out.println("\t- battery: " + request.getBattery());
        System.out.println("\t- elected: " + request.getElected());
        boolean master = false;
        // if this drone is being elected
        if (request.getElected() && request.getId() == drone.getId()){
            System.out.println("\nELECTION FINISHED: I'm the fucking lizard king");
            master  = true;
        } else {
            // if the requesting drone has less battery and I am participant I ignore the message
            if (drone.isParticipant() && request.getBattery() < drone.getBattery()){
                System.out.println("MULTIPLE ELECTIONS: ignoring the message " +
                        "by " + request.getId());
            } else {;
                drone.enterRing();
                drone.forwardElection(buildResponse(request));
                // forward election
            }
        }

        if (master) {
            drone.becomeMaster();
        }

        //System.out.println("ELECTION: sending response");
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

        System.out.println("\nElection message to send: ");
        System.out.println("\t- id: " + chosenId);
        System.out.println("\t- battery: " + battery);
        System.out.println("\t- elected: " + elected);


        return ElectionRequest.newBuilder()
                .setId(chosenId)
                .setBattery(battery)
                .setElected(elected)
                .build();
    }
}
