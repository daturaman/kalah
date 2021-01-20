package backbase.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backbase.api.Game;

class GameServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private GameService gameService;
    private Game game;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        game = gameService.create();
    }

    @Test
    void move() throws Exception {

        gameService.move(game.getId(), 3);
    }

    //PLAYER ONE when the last stone lands in the kalah then the player gets another turn
    //PLAYER TWO when the last stone lands in the kalah then the player gets another turn
    //when the stone lands in an empty player pit and the other player's pit is not empty
    //then  the player gets all stones from both pits

    //when a player runs out of stones
    //and the stones are tallied and the game is over

    //ERRORS
    //when an invalid game ID is requested
    @Test
    public void shouldThrowExceptionWhenPassedInvalidGameId() {
        final WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> gameService.move(-1, 1));
        assertEquals(exception.getMessage(), "Game ID not found");
        assertEquals(exception.getResponse().getStatusInfo(), Response.Status.NOT_FOUND);
    }
    //when a player one requests one of the other player's pits
    //when a player two requests one of the other player's pits
    //when an invalid pit is requested
    //when a player chooses p1 kalah
    //when a player chooses p2 kalah


    private void readGames() throws IOException {
        final InputStream is = GameServiceTest.class.getResourceAsStream("/games.json");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            final Map<Integer, Game> games = objectMapper
                    .readValue(reader, new TypeReference<Map<Integer, Game>>() {
                    });
        }
    }
}