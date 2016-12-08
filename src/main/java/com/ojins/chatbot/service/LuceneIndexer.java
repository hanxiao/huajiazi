package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.dialog.QAState;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    public LuceneIndexer(Directory index, Set<QAState> qaStates) {
        this.index = index;
        try {
            addManyQAState(qaStates, false);
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error("something wrong when adding QAState");
        }
    }

    private void indexQAState(IndexWriter w, QAState qaState) {
        // Each qastate is map to a set of docs. each of them has different question
        // but they all have the same answer
        // consider each doc as a emission path, they all lead to the same state.
        qaState.getQuestions().forEach(p -> {
            Document doc = new Document();
            doc.add(new TextField("Question", p, Field.Store.YES));
            doc.add(new TextField("Answer", qaState.getAnswers().get(0), Field.Store.YES));
            try {
                Query q = new QueryParser("Question", chineseAnalyzer)
                        .parse(QueryParser.escape(p));
                w.deleteDocuments(q);
                w.addDocument(doc);
                w.forceMergeDeletes();
                w.flush();
            } catch (Exception e) {
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
        w.commit();
        w.close();
    }

    public boolean addQAState(QAState qaState) {
        try {
            addManyQAState(new HashSet<>(Collections.singletonList(qaState)), true);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error("Something wrong when adding QAState");
            return false;
        }
    }
}
