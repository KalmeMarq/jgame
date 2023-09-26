package me.kalmemarq.jgame;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
    public static final ObjectMapper OBJECT_MAPPER;

    public static boolean hasNonNullString(JsonNode node, String fieldName) {
        return node.hasNonNull(fieldName) && node.get(fieldName).isTextual();
    }

    public static boolean hasNonNullArray(JsonNode node, String fieldName) {
        return node.hasNonNull(fieldName) && node.get(fieldName).isArray();
    }

    public static boolean hasNonNullObject(JsonNode node, String fieldName) {
        return node.hasNonNull(fieldName) && node.get(fieldName).isObject();
    }

    static {
        OBJECT_MAPPER = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }
}
