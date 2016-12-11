package com.ojins.chatbot.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ojins.chatbot.util.CollectionAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

/**
 * Created by hxiao on 2016/12/5.
 */

@Data
@Slf4j
@AllArgsConstructor
public class QAPair {
    private static transient final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();
    String question, answer, topic;
    Set<String> didYouMean, followUp;
    double score;
    int hits;

    public static Set<QAPair> loadStatesFromFile(String fp) throws IOException {
        val content = new String(Files.readAllBytes(Paths.get(fp)));
        Type setType = new TypeToken<Set<QAPair>>() {
        }.getType();
        return gson.fromJson(content, setType);
    }

    public static QAPair buildStateFromJson(String json) {
        Type setType = new TypeToken<QAPair>() {
        }.getType();
        return gson.fromJson(json, setType);
    }

    public static void writeStatesToFile(Set<QAPair> states, String fp) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(fp), false))) {
            writer.println(gson.toJson(states));
            writer.flush();
        } catch (Exception ex) {
            log.error("Something wrong when writing states to the file", ex);
        }
    }

    public void incrementScore(double delta) {
        this.score += delta;
    }

    public void incrementHits(int delta) {
        this.hits += delta;
    }

    public void incrementHitsByOne() {
        incrementHits(1);
    }

    public boolean isValid() {
        return question != null && answer != null && topic != null;
    }
}
