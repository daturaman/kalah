package backbase.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a round of Kalah.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Game {

    private int id;
    private String url;
    private Map<Integer, Integer> status;
    private String result;
    @JsonIgnore
    private boolean playerTwoTurn;

    /**
     * Default constructor.
     */
    public Game() {
        //Required for Jackson serialisation
    }

    /**
     * Creates a Game instance with the specified parameters.
     *
     * @param id unique ID of the game.
     * @param url URL of the game.
     * @param status map representing the state of the game board.
     * @param playerTwoTurn indicates if this is player two's turn.
     * @param result will display the provided message if the game has ended.
     */
    public Game(int id, String url, Map<Integer, Integer> status, boolean playerTwoTurn, String result) {
        this.id = id;
        this.url = url;//TODO make a URL
        this.status = status;
        this.result = result;
        this.playerTwoTurn = playerTwoTurn;
    }

    /**
     * Gets the game ID.
     *
     * @return the game ID.
     */
    @JsonProperty
    public int getId() {
        return id;
    }

    /**
     * Gets the game URL.
     *
     * @return the game URL.
     */
    @JsonProperty
    public String getUrl() {
        return url;
    }

    /**
     * Gets the status of the game.
     *
     * @return the current status of the game.
     */
    @JsonProperty
    public Map<Integer, Integer> getStatus() {
        return status;
    }

    /**
     * Gets the result of the game, if it has been completed. Returns null if it is still in progress.
     *
     * @return the result of the game.
     */
    @JsonProperty
    public String getResult() {
        return result;
    }

    /**
     * Indicates if the next turn is player two's.
     *
     * @return true if it is player two's turn.
     */
    public boolean isPlayerTwoTurn() {
        return playerTwoTurn;
    }
}
