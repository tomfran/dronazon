package Drone;

import Grpc.AliveClient;
import RestServer.beans.Statistic;
import Simulators.Measurement;
import com.drone.grpc.DroneService;

import java.sql.Timestamp;
import java.util.*;

public class StatisticsMonitor extends Thread{

    private Drone drone;

    private HashMap<Integer, Integer> deliveries;
    private HashMap<Integer, Integer> batteries;
    private ArrayList<Double> kmList;
    private ArrayList<Measurement> pollutionList;

    protected final Object statisticLock;

    public StatisticsMonitor(Drone drone) {
        this.drone = drone;
        deliveries = new HashMap<Integer, Integer>();
        batteries = new HashMap<Integer, Integer>();
        kmList = new ArrayList<Double>();
        pollutionList = new ArrayList<Measurement>();
        statisticLock = new Object();
    }

    /*
        Add a statistic to then compute the average,
        the method is synchronized as it requires to lock everything
         */
    public synchronized void addStatistic(DroneService.OrderResponse s){
        deliveries.put(s.getId(), (deliveries.getOrDefault(s.getId(), 0)) + 1);
        batteries.put(s.getId(), s.getResidualBattery());
        kmList.add(s.getKm());
        for ( DroneService.Measurement m : s.getMeasurementsList() )
            pollutionList.add(new Measurement("", "", m.getAvg(), 0));

    }

    /*
    Get the payload for the rest api call and clean all the lists and maps.
    The method is synchronized as it requires to lock everything
     */
    public synchronized Statistic getStatisticAndClean() {
        
        int totDeliveries = 0;
        for ( Integer i : deliveries.values() )
            totDeliveries += i;

        int totBatteries = 0;
        for ( Integer i : batteries.values() )
            totBatteries += i;

        double totKm = 0;
        for ( Double i : kmList )
            totKm += i;

        double totPollution = 0;
        for ( Measurement m : pollutionList )
            totPollution += m.getValue();

        int deliveringDrones = deliveries.size();


        Statistic ret;

        if (deliveringDrones == 0) {
            ret = new Statistic(
                    0, 0, 0, 0,
                    new Timestamp(System.currentTimeMillis()).getTime()
            );
        } else {
            ret = new Statistic(
                    (double) totDeliveries / (double) deliveringDrones,
                    totKm / deliveringDrones,
                    totPollution / pollutionList.size(),
                    (double) totBatteries / (double) deliveringDrones,
                    new Timestamp(System.currentTimeMillis()).getTime()
            );
        }

        deliveries = new HashMap<Integer, Integer>();
        batteries = new HashMap<Integer, Integer>();
        kmList = new ArrayList<Double>();
        pollutionList = new ArrayList<Measurement>();

        return ret;
    }

    public void start() {
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                synchronized (statisticLock) {
                    Statistic ret = getStatisticAndClean();
                    if (ret.getAvgDelivery() > 0)
                        drone.restMethods.sendStatistic(ret);
                    statisticLock.notify();
                }
            };
        };
        t.scheduleAtFixedRate(tt,new Date(),10000);
    }
}
