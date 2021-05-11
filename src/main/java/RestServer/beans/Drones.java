package RestServer.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Drones {

    @XmlElement(name="drones")
    private ArrayList<Drone> dronesList;

    private static Drones instance;

    private Drones() {
        dronesList = new ArrayList<Drone>() ;
    }

    //singleton
    public synchronized static Drones getInstance(){
        if(instance==null)
            instance = new Drones();
        return instance;
    }

    public synchronized ArrayList<Drone> getDronesList() {
        return new ArrayList<Drone>(this.dronesList);
    }

    private int[] randomCoordinates() {
        Random rd = new Random();
        int x = abs(rd.nextInt()%10);
        int y = abs(rd.nextInt()%10);
        return new int[]{x, y};
    }

    public synchronized CoordDroneList add(Drone u){

        for ( Drone d : this.getDronesList() ) {
            if (u.getId() == d.getId())
                return null;
        }
        this.dronesList.add(u);
        return new CoordDroneList(getDronesList(), randomCoordinates());
    }

    public synchronized Drone getById(int id){
        for ( Drone d : this.getDronesList() ) {
            if ( id == d.getId())
                return d;
        }
        return null;
    }

    public synchronized Drone deleteById(int id) {
        for ( Drone d : this.dronesList ) {
            if (d.getId() == id) {
                this.dronesList.remove(d);
                return d;
            }
        }
        return null;
    }
}