package backbase.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backbase.api.Game;

/**
 * Service for managing games of Kalah.
 */
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private static final String GAMES_STORE = "/games.json";
    private static final int PLAYER_ONE_KALAH = 7;
    private static final int PLAYER_TWO_KALAH = 14;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Integer, Game> gamesCache = new HashMap<>();

    public GameService() {
        final InputStream is = GameService.class.getResourceAsStream(GAMES_STORE);
        StringBuilder builder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            gamesCache = objectMapper.readValue(reader, new TypeReference<Map<Integer, Game>>() {});
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line).append("\n");
//            }
        } catch (IOException e) {
            logger.error("Exception thrown whilst attempting to populate games cache: ", e);
            throw new IllegalStateException("Unable to populate games cache.");
        }



    }
    /**
     *  Performs a putsert for new and existing games.
     * @param game
     */
    public void save(Game game) {
//        try {
//            objectMapper.writeValue(new File(GAMES_STORE), game);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public synchronized Game move(int gameId, int pitId) {
        //Load game from file and deserialise
        //handle errors with move
            //Wrong player pits
            //
        return null;
    }

    public Game read(int id) {
        return gamesCache.get(id);
    }
}
