package backbase.services;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backbase.api.Game;

class GameServiceTest {

    private static final int PLAYER_ONE_KALAH = 7;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private GameService gameService;
    private Game game;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        game = gameService.create();
    }

    //TODO one test?
    @Test
    public void playerOneShouldGetAnotherMoveWhenLastPitSowedIsKalah() {
        game = gameService.move(game.getId(), 1);
        //TODO assert game status
        assertFalse(game.isPlayerTwoTurn());
        //Player one can make another move
        game = gameService.move(game.getId(), 2);
        assertTrue(game.isPlayerTwoTurn());

    }

    @Test
    public void playerTwoShouldGetAnotherMoveWhenLastPitSowedIsKalah() {
        //Player one moves
        game = gameService.move(game.getId(), 2);
        //TODO assert game status
        assertTrue(game.isPlayerTwoTurn());
        //Player two moves
        game = gameService.move(game.getId(), 13);
        assertFalse(game.isPlayerTwoTurn());
        //Player one moves
        game = gameService.move(game.getId(), 6);
        assertTrue(game.isPlayerTwoTurn());
        //Player two moves and gets another turn
        game = gameService.move(game.getId(), 13);
        assertTrue(game.isPlayerTwoTurn());

    }
    //Is player one's turn when last p2 stone does not land in kalah
    //Is player two's turn when last p1 stone does not land in kalah

    @Test
    public void playerOneShouldBankAllStonesFromRankWhenLastPitIsOwnAndEmpty() {
        //Player one moves
        gameService.move(game.getId(), 1);
        //Player one ends up kalah, gets another turn
        gameService.move(game.getId(), 5);
        //Player two moves
        gameService.move(game.getId(), 8);
        //Player one moves
        gameService.move(game.getId(), 1);
        //Player two moves
        gameService.move(game.getId(), 9);
        //Player one moves
        gameService.move(game.getId(), 2);
        //Player two moves
        gameService.move(game.getId(), 8);
        //Player one moves and captures stones from opposing kalah
        game = gameService.move(game.getId(), 6);
        assertTrue(game.isPlayerTwoTurn());
        int expectedPlayerOneKalahTally = 15;
        assertEquals(expectedPlayerOneKalahTally, game.getStatus().get(PLAYER_ONE_KALAH));
        assertEquals(0, game.getStatus().get(2));
        assertEquals(0, game.getStatus().get(12));
    }

    //TODO
    @Test
    public void playerTwoShouldBankAllStonesFromRankWhenLastPitIsOwnAndEmpty() {
        //Player one moves
        gameService.move(game.getId(), 1);
        //Player one ends up kalah, gets another turn
        gameService.move(game.getId(), 5);
        //Player two moves
        gameService.move(game.getId(), 8);
        //Player one moves
        gameService.move(game.getId(), 1);
        //Player two moves
        gameService.move(game.getId(), 9);
        //Player one moves
        gameService.move(game.getId(), 2);
        //Player two moves
        gameService.move(game.getId(), 8);
        //Player one moves and captures stones from opposing kalah
        game = gameService.move(game.getId(), 6);
        assertTrue(game.isPlayerTwoTurn());
        int expectedPlayerOneKalahTally = 15;
        assertEquals(expectedPlayerOneKalahTally, game.getStatus().get(PLAYER_ONE_KALAH));
        assertEquals(0, game.getStatus().get(2));
        assertEquals(0, game.getStatus().get(12));
    }

    //when a player runs out of stones
    //and the stones are tallied and the game is over

    //ERRORS
    @Test
    public void shouldThrowExceptionWhenPassedInvalidGameId() {
        final WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> gameService.move(-1, 1));
        final String actualError = exception.getMessage();
        final String expectedError = "Game ID not found";
        assertEquals(expectedError, actualError);
        assertEquals(NOT_FOUND, exception.getResponse().getStatusInfo());
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 9, 10, 11, 12, 13})
    public void shouldThrowExceptionWhenPlayerOneSelectsOtherPlayersPit(int playerTwoPit) {
        final WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> gameService.move(game.getId(), playerTwoPit));
        final String actualError = exception.getMessage();
        final String expectedError = "Player one - please select pits numbered 1 to 6";
        assertEquals(expectedError, actualError);
        assertEquals(BAD_REQUEST, exception.getResponse().getStatusInfo());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    public void shouldThrowExceptionWhenPlayerTwoSelectsOtherPlayersPit(int playerOnePit) {
        //Player one moves
        gameService.move(game.getId(), 2);
        //Player two moves
        final WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> gameService.move(game.getId(), playerOnePit));
        final String actualError = exception.getMessage();
        final String expectedError = "Player two - please select pits numbered 8 to 13";
        assertEquals(expectedError, actualError);
        assertEquals(BAD_REQUEST, exception.getResponse().getStatusInfo());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 15, 0, Integer.MAX_VALUE})
    public void shouldThrowExceptionWhenInvalidPitSelected(int pitId) {
        final WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> gameService.move(game.getId(), pitId));
        final String actualError = exception.getMessage();
        final String expectedError = "Invalid pit ID";
        assertEquals(expectedError, actualError);
        assertEquals(BAD_REQUEST, exception.getResponse().getStatusInfo());

    }

    @ParameterizedTest
    @ValueSource(ints = {7, 14})
    public void shouldThrowExceptionWhenSelectedPitIsKalah(int kalahPit) {
        final WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> gameService.move(game.getId(), kalahPit));
        final String actualError = exception.getMessage();
        final String expectedError = "You can't pick from a kalah!";
        assertEquals(expectedError, actualError);
        assertEquals(BAD_REQUEST, exception.getResponse().getStatusInfo());
    }
}