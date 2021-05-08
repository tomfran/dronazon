package REST.services;

import REST.beans.CoordDroneList;
import REST.beans.Drone;
import REST.beans.Drones;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("drones")
public class DronesService {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getDrones(){
        return Response.ok(Drones.getInstance()).build();

    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addDrone(Drone u){
        CoordDroneList result = Drones.getInstance().add(u);
        if(result != null){
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    @Path("get")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getDronesList(){
        ArrayList<Drone> l = Drones.getInstance().getDronesList();
        return Response.ok(l).build();
    }

    @Path("get/{id}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getById(@PathParam("id") int id){
        Drone u = Drones.getInstance().getById(id);
        if(u!=null)
            return Response.ok(u).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("remove/{id}")
    @DELETE
    @Produces({"application/json", "application/xml"})
    public Response deleteById(@PathParam("id") int id){
        Drone u = Drones.getInstance().deleteById(id);
        if(u!=null)
            return Response.ok(u).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

}