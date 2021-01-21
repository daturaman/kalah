package backbase.services;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;

import backbase.api.Game;
import io.dropwizard.lifecycle.Managed;

/**
 * Service for managing games of Kalah.
 */
public class GameService implements Managed {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private static final String GAMES_STORE = "/games.json";
    private static final int PLAYER_ONE_KALAH = 7;
    private static final int PLAYER_TWO_KALAH = 14;
    private static final int MAX_ID = 9999;
    private static final int FIRST_PIT = 1;
    private static final int LAST_PIT = 13;
    private static final int PLAYER_TWO_FIRST_PIT = 8;
    private static final int STARTING_STONES = 6;
    private static final Map<Integer, Integer> playerOneToPlayerTwoPits = Map
            .of(1, 13, 2, 12, 3, 11, 4, 10, 5, 9, 6, 8);
    private static final Map<Integer, Integer> playerTwoToPlayerOnePits = Map
            .of(8, 6, 9, 5, 10, 4, 11, 3, 12, 2, 13, 1);
    private static final List<Integer> playerOnePits = List.of(1, 2, 3, 4, 5, 6);
    private static final List<Integer> playerTwoPits = List.of(8, 9, 10, 11, 12, 13);
    private static final String GAME_ID_NOT_FOUND = "Game ID not found";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Integer, Game> gamesCache = new HashMap<>();

    /**
     * Retrieve a {@link Game} with the provided ID.
     *
     * @param gameId ID of the game to retrieve.
     * @return the Game specified by the ID.
     */
    public Game read(int gameId) {
        final Game game = gamesCache.get(gameId);
        if (game == null) {
            throw new WebApplicationException(GAME_ID_NOT_FOUND, NOT_FOUND);
        }
        return game;
    }

    /**
     * Retrieves all the games.
     *
     * @return a Map of all existing games.
     */
    public Map<Integer, Game> read() {
        return gamesCache;
    }

    /**
     * Creates a new game..
     *
     * @return a new {@link Game} instance.
     */
    public Game create() {
        final int id = new Random().nextInt(MAX_ID);
        final Game game = new Game(id, "http://localtest.me:8080/games/" + id, null, false, null);
        gamesCache.putIfAbsent(id, game);
        return game;
    }

    /**
     * Executes a single move in a game of Kalah.
     *
     * @param gameId the ID of  the game.
     * @param pitId the pit from which to begin sowing.
     * @return the game with its status updated after executing the requested move.
     */
    public Game move(int gameId, int pitId) {
        //validate move
        if (!gamesCache.containsKey(gameId)) {
            throw new WebApplicationException("Game ID not found", NOT_FOUND);
        }
        if (pitId == PLAYER_ONE_KALAH || pitId == PLAYER_TWO_KALAH) {
            throw new WebApplicationException("You can't pick from a kalah!", BAD_REQUEST);
        }
        if (pitId < FIRST_PIT || pitId > LAST_PIT) {
            throw new WebApplicationException("Invalid pit ID", BAD_REQUEST);
        }

        final Game game = gamesCache.get(gameId);
        //TODO simply return the game if it has ended (304?)
//        if (game.getStatus().entrySet().stream().filter(pit -> pit.getKey() != PLAYER_ONE_KALAH)
//                .filter(pit -> pit.getKey() != PLAYER_TWO_KALAH).map(
//                        Map.Entry::getValue).count() == 0) {
//            return game;//TODO Exception when game has ended?
//        }

        boolean playerTwoTurn = game.isPlayerTwoTurn();
        if (!playerTwoTurn && pitId > PLAYER_ONE_KALAH) {
            throw new WebApplicationException("Player one - please select pits numbered 1 to 6", BAD_REQUEST);
        }
        if (playerTwoTurn && pitId < PLAYER_TWO_FIRST_PIT) {
            throw new WebApplicationException("Player two - please select pits numbered 8 to 13", BAD_REQUEST);
        }

        final Map<Integer, Integer> pits = game.getStatus() != null ? game.getStatus() : createStartingBoard();
        //Get the stones from the selected pit
        int stoneCount = pits.get(pitId);
        if (stoneCount == 0) {
            throw new WebApplicationException("You chose an empty pit", BAD_REQUEST);
        }
        //Pit is now empty
        clearPit(pitId, pits);
        //Create a cyclic iterable
        final Iterator<Map.Entry<Integer, Integer>> pitIterator = Iterators.cycle(pits.entrySet());
        //Move to the selected starting pit
        while (true) {
            final Map.Entry<Integer, Integer> next = pitIterator.next();
            if (next.getKey() == pitId) {
                break;
            }
        }
        //Sow
        for (; stoneCount > 0; stoneCount--) {
            Map.Entry<Integer, Integer> currentPit = pitIterator.next();
            Integer currentPitId = currentPit.getKey();
            //Skip opposing player's kalah
            if ((!playerTwoTurn && currentPitId == PLAYER_TWO_KALAH) ||
                    playerTwoTurn && currentPitId == PLAYER_ONE_KALAH) {
                currentPit = pitIterator.next();
                currentPitId = currentPit.getKey();
            }

            currentPit.setValue(currentPit.getValue() + 1);

            //Evaluate where last stone placed
            if (stoneCount == 1) {
                if (!playerTwoTurn) {
                    if (currentPitId == PLAYER_ONE_KALAH) {
                        break;
                    } else if (currentPit.getValue() == 1 && playerOnePits.contains(currentPitId)) {
                        //Player one kalah gets all stones from opposing pit, plus their own
                        bankStonesFromOpposingPits(currentPitId, PLAYER_ONE_KALAH, playerOneToPlayerTwoPits, pits);
                        playerTwoTurn = true;
                    } else {
                        playerTwoTurn = true;
                    }
                } else {
                    if (currentPitId == PLAYER_TWO_KALAH) {
                        break;
                    } else if (currentPit.getValue() == 1 && playerTwoPits.contains(currentPitId)) {
                        //Player two kalah gets all stones from opposing pit, plus their own
                        bankStonesFromOpposingPits(currentPitId, PLAYER_TWO_KALAH, playerTwoToPlayerOnePits, pits);
                        playerTwoTurn = false;
                    } else {
                        playerTwoTurn = false;
                    }
                }
            }
        }

        //TODO if player has no stones left, add other player's remaining stones to their kalah and perform final tally

        final Game updated = new Game(gameId, game.getUrl(), pits, playerTwoTurn, null);
        gamesCache.replace(gameId, updated);
        return updated;
    }

    private void bankStonesFromOpposingPits(int currentPitId, int kalah, Map<Integer, Integer> converter, Map<Integer
            , Integer> pits) {
        int oppositePit = converter.get(currentPitId);
        int bothPlayerStones = pits.get(oppositePit) + 1;
        pits.compute(kalah, (pit, stones) -> stones += bothPlayerStones);
        clearPit(currentPitId, pits);
        clearPit(oppositePit, pits);
    }

    private Integer clearPit(int pitId, Map<Integer, Integer> pits) {
        return pits.compute(pitId, (pit, stones) -> stones = 0);
    }

    private Map<Integer, Integer> createStartingBoard() {
        Map<Integer, Integer> board = new HashMap<>();
        for (int i = 1; i <= PLAYER_TWO_KALAH; i++) {
            if (i == PLAYER_ONE_KALAH || i == PLAYER_TWO_KALAH) {
                board.put(i, 0);
            } else {
                board.put(i, STARTING_STONES);
            }
        }
        return board;
    }

    private void readFileToCache(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            gamesCache = objectMapper.readValue(reader, new TypeReference<Map<Integer, Game>>() {
            });
        } catch (IOException e) {
            logger.error("Exception thrown whilst attempting to populate games cache: ", e);
            throw new IllegalStateException("Unable to populate games cache.");
        }
    }

    @Override
    public void start() throws Exception {
        //TODO or just  store to memory
        //TODO write to a temp file, not resources
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(GameService.class.getResourceAsStream(GAMES_STORE)))) {
            gamesCache = objectMapper.readValue(reader, new TypeReference<Map<Integer, Game>>() {
            });
        } catch (IOException e) {
            logger.error("Exception thrown whilst attempting to populate games cache: ", e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
//TODO write cache to file
    }
}
