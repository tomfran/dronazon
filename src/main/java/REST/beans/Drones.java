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

    public synchronized boolean add(Drone u){
        if( !dronesDict.containsKey(u.getId()) ) {
            dronesDict.put(u.getId(), u);
            return true;
        } else {
            return false;
        }
    }

    public Drone getById(int id){
        return getDronesDict().get(id);
    }

    public Drone deleteById(int id) {
        return this.dronesDict.remove(id);
    }
}