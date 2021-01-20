package backbase.resources;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import backbase.api.Game;
import backbase.services.GameService;

/**
 * A resource for creating and managing Kalah games.
 */
@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    private final GameService gameService = new GameService();

    @POST
    public Response create() {
        return Response.status(CREATED).entity(gameService.create()).build();
    }

    @GET
    public Response findAll() {
        return Response.status(OK).entity(gameService.read()).build();
    }

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") int id) {
        final Game game = gameService.read(id);
        return Response.status(OK).entity(game).build();
    }

    @PUT
    @Path("{id}/pits/{pitId}")
    public Response move(@PathParam("id") @NotEmpty String id, @PathParam("pitId") @NotEmpty String pitId) {
        //Invalid ID
        //Invalid PitID
        //Move will need synchronisation
        return null;
    }
}
