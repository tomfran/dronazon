package RestServer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.Scanner;

public class AdminClient {
    public static String restBaseAddress = "http://localhost:1337/drones/";

    static String commandList = "Available methods:\n\n" +
            "\t(1) Get drones in the smart-city\n" +
            "\t(2) Get last N stats from the smart-city\n" +
            "\t(3) Get the average number of deliveries between two timestamps\n" +
            "\t(4) Get the average km between two timestamps\n" +
            "\t(5) Quit\n\n" +
            "Insert a command between 1 and 5: ";

    public static void main(String[] args){
        System.out.println("Smart-city ADMIN client\n");
        int command = 0;
        Scanner sc=new Scanner(System.in);
        while (command != 5) {
            System.out.print(commandList);
            command = sc.nextInt();
            System.out.println("");
        }


        System.out.println("Initial information request");
        try {
            Client client = Client.create();
            WebResource webResource = client
                    .resource(restBaseAddress + "get");

            ClientResponse response = webResource.type("application/json")
                    .get(ClientResponse.class);

            // if the id is not present in the system
            int status = response.getStatus();

            if (status == 200) {
                System.out.println(response);
            } else {
                // unhandled
                System.out.println("ERROR");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
