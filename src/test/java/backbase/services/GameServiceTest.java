package backbase.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backbase.api.Game;

class GameServiceTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    public void shouldReadGameFromStore() {
        final GameService gameService = new GameService();
        final Game read = gameService.read(1234);
        Assertions.assertNotNull(read);
    }

    @Test
    void saveGame() {
    }

    @Test
    void move() {
    }
}