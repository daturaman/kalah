package backbase.services;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Integer, Game> gamesCache = new HashMap<>();

    /**
     * Performs a putsert for new and existing games.
     *
     * @param game
     */
    public void save(Game game) {
//        try {
//            objectMapper.writeValue(new File(GAMES_STORE), game);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public Game move(int gameId, int pitId) {
        //Load game from file and deserialise
        if (!gamesCache.containsKey(gameId)) {
            throw new WebApplicationException("Game ID not found", NOT_FOUND);
        }
        //handle errors with move
        //Wrong player pits
        //
        return null;
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(GameService.class.getResourceAsStream(GAMES_STORE)))) {
            gamesCache = objectMapper.readValue(reader, new TypeReference<Map<Integer, Game>>() {
            });
        } catch (IOException e) {
            logger.error("Exception thrown whilst attempting to populate games cache: ", e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {

    }
}
