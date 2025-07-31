package eatda.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MappingUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MappingUtils() {
    }

    public static byte[] toJsonBytes(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
