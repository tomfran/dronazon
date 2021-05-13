package Dronazon;

import org.codehaus.jettison.json.JSONException;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Random;

import static java.lang.Math.abs;

public class Dronazon {

    static Order generateRandomOrder(Random rd, int id){
        int x1, y1, x2, y2;

        do {
            x1 = abs(rd.nextInt()%10);
            y1 = abs(rd.nextInt()%10);
            x2 = abs(rd.nextInt()%10);
            y2 = abs(rd.nextInt()%10);
        } while (x1 == x2 && y1 == y2);

        return new Order(id, new int[]{x1, y1}, new int[]{x2, y2});
    }

    public static void main(String[] args) {
        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "dronazon/smartcity/orders";
        int qos = 2;

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            //connOpts.setUserName(username); // optional
            //connOpts.setPassword(password.toCharArray()); // optional
            //connOpts.setWill("this/is/a/topic","will message".getBytes(),1,false);  // optional
            //connOpts.setKeepAliveInterval(60);  // optional

            // Connect the client
            System.out.println(clientId + " Connecting Broker " + broker);
            client.connect(connOpts);
            System.out.println(clientId + " Connected");

            Random rd = new Random();

            for (int i = 0; i < 100; i++) {
                Order o = generateRandomOrder(rd, i);
                String payload = o.getJson();
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(qos);
                System.out.println(clientId + " Publishing order: " + payload + " ...");
                client.publish(topic, message);
                System.out.println(clientId + " Order published");
                Thread.sleep(5000);
            }

            if (client.isConnected())
                client.disconnect();
            System.out.println("Publisher " + clientId + " disconnected");



        } catch (MqttException | JSONException | InterruptedException me ) {
            // System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

}
