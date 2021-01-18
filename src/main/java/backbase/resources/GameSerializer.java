/*
 * Copyright 2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package backbase.resources;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import backbase.api.Game;

/**
 * This class is for seralising instances of {@link Game} to json objects before they are written to the game json
 * file (which is filling the role of a persistence mechanism for brevity's sake).
 */
public class GameSerializer extends JsonSerializer<Game> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

    }
}
