package com.ojins.chatbot.service;

import com.ojins.chatbot.dialog.QAResult;
import com.ojins.chatbot.dialog.QAState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by han on 12/5/16.
 */
public class QAService {
    private LuceneIndexer luceneIndexer;
    private LuceneReader luceneReader;
    private String curTopic;

    private static transient final Logger LOG = LoggerFactory.getLogger(LuceneReader.class);

    public QAService(Set<QAState> qaStates, String topic, boolean overwrite) {
        curTopic = topic;
        Path fp = Paths.get("index", topic);

        if (!Files.exists(fp) || overwrite) {
            if (Files.exists(fp) && overwrite) {
                LOG.info(String.format("topic: %s already exists, but I will overwrite it", topic));
            } else if (!Files.exists(fp)) {
                LOG.info(String.format("topic: %s not exists, I will create it", topic));
            }
            luceneIndexer = new LuceneIndexerBuilder()
                    .setFilePath(fp.toString())
                    .setQAStates(qaStates)
                    .createLuceneIndexer();
        } else {
            LOG.info(String.format("topic: %s already exists, will loading from it", topic));
            luceneIndexer = new LuceneIndexerBuilder()
                    .setFilePath(fp.toString())
                    .createLuceneIndexer();
        }

        luceneReader = new LuceneReaderBuilder()
                .setIndexer(luceneIndexer)
                .createLuceneReader();

        printServiceInfo();
    }

    public int getNumDocs() {
        try {
            return luceneReader.getNumDocs();
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public static String[] getAvailableTopics() {
        File file = new File("index/");
        return file.list((current, name) -> new File(current, name).isDirectory());
    }

    public static Optional<QAService> selectTopic(String topic) {
        if (new HashSet<>(Arrays.asList(getAvailableTopics())).contains(topic)) {
            return Optional.of(new QAServiceBuilder().setTopic(topic).createQAService());
        } else {
            LOG.warn(String.format("Do not support topic %s", topic));
            return Optional.empty();
        }
    }

    public Optional<QAResult> getAnswer(String question) {
        try {
            return luceneReader.getAnswers(question);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<List<QAResult>> getUnsolved() {
        try {
            return luceneReader.getUnsolved();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Optional<QAResult>> getAnswer(String[] question) {
        return Arrays.stream(question).map(this::getAnswer).collect(Collectors.toList());
    }

    public boolean addQAPair(String question, String answer) {
        return addQAPair(question, answer, false);
    }

    public boolean addQAPair(String question, String answer, boolean update) {
        return luceneIndexer.addQAState(new QAState(question, answer), update);
    }

    private void printServiceInfo() {
        try {
            LOG.info(String.format("topic: %s;\tdocs: %d\n", curTopic, luceneReader.getNumDocs()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
