package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.model.QAPair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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

    public LuceneIndexer(Directory index, Set<QAPair> qaPairs, boolean overwrite) {
        this.index = index;
        try (IndexWriter w = new IndexWriter(index, new IndexWriterConfig(chineseAnalyzer))) {
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
        } catch (IOException ex) {
            log.error("something wrong when adding QAState", ex);
        }
    }

    private void indexQAState(IndexWriter w, QAPair qaPair, boolean overwrite) {
        // Each qastate is map to a set of docs. each of them has different question
        // but they all have the same answer
        // consider each doc as a emission path, they all lead to the same state.

        Document doc = new Document();
        doc.add(new TextField("Question", qaPair.getQuestion(), Field.Store.YES));
        doc.add(new TextField("Answer", qaPair.getAnswer(), Field.Store.YES));
        doc.add(new StringField("OriginalQuestion", qaPair.getQuestion(), Field.Store.NO));
        doc.add(new StringField("OriginalAnswer", qaPair.getAnswer(), Field.Store.NO));

        try {
            if (overwrite) {
                Query q = new TermQuery(new Term("OriginalQuestion", qaPair.getQuestion()));
                w.deleteDocuments(q);
            }
            w.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addQAPair(QAPair qaPair) {
        return addQAPair(qaPair, false);
    }

    public boolean addQAPair(QAPair qaState, boolean overwrite) {
        try (IndexWriter w = new IndexWriter(index, new IndexWriterConfig(chineseAnalyzer))) {
            indexQAState(w, qaState, overwrite);
            w.commit();
            log.info("New QAPair is added: {}", qaState);
            return true;
        } catch (IOException ex) {
            log.error("Something wrong when adding QAState", ex);
            return false;
        }
    }
}
