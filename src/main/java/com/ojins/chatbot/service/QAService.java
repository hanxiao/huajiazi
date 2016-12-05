package com.ojins.chatbot.service;

import com.ojins.chatbot.dialog.QAState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Created by han on 12/5/16.
 */
public class QAService {
    private LuceneIndexer luceneIndexer;
    private LuceneReader luceneReader;
    private String curTopic;

    private static transient final Logger LOG = LoggerFactory.getLogger(LuceneReader.class);

    public QAService(Set<QAState> qaStates, String topic) {
        curTopic = topic;
        Path fp = Paths.get("index", topic);

        if (!Files.exists(fp) || !qaStates.isEmpty()) {
            LOG.info(String.format("topic: %s does not exist, will create a new one", topic));

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

    public void addQAPair(String question, String answer) {

    }

    public void printServiceInfo() {
        try {
            LOG.info(String.format(
                    "\n*******************\n" +
                            "Topic: %s\n" +
                            "Docs: %d\n" +
                            "*******************", curTopic, luceneReader.getNumDocs()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
