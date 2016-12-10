package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.dialog.QAPair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 11/13/16.
 */

@Slf4j
@Data
public class LuceneIndexer {

    private Analyzer chineseAnalyzer = new ChineseSynonymAnalyzer();
    private Directory index;
    private IndexWriterConfig indexWriterConfig = new IndexWriterConfig(chineseAnalyzer);

    public LuceneIndexer(Directory index, Set<QAPair> qaPairs, boolean overwrite) {
        this.index = index;
        try {
            IndexWriter w = new IndexWriter(index, indexWriterConfig);
            if (overwrite) {
                w.deleteAll();
                w.commit();
            }
            qaPairs.forEach(p -> {
                indexQAState(w, p, overwrite);
            });
            w.forceMergeDeletes();
            w.flush();
            w.commit();
            w.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error("something wrong when adding QAState");
        }
    }

    private void indexQAState(IndexWriter w, QAPair qaPair, boolean overwrite) {
        // Each qastate is map to a set of docs. each of them has different question
        // but they all have the same answer
        // consider each doc as a emission path, they all lead to the same state.

        Document doc = new Document();
        doc.add(new TextField("Question", qaPair.getQuestion(), Field.Store.YES));
        doc.add(new TextField("Answer", qaPair.getAnswer(), Field.Store.YES));
        try {
            if (overwrite) {
                Query q = new TermQuery(new Term("QOriginal", qaPair.getQuestion()));
                w.deleteDocuments(q);
            }
            w.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addQAState(QAPair qaPair) {
        return addQAState(qaPair, false);
    }

    public boolean addQAState(QAPair qaState, boolean overwrite) {
        try {
            IndexWriter w = new IndexWriter(index, indexWriterConfig);
            indexQAState(w, qaState, overwrite);
            w.commit();
            w.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error("Something wrong when adding QAState");
            return false;
        }
    }
}
