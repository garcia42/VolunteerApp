package com.example.jegarcia.VolunteerMaps.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ListToStringConverter implements JsonSerializer<String>,
        JsonDeserializer<String> {

    @Override
    public String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        StringBuilder builder = new StringBuilder();
        JsonArray ja = jsonElement.getAsJsonArray();
        builder.append(",0"); //For the "all" category
        if (ja == null || ja.size() == 0) {
            builder.append(",");
        }
        for (JsonElement je : ja) {
            builder.append(",");
            builder.append(je.getAsString());
            builder.append(",");
        }
        return builder.toString();
    }

    @Override
    public JsonElement serialize(String s, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray ja = new JsonArray();
        for (String category : s.split(",")) {
            ja.add(jsonSerializationContext.serialize(category));
        }
        return ja;
    }
}
