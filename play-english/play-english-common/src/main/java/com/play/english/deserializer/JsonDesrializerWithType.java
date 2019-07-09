package com.play.english.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * @author chaiqx on 2019/7/5
 */
public abstract class JsonDesrializerWithType<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonDeserializer<? extends JsonNode> deserializer = JsonNodeDeserializer
                .getDeserializer(ObjectNode.class);
        ObjectNode node = (ObjectNode) deserializer.deserialize(jp, ctxt);
        int type = 0;
        if (node != null && node.get("type") != null) {
            type = node.get("type").intValue();
        }
        return deserialize(node, type);
    }

    protected abstract T deserialize(ObjectNode node, int type);
}
