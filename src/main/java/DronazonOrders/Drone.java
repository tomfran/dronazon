package DronazonOrders;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Drone {

    // drone fields
    private int id;
    private String ip;
    private int port;
    private int[] coordinates;
    private int battery;
    private ArrayList<Drone> dronesList;

    // rest api base
    public static String restBaseAddress = "http://localhost:1337/drones/";

    //master drone fields
    private boolean isMaster;

    public Drone(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.battery = 100;
        this.dronesList = new ArrayList<>();
        this.coordinates = new int[2];
    }

    public void run(){
        request();
        System.out.println(this);
    }

    public boolean request() {
        System.out.println("Initial information request");
        try {
            Client client = Client.create();
            WebResource webResource = client
                    .resource(restBaseAddress + "add");

            String payload = this.getPostPayload();

            ClientResponse response = webResource.type("application/json")
                    .post(ClientResponse.class, payload);

            // if the id is not present in the system
            int status = response.getStatus();

            if (status == 200) {
                // no conflict, unpack the response and go on
                unpackResponse(response.getEntity(String.class));
                System.out.println("Drone (" + this.id + ") initialization completed");
                return true;
            } else if (status == 409) {
                // if rest api gives a conflict response
                System.out.println("The given ID (" + this.id + ") is already in the system, retry.");
            } else {
                // unhandled
                System.out.println("Unhandled case: response.getStatus() = " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getPostPayload() throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("id", this.id);
        payload.put("ip", this.ip);
        payload.put("port", this.port);
        return payload.toString();
    }

    private void unpackResponse(String response) throws JSONException {

        JSONObject input = new JSONObject(response);

        // unpack coordinates
        JSONArray coordinates = input.getJSONArray("coordinates");
        for (int i = 0; i < 2; i++)
            this.coordinates[i] = coordinates.getInt(i);

        /*
        unpack drone list
        first request gives out a json object and not a json array,
        as only one drone is in the system, i.e. the drone becomes the master
         */
        try {
            JSONArray list = input.getJSONArray("dronesList");
            for (int i = 0; i < list.length(); i++) {
                JSONObject current = list.getJSONObject(i);
                int id = current.getInt("id");
                String ip = current.getString("id");
                int port = current.getInt("port");
                if (id != this.id)
                    this.dronesList.add(new Drone(id, ip, port));
            }
        } catch (JSONException e) {
            isMaster = true;
        }
    }

    public String getInfo(){
        return (isMaster? "MASTER" : "SIMPLE") + " DRONE" + " Id: " + id +
                "\nIp and port: " + this.ip + ":" + port;
    }

    public String toString(){
        String ret = getInfo() + "\nDrones list: [\n";

        for (Drone d : dronesList)
            ret += "\n" + d.getInfo() + ", \n\n";

        ret += "]";
        return ret;
    }

    public static void main(String[] args) {
        Drone d1 = new Drone(3, "localhost", 5000);
        Drone d2 = new Drone(4, "localhost", 6000);

        d1.run();
        d2.run();
    }

}
