package DRONAZON;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Order {

    int id;
    int[] startCoordinates;
    int[] endCoordinates;

    public Order(int id, int[] startCoordinates, int[] endCoordinates) {
        this.id = id;
        this.startCoordinates = startCoordinates;
        this.endCoordinates = endCoordinates;
    }

    public String getJson() throws JSONException {
        JSONObject order = new JSONObject();
        order.put("id", this.id);

        JSONObject startCoordinates = new JSONObject();
        startCoordinates.put("x", this.startCoordinates[0]);
        startCoordinates.put("y", this.startCoordinates[1]);
        order.put("startCoordinates", startCoordinates);

        JSONObject endCoordinates = new JSONObject();
        endCoordinates.put("x", this.endCoordinates[0]);
        endCoordinates.put("y", this.endCoordinates[1]);
        order.put("endCoordinates", endCoordinates);

        return order.toString();
    }

}