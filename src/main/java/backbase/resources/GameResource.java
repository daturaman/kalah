package backbase.resources;

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

    @POST
    public Response createGame() {
        //Create game, add it to json array file, retrun it
        final long id = 1234L;
        final Game game = new Game(id, "http://localtest.me:8080/games/" + id, null);
        return Response.status(Response.Status.OK).entity(game).build();
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
