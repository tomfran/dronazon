package Drone;

import Dronazon.Order;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.Scanner;

public class MonitorOrders extends Thread{
    private Drone drone;
    private MqttClient client;
    private static String broker = "tcp://localhost:1883";
    private final String clientId;
    private static String topic = "dronazon/smartcity/orders";
    private OrderQueue queue;

    public MonitorOrders(Drone drone, OrderQueue queue) {
        this.drone = drone;
        this.clientId = MqttClient.generateClientId();
        this.queue = queue;
    }

    public void run() {

        int qos = 2;

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Connect the client
            System.out.println(clientId + " Connecting Broker " + broker);
            client.connect(connOpts);
            System.out.println(clientId + " Connected - Thread PID: " + Thread.currentThread().getId());

            // Callback
            client.setCallback(new MqttCallback() {
                public void messageArrived(String topic, MqttMessage message) {
                    // Called when a message arrives from the server that matches any subscription made by the client
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());

                    Order o = Order.unpackJson(receivedMessage);
                    if (o.id == -1){
                        System.out.println("ERROR while unpacking order json");
                    } else {
                        queue.produce(o);
                    }
                }

                public void connectionLost(Throwable cause) {
                    System.out.println(clientId + " Connectionlost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used here
                }

            });
            System.out.println(clientId + " Subscribing ... - Thread PID: " + Thread.currentThread().getId());
            client.subscribe(topic,qos);
            System.out.println(clientId + " Subscribed to topics : " + topic);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException me ) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

}
