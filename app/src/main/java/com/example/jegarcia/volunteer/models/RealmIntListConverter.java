package com.example.jegarcia.volunteer.models;

import com.example.jegarcia.volunteer.models.volunteerMatchModels.RealmInt;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import io.realm.RealmList;

public class RealmIntListConverter implements JsonSerializer<RealmList<RealmInt>>,
        JsonDeserializer<RealmList<RealmInt>> {
    @Override
    public RealmList<RealmInt> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        RealmList<RealmInt> tags = new RealmList<>();
        JsonArray ja = json.getAsJsonArray();
        for (JsonElement je : ja) {
            tags.add((RealmInt) context.deserialize(je, RealmInt.class));
        }
        return tags;
    }

    @Override
    public JsonElement serialize(RealmList<RealmInt> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray ja = new JsonArray();
        for (RealmInt tag : src) {
            ja.add(context.serialize(tag));
        }
        return ja;
    }
}
