package backbase.services;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
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
        final Game game = new Game(id, "http://localtest.me:8080/games/" + id, null, false);
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
        if (!game.isPlayerTwoTurn() && pitId > PLAYER_ONE_KALAH) {
            throw new WebApplicationException("Player one - please select pits numbered 1 to 6", BAD_REQUEST);
        }
        if (game.isPlayerTwoTurn() && pitId < PLAYER_TWO_FIRST_PIT) {
            throw new WebApplicationException("Player two - please select pits numbered 8 to 13", BAD_REQUEST);
        }

        final Map<Integer, Integer> pits = game.getStatus() != null ? game.getStatus() : createStartingBoard();
        //Get the stones from the selected pit
        int stoneCount = pits.get(pitId);
        //Pit is now empty
        pits.compute(pitId, (pit, stones) -> stones = 0);
        //Create a cyclic iterable
        final Iterator<Map.Entry<Integer, Integer>> cycle = Iterators.cycle(pits.entrySet());
        //Move to the selected starting pit
        while (true) {
            final Map.Entry<Integer, Integer> next = cycle.next();
            if (next.getKey() == pitId) {
                break;
            }
        }
        //Sow
        while (stoneCount > 0) {
            final Map.Entry<Integer, Integer> pit = cycle.next();
            if ((!game.isPlayerTwoTurn() && pit.getKey() == PLAYER_TWO_KALAH) ||
                    game.isPlayerTwoTurn() && pit.getKey() == PLAYER_ONE_KALAH) {
                continue;
            }
            System.out.println(pit.getKey());
            pit.setValue(pit.getValue() + 1);
            stoneCount--;
        }

        final boolean playerTwoTurn = true;
        return new Game(game.getId(), game.getUrl(), pits, playerTwoTurn);
    }

    private Map<Integer, Integer> createStartingBoard() {
        Map<Integer, Integer> board = new HashMap<>();
        for (int i = 1; i <= PLAYER_TWO_KALAH; i++) {
            if (i == PLAYER_ONE_KALAH || i == PLAYER_TWO_KALAH) {
                board.put(i, 0);
            }
            board.put(i, STARTING_STONES);
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
