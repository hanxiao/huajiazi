package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.dialog.QAState;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
            addManyQAState(qaStates, false, false);
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error("something wrong when adding QAState");
        }
    }

    private void indexQAState(IndexWriter w, QAState qaState, boolean overwrite) {
        // Each qastate is map to a set of docs. each of them has different question
        // but they all have the same answer
        // consider each doc as a emission path, they all lead to the same state.
        qaState.getQuestions().forEach(p -> {
            Document doc = new Document();
            doc.add(new TextField("Question", p, Field.Store.YES));
            doc.add(new StoredField("QOriginal", p));
            doc.add(new TextField("Answer", qaState.getAnswers().get(0), Field.Store.YES));
            try {
                if (overwrite) {
                    Query q = new TermQuery(new Term("QOriginal", p));
                    w.deleteDocuments(q);
                }
                w.addDocument(doc);
                w.forceMergeDeletes();
                w.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addManyQAState(Set<QAState> qaStates, boolean append, boolean overwrite) throws IOException {
        if (qaStates.isEmpty()) return;
        IndexWriter w = new IndexWriter(index, new IndexWriterConfig(chineseAnalyzer));
        if (!append) {
            w.deleteAll();
            w.commit();
        }
        qaStates.forEach(p -> {
            indexQAState(w, p, overwrite);
        });
        w.commit();
        w.close();
    }

    public boolean addQAState(QAState qaState) {
        return addQAState(qaState, false);
    }

    public boolean addQAState(QAState qaState, boolean overwrite) {
        try {
            IndexWriter w = new IndexWriter(index, new IndexWriterConfig(chineseAnalyzer));
            indexQAState(w, qaState, overwrite);
            w.commit();
            w.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error("Something wrong when adding QAState");
            return false;
        }
    }
}
