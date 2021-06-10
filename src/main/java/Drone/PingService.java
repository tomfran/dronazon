package Drone;

import Grpc.AliveClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingService extends Thread{
    Drone drone;

    public PingService(Drone drone) {
        this.drone = drone;
    }

    public void start() {
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if(!drone.isMaster()) {
                    AliveClient t = null;
                    for (Drone d : drone.getDronesList().getDronesList()) {
                        if (d.isMaster()) {
                            t = new AliveClient(drone, d);
                            t.start();
                            break;
                        }
                    }
                    if (t != null) {
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted exception in join");
                        }
                    }
                }
            };
        };
        t.scheduleAtFixedRate(tt,new Date(),2000);
    }
}
