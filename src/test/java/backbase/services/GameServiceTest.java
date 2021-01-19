package backbase.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backbase.api.Game;

class GameServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void shouldCreateNewGame() {

    }

    @Test
    void move() {
    }

    private void readGames(String gameFile) throws IOException {
        final InputStream is = GameServiceTest.class.getResourceAsStream(gameFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            final Map<Integer, Game> games = objectMapper
                    .readValue(reader, new TypeReference<Map<Integer, Game>>() {
                    });
        }
    }
}