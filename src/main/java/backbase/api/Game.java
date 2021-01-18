package backbase.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a Kalah game.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Game {

    private long id;
    private String url;
    private Map<Integer, Integer> status;

    public Game() {
        //Required for Jackson serialisation
    }

    public Game(long id, String url, Map<Integer, Integer> status) {
        this.id = id;
        this.url = url;
        this.status = status;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    @JsonProperty
    public Map<Integer, Integer> getStatus() {
        return status;
    }
}
