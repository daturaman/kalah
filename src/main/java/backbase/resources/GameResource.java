package backbase.resources;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

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

    /**
     * Endpoints that creates a new {@link Game} with a randomly generated ID.
     *
     * @return a {@link Response} containing the game details as a json object.
     */
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
    public Response move(@PathParam("id") int id, @PathParam("pitId") int pitId) {
        final Game game = gameService.move(id, pitId);
        return Response.status(OK).entity(game).build();
    }
}
