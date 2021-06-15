package RestServer;

import RestServer.beans.Drone;
import RestServer.beans.Drones;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class AdminClient {
    public static String restBaseAddressDrones = "http://localhost:1337/drones/";
    public static String restBaseAddressStatistics = "http://localhost:1337/statistics/";
    public static Client client = Client.create();
    public static Scanner sc=new Scanner(System.in);

    static String commandList = "Available methods:\n\n" +
            "\t1) GET DRONES:\n" +
            "\t\t\tno parameters needed\n" +
            "\t2) GET LAST N STATISTICS:\n" +
            "\t\t\tneed to specify an integer after choosing the command\n" +
            "\t3) GET AVERAGE DELIVERIES\n" +
            "\t\t\tneed to enter start and end timestamp in long format\n" +
            "\t4) GET AVERAGE KILOMETERS\n" +
            "\t\t\tneed to enter start and end timestamp in long format\n" +
            "\t5) QUIT \n\n" +
            "Insert a command between 1 and 5: ";

    private static void getDrones(){
        WebResource webResource = client
                .resource(restBaseAddressDrones + "get");
        ClientResponse response = webResource.type("application/json")
                .get(ClientResponse.class);
        try {
            JSONArray r = new JSONArray(response.getEntity(String.class));
            System.out.println( (r.length() > 0)? "\nDrones in the smart city: \n" : "\nNo drones found\n");
            for (int i = 0; i < r.length(); i++) {
                JSONObject d = r.getJSONObject(i);
                System.out.println((i+1)+". drone: " + "\n\t- id: "+ d.getInt("id")
                + "\n\t- ip: " + d.getString("ip") + "\n\t- port: " + d.getInt("port") + '\n');
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getNStats(){
        System.out.print("Enter the number of statistics you want: ");
        String n = sc.nextLine();
        WebResource webResource = client
                .resource(restBaseAddressStatistics + "get/" + n);
        ClientResponse response = webResource.type("application/json")
                .get(ClientResponse.class);
        try {
            JSONArray r = new JSONArray(response.getEntity(String.class));
            System.out.println( (r.length() > 0)? "\nLAST STATISTICS: \n" : "\nNo statistics found\n");
            for (int i = 0; i < r.length(); i++) {
                JSONObject d = r.getJSONObject(i);
                System.out.println((i+1)+". statistic: "
                        + "\n\t- avgDelivery: "+ d.getDouble("avgDelivery")
                        + "\n\t- avgKm: " + d.getDouble("avgKm")
                        + "\n\t- avgPollution: " + d.getDouble("avgPollution")
                        + "\n\t- avgBattery: " + d.getDouble("avgBattery")
                        + "\n\t- timestamp: " + d.getLong("timestamp")
                        + '\n');
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getAvgDeliveries(){
        System.out.print("Enter the start timestamp: ");
        long t1 = sc.nextLong();
        System.out.print("Enter the end timestamp: ");
        long t2 = sc.nextLong();
        WebResource webResource = client
                .resource(restBaseAddressStatistics + "get/delivery/" + t1 + "-" + t2);
        ClientResponse response = webResource.type("application/json")
                .get(ClientResponse.class);

        System.out.println("\nAverage number of deliveries " +
                "between " + t1 + " and " + t2 + ": " +
                response.getEntity(String.class));
    }

    private static void getAvgkm(){
        System.out.print("\nEnter the start timestamp: ");
        long t1 = sc.nextLong();
        System.out.print("Enter the end timestamp: ");
        long t2 = sc.nextLong();
        WebResource webResource = client
                .resource(restBaseAddressStatistics + "get/km/" + t1 + "-" + t2);
        ClientResponse response = webResource.type("application/json")
                .get(ClientResponse.class);
        System.out.println("\nAverage number of km " +
                "between " + t1 + " and " + t2 + ": " +
                response.getEntity(String.class));
    }


    public static void main(String[] args){
        System.out.println("==== Smart-city ADMIN CLIENT ====\n");
        String command = "1";
        boolean exit = false;
        while (!exit) {
            System.out.print((!command.equals(""))? commandList : "");
            try{
                command = sc.nextLine();
                if (!command.equals("")) {
                    int c = Integer.parseInt(command);
                    switch (c) {
                        case 1:
                            getDrones();
                            break;
                        case 2:
                            getNStats();
                            break;
                        case 3:
                            getAvgDeliveries();
                            break;
                        case 4:
                            getAvgkm();
                            break;
                        case 5:
                            exit = true;
                            break;
                        default:
                            System.out.println("Please enter a valid command.");
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                command = "";
                System.out.println("Please enter a valid command.");
            }
        }
        sc.close();
        System.exit(0);
    }


}
