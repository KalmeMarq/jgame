package me.kalmemarq.jgame;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
    public static final ObjectMapper OBJECT_MAPPER;

    /**
     * If the given JsonNode has a property fieldName with a non-null string value, returns true, otherwise false.
     * @param node the ObjectNode
     * @param fieldName the property key
     * @return true if there is the given property with a non-null string value, otherwise false.
     */
    public static boolean hasNonNullString(JsonNode node, String fieldName) {
        return node.hasNonNull(fieldName) && node.get(fieldName).isTextual();
    }

    /**
     * If the given JsonNode has a property fieldName with a non-null ArrayNode value, returns true, otherwise false.
     * @param node the ObjectNode
     * @param fieldName the property key
     * @return true if there is the given property with a non-null ArrayNode value, otherwise false.
     */
    public static boolean hasNonNullArray(JsonNode node, String fieldName) {
        return node.hasNonNull(fieldName) && node.get(fieldName).isArray();
    }

    /**
     * If the given JsonNode has a property fieldName with a non-null ObjectNode value, returns true, otherwise false.
     * @param node the ObjectNode
     * @param fieldName the property key
     * @return true if there is the given property with a non-null ObjectNode value, otherwise false.
     */
    public static boolean hasNonNullObject(JsonNode node, String fieldName) {
        return node.hasNonNull(fieldName) && node.get(fieldName).isObject();
    }

    static {
        OBJECT_MAPPER = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }
}
