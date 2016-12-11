package com.ojins.chatbot.service;

import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
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
public class QAService {
    private LuceneIndexer luceneIndexer;
    private LuceneReader luceneReader;
    private String curTopic;

    public QAService(Set<QAPair> qaStates, String topic, boolean overwrite) {
        curTopic = topic;
        val fp = Paths.get("index", topic);
        val luceneIndexerBuilder = new LuceneIndexerBuilder()
                .setFilePath(fp.toString())
                .setOverwrite(overwrite)
                .setQaStates(qaStates);

        if (Files.exists(fp) && overwrite) {
            log.info(String.format("topic: %s already exists, but I will overwrite it", topic));
        } else if (!Files.exists(fp)) {
            log.info(String.format("topic: %s not exists, I will create it", topic));
        } else if (Files.exists(fp) && !overwrite) {
            log.info(String.format("topic: %s already exists, will loading from it", topic));
        }

        luceneIndexer = luceneIndexerBuilder.createLuceneIndexer();

        luceneReader = new LuceneReaderBuilder()
                .setIndexer(luceneIndexer)
                .createLuceneReader();

        printServiceInfo();
    }

    public static String[] getAvailableTopics() {
        File file = new File("index/");
        return file.list((current, name) -> new File(current, name).isDirectory());
    }

    public static QAService selectTopic(Map<String, QAService> qaServiceMap, String topic) {
        return qaServiceMap.getOrDefault(topic, qaServiceMap.get("default"));
    }

    public static Optional<QAService> selectTopic(String topic) {
        if (new HashSet<>(Arrays.asList(getAvailableTopics())).contains(topic)) {
            return Optional.of(new QAServiceBuilder().setTopic(topic).createQAService());
        } else {
            log.warn(String.format("Do not support topic %s", topic));
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

    public List<Optional<QAPair>> getAnswer(String[] question) {
        return Arrays.stream(question).map(this::getAnswer).collect(Collectors.toList());
    }

    public boolean addQAPair(String question, String answer) {
        return addQAPair(question, answer, false);
    }

    public boolean addQAPair(QAPair qaPair, boolean overwrite) {
        return luceneIndexer.addQAPair(qaPair, overwrite);
    }

    public boolean addQAPair(QAPair qaPair) {
        return luceneIndexer.addQAPair(qaPair, false);
    }

    public boolean addQAPair(String question, String answer, boolean overwrite) {
        return luceneIndexer.addQAPair(
                new QAPairBuilder()
                        .setQuestion(question)
                        .setAnswer(answer)
                        .build(), overwrite);
    }

    private void printServiceInfo() {
        try {
            log.info(String.format("topic: %s;\tdocs: %d\n", curTopic, luceneReader.getNumDocs()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
