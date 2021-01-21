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
            .of(1, 8, 2, 9, 3, 10, 4, 11, 5, 12, 6, 13);
    private static final Map<Integer, Integer> playerTwoToPlayerOnePits = Map
            .of(8, 1, 9, 2, 10, 3, 11, 4, 12, 5, 13, 6);
    private static final List<Integer> playerOnePits = List.of(1, 2, 3, 4, 5, 6);
    private static final List<Integer> playerTwoPits = List.of(8, 9, 10, 11, 12, 13);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Integer, Game> gamesCache = new HashMap<>();

    public Game read(int gameId) {
        return gamesCache.get(gameId);
    }

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
        //Pit is now empty
        pits.compute(pitId, (pit, stones) -> stones = 0);
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
            final Map.Entry<Integer, Integer> currentPit = pitIterator.next();
            Integer currentPitId = currentPit.getKey();
            //Skip opposing player's kalah
            if ((!playerTwoTurn && currentPitId == PLAYER_TWO_KALAH) ||
                    playerTwoTurn && currentPitId == PLAYER_ONE_KALAH) {
                continue;
            }

            currentPit.setValue(currentPit.getValue() + 1);

            //Evaluate where last stone placed
            if (stoneCount == 1) {
                if (!playerTwoTurn) {
                    if (currentPitId == PLAYER_ONE_KALAH) {
                        break;
                    } else if (currentPit.getValue() == 1 && playerOnePits.contains(currentPitId)) {
                        //Player one kalah gets all stones from opposing pit, plus their own
                        pits.put(PLAYER_ONE_KALAH, playerOneToPlayerTwoPits.get(currentPitId) + 1);
                        currentPit.setValue(0);
                    } else {
                        playerTwoTurn = true;
                    }
                } else {
                    if (currentPitId == PLAYER_TWO_KALAH) {
                        break;
                    } else if (currentPit.getValue() == 1 && playerTwoPits.contains(currentPitId)) {
                        //Player two kalah gets all stones from opposing pit, plus their own
                        pits.put(PLAYER_TWO_KALAH, playerTwoToPlayerOnePits.get(currentPitId) + 1);
                        currentPit.setValue(0);
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
