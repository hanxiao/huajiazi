package com.ojins.chatbot.dialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ojins.chatbot.util.CollectionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */
public class StateIO {
    private static transient final Logger LOG = LoggerFactory.getLogger(StateIO.class);
    private static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();

    public static Set<QAState> loadStatesFromJson(String fp) throws FileNotFoundException {
        String content = new Scanner(new File(fp)).useDelimiter("\\Z").next();
        Type setType = new TypeToken<Set<QAState>>() {
        }.getType();
        return gson.fromJson(content, setType);
    }

    public static void writeStatesToJson(Set<QAState> states, String fp) {
        String jsonOutput = gson.toJson(states);
        writeToFile(new File(fp), jsonOutput);
    }


    private static void writeToFile(File outFile, String jsonOutput) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(outFile, false));
            writer.println(jsonOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            LOG.error("Could not save Database {}", outFile, ex);
        }
    }
}
