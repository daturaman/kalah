package backbase.resources;

import static javax.ws.rs.core.Response.Status.CREATED;

import java.util.Random;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import backbase.api.Game;

/**
 * A resource for creating and managing Kalah games.
 */
@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    private static final int MAX_ID = 9999;

    @POST
    public Response create() {
        final long id = new Random().nextInt(MAX_ID);
        final Game game = new Game(id, "http://localtest.me:8080/games/" + id, null);
        return Response.status(CREATED).entity(game).build();
    }

    @PUT
    @Path("{id}/pits/{pitId}")
    public Response move(@PathParam("id") String id, @PathParam("pitId") String pitId) {
        //Invalid ID
        //Invalid PitID
        //Move will need synchronisation
        return null;
    }
}
