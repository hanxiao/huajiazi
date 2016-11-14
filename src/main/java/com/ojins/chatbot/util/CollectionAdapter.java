package com.ojins.chatbot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by han on 11/14/16.
 */
public class CollectionAdapter implements JsonSerializer<Collection<?>> {
    public JsonElement serialize(Collection<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty() || src.toString().trim().length() == 0) // exclusion is made here
            return null;

        JsonArray array = new JsonArray();

        for (Object child : src) {
            JsonElement element = context.serialize(child);
            array.add(element);
        }

        return array;
    }
}