package RestServer.services;

import RestServer.beans.Statistic;
import RestServer.beans.Statistics;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;

@Path("statistics")
public class StatsService {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getStatistics(){
        return Response.ok(Statistics.getInstance()).build();

    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addStatistic(Statistic u){
        Statistics.getInstance().add(u);
        return Response.ok().build();
    }

    @Path("get")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getStatsList(){
        ArrayList<Statistic> l = Statistics.getInstance().getStatsList();
        return Response.ok(l).build();
    }

    @Path("get/km/{t1}-{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAvgKm(@PathParam("t1") Timestamp t1, @PathParam("t2") Timestamp t2){
        double avg = Statistics.getInstance().avgKm(t1, t2);
        return Response.ok(avg).build();
    }

    @Path("get/delivery/{t1}-{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAvgDelivery(@PathParam("t1") Timestamp t1, @PathParam("t2") Timestamp t2){
        double avg = Statistics.getInstance().avgDelivery(t1, t2);
        return Response.ok(avg).build();
    }

}