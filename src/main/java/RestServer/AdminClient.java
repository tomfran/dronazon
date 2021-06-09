package RestServer;

import RestServer.beans.Drones;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.ArrayList;
import java.util.Scanner;

public class AdminClient {
    public static String restBaseAddressDrones = "http://localhost:1337/drones/";
    public static String restBaseAddressStatistics = "http://localhost:1337/statistics/";
    public static Client client = Client.create();
    public static Scanner sc=new Scanner(System.in);

    static String commandList = "\nAvailable methods:\n\n" +
            "\t(1) Get drones in the smart-city\n" +
            "\t(2) Get last N stats from the smart-city\n" +
            "\t(3) Get the average number of deliveries between two timestamps\n" +
            "\t(4) Get the average km between two timestamps\n" +
            "\t(5) Quit\n\n" +
            "Insert a command between 1 and 5: ";

    private static void getDrones(){
        System.out.println("\nDRONES IN THE SYSTEM: \n");
        WebResource webResource = client
                .resource(restBaseAddressDrones + "get");
        ClientResponse response = webResource.type("application/json")
                .get(ClientResponse.class);
        System.out.println(response.getEntity(String.class));
    }

    private static void getNStats(){
        System.out.print("\nEnter the number of statistics you want: ");
        int n = sc.nextInt();
        WebResource webResource = client
                .resource(restBaseAddressStatistics + "get/" + n);
        ClientResponse response = webResource.type("application/json")
                .get(ClientResponse.class);
        System.out.println(response.getEntity(String.class));
    }

    private static void getAvgDeliveries(){
        System.out.print("\nEnter the start timestamp: ");
        long t1 = sc.nextLong();
        System.out.print("Enter the end timestamp: ");
        long t2 = sc.nextLong();
        WebResource webResource = client
                .resource(restBaseAddressStatistics + "get/delivery/" + t1 + "-" + t2);
        ClientResponse response = webResource.type("application/json")
                .get(ClientResponse.class);
        System.out.println(response.getEntity(String.class));
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
        System.out.println(response.getEntity(String.class));
    }


    public static void main(String[] args){
        System.out.println("==== Smart-city ADMIN CLIENT ====\n");
        int command = 0;

        while (true) {
            if(command != 0)
                sc.nextLine();
            System.out.print(commandList);
            try{
                command = sc.nextInt();
                switch (command) {
                    case 1: getDrones(); break;
                    case 2: getNStats(); break;
                    case 3: getAvgDeliveries(); break;
                    case 4: getAvgkm(); break;
                    case 5: System.exit(0);
                    default:
                        System.out.println("Please enter a valid command.");
                }
                System.out.print("\nPress ENTER to continue...");
                sc.nextLine();
            } catch (Exception e){
                System.out.println("Please enter a valid command.");
            }
        }
    }


}
