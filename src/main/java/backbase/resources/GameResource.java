/*
 * Copyright 2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package backbase.resources;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import backbase.api.Game;

/**
 * A resource for creating and managing Kalah games.
 */
@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    @POST
    public String createGame() {
        return null;
    }

    @PUT
    @Path("{id}")
    public Game move(@PathParam("id") String id) {
        return null;
    }
}
