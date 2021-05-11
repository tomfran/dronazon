package RestServer.beans;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class CoordDroneList {
    private ArrayList<Drone> dronesList;
    private int[] coordinates;

    public CoordDroneList() {
    }

    public CoordDroneList(ArrayList<Drone> dronesList, int[] coordinates) {
        this.dronesList = dronesList;
        this.coordinates = coordinates;
    }

    public ArrayList<Drone> getDronesList() {
        return dronesList;
    }

    public void setDronesList(ArrayList<Drone> dronesList) {
        this.dronesList = dronesList;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }
}
