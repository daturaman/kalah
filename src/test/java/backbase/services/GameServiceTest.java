package backbase.services;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import backbase.api.Game;

class GameServiceTest {

    private static final int PLAYER_ONE_KALAH = 7;
    private static final int PLAYER_TWO_KALAH = 14;
    private GameService gameService;
    private Game game;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        game = gameService.create();
    }

    @Test
    public void playerOneShouldGetAnotherMoveWhenLastPitSowedIsKalah() {
        game = gameService.move(game.getId(), 1);
        assertFalse(game.isPlayerTwoTurn());
        //Player one can make another move
        game = gameService.move(game.getId(), 2);
        assertTrue(game.isPlayerTwoTurn());

    }

    @Test
    public void playerTwoShouldGetAnotherMoveWhenLastPitSowedIsKalah() {
        //Player one moves
        game = gameService.move(game.getId(), 2);
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

    @Test
    public void playerOneShouldBankAllStonesWhenLastPitIsOwnAndEmpty() {
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

    @Test
    public void playerTwoShouldBankAllStonesWhenLastPitIsOwnAndEmpty() {
        //Player one moves
        gameService.move(game.getId(), 6);
        //Player two moves
        gameService.move(game.getId(), 8);
        //Player one moves
        gameService.move(game.getId(), 5);
        //Player two moves
        gameService.move(game.getId(), 9);
        //Player one moves
        gameService.move(game.getId(), 4);
        //Player two moves
        gameService.move(game.getId(), 8);
        //Player one moves
        gameService.move(game.getId(), 2);
        //Player two moves
        gameService.move(game.getId(), 12);
        //Player one moves
        gameService.move(game.getId(), 3);
        //Player two moves
        game = gameService.move(game.getId(), 10);

        assertFalse(game.isPlayerTwoTurn());
        int expectedPlayerTwoKalahTally = 9;
        assertEquals(expectedPlayerTwoKalahTally, game.getStatus().get(PLAYER_TWO_KALAH));
        assertEquals(0, game.getStatus().get(10));
        assertEquals(0, game.getStatus().get(4));
    }

    @Test
    public void shouldEndGameAndRunTallyWhenOnePlayerRunsOutOfStones() {
        gameService.move(game.getId(), 1);
        gameService.move(game.getId(), 5);
        gameService.move(game.getId(), 8);
        gameService.move(game.getId(), 1);
        gameService.move(game.getId(), 9);
        gameService.move(game.getId(), 2);
        gameService.move(game.getId(), 8);
        gameService.move(game.getId(), 6);
        gameService.move(game.getId(), 8);
        gameService.move(game.getId(), 4);
        gameService.move(game.getId(), 8);
        gameService.move(game.getId(), 6);
        gameService.move(game.getId(), 5);
        gameService.move(game.getId(), 6);
        gameService.move(game.getId(), 1);
        gameService.move(game.getId(), 12);
        gameService.move(game.getId(), 3);
        gameService.move(game.getId(), 12);
        gameService.move(game.getId(), 6);
        gameService.move(game.getId(), 5);
        gameService.move(game.getId(), 11);
        gameService.move(game.getId(), 6);
        gameService.move(game.getId(), 5);
        gameService.move(game.getId(), 13);
        gameService.move(game.getId(), 6);
        gameService.move(game.getId(), 5);
        gameService.move(game.getId(), 12);
        gameService.move(game.getId(), 10);
        gameService.move(game.getId(), 5);
        gameService.move(game.getId(), 10);
        gameService.move(game.getId(), 3);
        gameService.move(game.getId(), 13);
        gameService.move(game.getId(), 1);
        gameService.move(game.getId(), 12);
        gameService.move(game.getId(), 8);
        gameService.move(game.getId(), 2);
        gameService.move(game.getId(), 3);
        game = gameService.move(game.getId(), 13);
        assertEquals("Player one wins!!", game.getResult());
        assertIterableEquals(List.of(0, 0, 0, 0, 0, 0, 52, 0, 0, 0, 0, 0, 0, 21), game.getStatus().values());
    }

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