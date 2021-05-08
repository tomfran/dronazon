package REST.beans;
/**
 * Created by civi on 26/04/16.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.abs;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Drones {

    @XmlElement(name="drones")
    private HashMap<Integer, Drone> dronesDict;

    private static Drones instance;

    private Drones() {
        dronesDict = new HashMap<Integer, Drone>();
    }

    //singleton
    public synchronized static Drones getInstance(){
        if(instance==null)
            instance = new Drones();
        return instance;
    }

    public synchronized HashMap<Integer, Drone> getDronesDict() {
        return new HashMap<Integer, Drone>(this.dronesDict);
    }

    public synchronized ArrayList<Drone> getDronesList(){
        return new ArrayList<Drone> (this.dronesDict.values());
    }

    public synchronized void setDronesDict(HashMap<Integer, Drone> dronesDict) {
        this.dronesDict = dronesDict;
    }

    private int[] randomCoordinates() {
        Random rd = new Random();
        int x = abs(rd.nextInt()%10);
        int y = abs(rd.nextInt()%10);
        return new int[]{x, y};
    }

    public synchronized CoordDroneList add(Drone u){
        if( !dronesDict.containsKey(u.getId()) ) {
            dronesDict.put(u.getId(), u);
            return new CoordDroneList(getDronesList(), randomCoordinates());
        } else {
            return null;
        }
    }

    public synchronized Drone getById(int id){
        return this.dronesDict.get(id);
    }

    public synchronized Drone deleteById(int id) {
        return this.dronesDict.remove(id);
    }
}