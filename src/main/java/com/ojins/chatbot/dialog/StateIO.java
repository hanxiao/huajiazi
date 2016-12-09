package com.ojins.chatbot.dialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ojins.chatbot.util.CollectionAdapter;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */

@Slf4j
public class StateIO {
    private static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();

    public static Set<QAPair> loadStatesFromJson(String fp) throws FileNotFoundException {
        String content = new Scanner(new File(fp)).useDelimiter("\\Z").next();
        Type setType = new TypeToken<Set<QAPair>>() {
        }.getType();
        return gson.fromJson(content, setType);
    }

    public static void writeStatesToJson(Set<QAPair> states, String fp) {
        try {
            @Cleanup PrintWriter writer = new PrintWriter(new FileOutputStream(new File(fp), false));
            writer.println(gson.toJson(states));
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Something wrong when writing states to the file");
        }
    }
}
