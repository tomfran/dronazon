package Drone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class QuitDrone extends Thread{
    private Drone drone;
    private BufferedReader inFromUser;


    public QuitDrone(Drone drone) {
        this.drone = drone;
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run(){
        String message = "";
        do {
            try {
                message = inFromUser.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while(!message.equals("quit"));
        drone.stop();
    }
}
