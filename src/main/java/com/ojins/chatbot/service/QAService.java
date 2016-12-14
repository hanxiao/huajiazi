package com.ojins.chatbot.service;

import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by han on 12/5/16.
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QAService {
    public static String UNSOLVED_MARKER = "unsolved";
    LuceneIndexer luceneIndexer;
    LuceneReader luceneReader;
    String curTopic;

    QAService(Set<QAPair> qaStates, String topic, boolean overwrite, String dir) {
        curTopic = topic;
        String indexDir = dir.endsWith("/") ? dir : dir + "/";

        val fp = Paths.get(indexDir, topic);
        val luceneIndexerBuilder = new LuceneIndexerBuilder()
                .setFilePath(fp.toString())
                .setOverwrite(overwrite);

        if (Files.exists(fp) && overwrite) {
            luceneIndexerBuilder.setQaStates(qaStates);
            log.info("topic: {} already exists, but I will overwrite it", topic);
        } else if (!Files.exists(fp)) {
            luceneIndexerBuilder.setQaStates(qaStates);
            log.info("I will create a new topic {}", topic);
        } else if (Files.exists(fp) && !overwrite) {
            log.info("topic: {} already exists, I will load it", topic);
        }

        luceneIndexer = luceneIndexerBuilder.createLuceneIndexer();

        luceneReader = new LuceneReaderBuilder()
                .setIndexer(luceneIndexer)
                .createLuceneReader();

        printServiceInfo();
    }

    public static Optional<String[]> getAvailableTopics(String indexDir) {
        indexDir += indexDir.endsWith("/") ? "" : "/";
        File file = new File(indexDir);

        if (file.exists()) {
            return Optional.ofNullable(file.list((current, name) -> new File(current, name).isDirectory()));
        } else {
            return Optional.empty();
        }
    }

    public static QAService selectTopic(Map<String, QAService> qaServiceMap, String topic) {
        return qaServiceMap.getOrDefault(topic, qaServiceMap.get("default"));
    }

    public static Optional<QAService> selectTopic(String topic, String indexDir) {
        val topics = getAvailableTopics(indexDir);

        if (topics.isPresent() && new HashSet<>(Arrays.asList(topics.get())).contains(topic)) {
            return Optional.of(new QAServiceBuilder()
                    .setIndexDir(indexDir)
                    .setTopic(topic)
                    .createQAService());
        } else {
            log.warn("Do not support topic {}", topic);
            return Optional.empty();
        }
    }

    public int getNumDocs() {
        try {
            return luceneReader.getNumDocs();
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public Optional<QAPair> getAnswer(String question) {
        try {
            return luceneReader.getAnswers(question);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<List<QAPair>> getUnsolved() {
        try {
            return luceneReader.getUnsolved();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<List<QAPair>> getAll() {
        try {
            return luceneReader.getAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Optional<QAPair>> getAnswer(String[] question) {
        return Arrays.stream(question).map(this::getAnswer).collect(Collectors.toList());
    }

    public boolean addQAPair(String question, String answer) {
        return addQAPair(question, answer, true);
    }

    public boolean addQAPair(QAPair qaPair, boolean overwrite) {
        return luceneIndexer.addQAPair(qaPair, overwrite);
    }

    public boolean addQAPair(QAPair qaPair) {
        return luceneIndexer.addQAPair(qaPair, true);
    }

    public boolean addQAPair(String question, String answer, boolean overwrite) {
        return luceneIndexer.addQAPair(
                new QAPairBuilder()
                        .setQuestion(question)
                        .setAnswer(answer)
                        .build(), overwrite);
    }

    public void printServiceInfo() {
        try {
            log.info("topic: {}, docs: {}", curTopic, luceneReader.getNumDocs());
        } catch (IOException ex) {
            log.error("Something wrong when printing service info", ex);
        }
    }
}
