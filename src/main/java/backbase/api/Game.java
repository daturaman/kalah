/*
 * Copyright 2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package backbase.api;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a Kalah game.
 */
public class Game {

    private long id;
    private URL url;
    private Map<Integer, Integer> status;

    public Game() {
    }

    public Game(long id, URL url, Map<Integer, Integer> status) {
        this.id = id;
        this.url = url;
        this.status = status;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public URL getUrl() {
        return url;
    }

    @JsonProperty
    public Map<Integer, Integer> getStatus() {
        return status;
    }
}
