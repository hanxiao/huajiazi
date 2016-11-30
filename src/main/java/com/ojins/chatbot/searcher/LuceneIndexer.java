package com.ojins.chatbot.searcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.util.CollectionAdapter;
import com.ojins.chatbot.util.HelperFunction;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by han on 11/13/16.
 */
public class LuceneIndexer {

    private static transient final Logger LOG = LoggerFactory.getLogger(LuceneIndexer.class);

    private Analyzer chineseAnalyzer = new ChineseSynonymAnalyzer();
    private IndexWriterConfig config = new IndexWriterConfig(chineseAnalyzer);
    private Directory index = new RAMDirectory();
    private static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();
    private Map<Integer, QAState> qaStateMap = new HashMap<>();


    private static void indexQAState(IndexWriter w, QAState qaState) {
        // Each qastate is map to a set of docs. each of them has different question
        // but they all have the same answer
        // consider each doc as a emission path, they all lead to the same state.
        qaState.getQuestions().stream().forEach(p -> {
            Document doc = new Document();
            doc.add(new TextField("Question", p, Field.Store.NO));
            doc.add(new StoredField("QAState", qaState.hashCode()));
            try {
                w.addDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void index(Set<QAState> qaStates) throws IOException {
        IndexWriter w = new IndexWriter(index, config);
        qaStates.stream().forEach(p -> {
            indexQAState(w, p);
            qaStateMap.put(p.hashCode(), p);
        });
        w.close();
    }

    public Collection<String> search(String question) throws IOException, ParseException {
        TokenStream ts = chineseAnalyzer.tokenStream("myfield", new StringReader(question));
        HelperFunction.printTokenStream(ts);

        Query q = new QueryParser("Question", chineseAnalyzer)
                .parse(QueryParser.escape(question));

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        int hitsPerPage = 5;
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        Map<Integer, String> answers = new HashMap<>();
        for (ScoreDoc h : hits) {
            int key = Integer.parseInt(searcher.doc(h.doc).get("QAState"));
            if (!answers.containsKey(key)) {
                answers.put(key, qaStateMap.get(key).popRandomAnswer(false).orElse("EMPTY"));
            }
        }
        reader.close();
        return answers.values();
    }
}
