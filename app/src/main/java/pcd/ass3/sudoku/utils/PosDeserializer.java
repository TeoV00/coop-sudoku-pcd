package pcd.ass3.sudoku.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PosDeserializer {

    public static class StdPairDeserializer extends StdDeserializer<Pos> {
        public StdPairDeserializer() {
            super(Pos.class);
        }

        @Override
        public Pos deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        int x = node.get("x").asInt();
        int y = node.get("y").asInt();
        return new Pos(x, y);
        }   
    }

    public static class PosKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
            System.err.println(key);
            return new Pos(2,3);
        }

    }
    
}

