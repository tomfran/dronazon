package Drone;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PrintDroneInfo extends Thread{
    Drone drone;

    public PrintDroneInfo(Drone drone) {
        this.drone = drone;
    }

    public void start() {
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                System.out.println(drone);
            };
        };
        t.scheduleAtFixedRate(tt,new Date(),20000);
    }
}
