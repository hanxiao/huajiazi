package com.ojins.chatbot.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.util.CollectionAdapter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by han on 11/13/16.
 */
public class LuceneIndexer {

    private static transient final Logger LOG = LoggerFactory.getLogger(LuceneIndexer.class);

    public Analyzer getChineseAnalyzer() {
        return chineseAnalyzer;
    }

    public Directory getIndex() {
        return index;
    }

    private Analyzer chineseAnalyzer = new ChineseSynonymAnalyzer();
    private Directory index;
    private static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();

    public LuceneIndexer(Directory index, Set<QAState> qaStates) {
        this.index = index;
        try {
            addManyQAState(qaStates, false);
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error("something wrong when adding QAState");
        }
    }

    private static void indexQAState(IndexWriter w, QAState qaState) {
        // Each qastate is map to a set of docs. each of them has different question
        // but they all have the same answer
        // consider each doc as a emission path, they all lead to the same state.
        qaState.getQuestions().forEach(p -> {
            Document doc = new Document();
            doc.add(new TextField("Question", p, Field.Store.NO));
            doc.add(new StoredField("Answer", qaState.getAnswers().get(0)));
            try {
                w.addDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addManyQAState(Set<QAState> qaStates, boolean append) throws IOException {
        if (qaStates.isEmpty()) return;
        IndexWriter w = new IndexWriter(index, new IndexWriterConfig(chineseAnalyzer));
        if (!append) {
            w.deleteAll();
            w.commit();
        }
        qaStates.forEach(p -> {
            indexQAState(w, p);
        });
        w.close();
    }

    public void addQAState(QAState qaState) throws IOException {
        addManyQAState(new HashSet<>(Collections.singletonList(qaState)), true);
    }
}
