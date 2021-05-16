package Drone;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RestMethods {
    Drone drone;

    public RestMethods(Drone drone) {
        this.drone = drone;
    }

    public boolean initialize() {
        System.out.println("Initial information request");
        try {
            Client client = Client.create();
            WebResource webResource = client
                    .resource(Drone.restBaseAddress + "add");

            String payload = this.getPostPayload();

            ClientResponse response = webResource.type("application/json")
                    .post(ClientResponse.class, payload);

            // if the id is not present in the system
            int status = response.getStatus();

            if (status == 200) {
                // no conflict, unpack the response and go on
                unpackResponse(response.getEntity(String.class));
                System.out.println("ADD: Drone " + drone.id + " initialization completed");
                return true;
            } else if (status == 409) {
                // if rest api gives a conflict response
                System.out.println("ADD: The given ID " + drone.id + " is already in the system, retry.");
            } else {
                // unhandled
                System.out.println("ADD: Unhandled case: response.getStatus() = " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getPostPayload() throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("id", drone.id);
        payload.put("ip", drone.ip);
        payload.put("port", drone.port);
        return payload.toString();
    }

    private void unpackResponse(String response) throws JSONException {

        JSONObject input = new JSONObject(response);

        // unpack coordinates
        JSONArray coordinates = input.getJSONArray("coordinates");
        for (int i = 0; i < 2; i++)
            drone.coordinates[i] = coordinates.getInt(i);

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
                String ip = current.getString("ip");
                int port = current.getInt("port");
                if (id != drone.id)
                    drone.dronesList.add(new Drone(id, ip, port));
            }
            // TODO remove
            //drone.isMaster = false;
        } catch (JSONException e) {
            // TODO remove
            //drone.isMaster = true;
        }
    }

    public void quit() {
        System.out.println("Quitting drone " + drone.id);
        try {
            Client client = Client.create();
            // calling a DELETE host/remove/id removes the drone with the given id
            WebResource webResource = client
                    .resource(Drone.restBaseAddress + "remove/" + drone.id);

            ClientResponse response = webResource.type("application/json")
                    .delete(ClientResponse.class);

            // if the id is not present in the system
            int status = response.getStatus();

            if (status == 200) {
                // id found
                System.out.println("REMOVE: Drone " + drone.id + " removed from REST api");
            } else if (status == 404) {
                // if rest api gives a conflict response
                System.out.println("REMOVE: Drone " + drone.id + " was not found on rest api");
            } else {
                // unhandled
                System.out.println("REMOVE: Unhandled case: response.getStatus() = " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // stop threads and quit
    }
}
