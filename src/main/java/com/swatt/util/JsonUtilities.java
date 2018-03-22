package com.swatt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilities {
    public static String objectToJsonString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }

        return json;
    }
}
