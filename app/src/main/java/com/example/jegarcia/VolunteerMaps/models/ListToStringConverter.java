package com.example.jegarcia.VolunteerMaps.models;

import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.RealmString;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by garciaj42 on 4/1/18.
 */

public class ListToStringConverter implements JsonSerializer<String>,
        JsonDeserializer<String> {

    @Override
    public String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        StringBuilder builder = new StringBuilder();
        JsonArray ja = jsonElement.getAsJsonArray();
        for (JsonElement je : ja) {
            builder.append(",");
            builder.append(new RealmString(je.getAsString()));
            builder.append(",");
        }
        return builder.toString();
    }

    @Override
    public JsonElement serialize(String s, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
