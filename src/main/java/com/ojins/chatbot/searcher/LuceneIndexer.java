package com.ojins.chatbot.searcher;

import com.ojins.chatbot.dialog.QAState;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 11/13/16.
 */
public class LuceneIndexer {

    private static transient final Logger LOG = LoggerFactory.getLogger(LuceneIndexer.class);

    private Analyzer chineseAnalyzer = new ChineseSynonymAnalyzer();
    private IndexWriterConfig config = new IndexWriterConfig(chineseAnalyzer);
    private Directory index = new RAMDirectory();

    private static void indexQAState(IndexWriter w, QAState qaState) {
        // Each qastate is map to a set of docs. each of them has different question
        // but they all have the same answer
        // consider each doc as a emission path, they all lead to the same state.
        qaState.getAnswers().stream().forEach(p -> {
            Document doc = new Document();
            doc.add(new TextField("Question", p, Field.Store.NO));
            doc.add(new StoredField("QAStateId", qaState.hashCode()));
            try {
                w.addDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void index(Set<QAState> qaStates) throws IOException {
        IndexWriter w = new IndexWriter(index, config);
        qaStates.stream().forEach(p-> {
            indexQAState(w, p);
        });
        w.close();
    }
}
