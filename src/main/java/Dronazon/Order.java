package Dronazon;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Order {

    public int id;
    public int[] startCoordinates;
    public int[] endCoordinates;

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

    public static Order unpackJson(String json) {

        JSONObject order = null;
        try {
            order = new JSONObject(json);

            JSONObject startCoordinates = order.getJSONObject("startCoordinates");
            JSONObject endCoordinates = order.getJSONObject("endCoordinates");

            return new Order(
                    order.getInt("id"),
                    new int[]{
                            startCoordinates.getInt("x"),
                            startCoordinates.getInt("y")
                    },
                    new int[]{
                            endCoordinates.getInt("x"),
                            endCoordinates.getInt("y")
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Order(-1, new int[]{}, new int[]{});
    }
}