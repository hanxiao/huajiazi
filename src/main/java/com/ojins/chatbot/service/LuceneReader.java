package com.ojins.chatbot.service;

import com.ojins.chatbot.util.HelperFunction;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by han on 12/5/16.
 */
public class LuceneReader {
    private static transient final Logger LOG = LoggerFactory.getLogger(LuceneReader.class);

    private Analyzer chineseAnalyzer;
    private Directory index;
    private int numAnswer;

    public LuceneReader(Analyzer chineseAnalyzer, Directory index, int numAnswer) {
        this.chineseAnalyzer = chineseAnalyzer;
        this.index = index;
        this.numAnswer = numAnswer;
    }

    public Collection<String> getAnswers(String question) throws IOException, ParseException {
        TokenStream ts = chineseAnalyzer.tokenStream("myfield", new StringReader(question));
        HelperFunction.printTokenStream(ts);

        Query q = new QueryParser("Question", chineseAnalyzer)
                .parse(QueryParser.escape(question));

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs docs = searcher.search(q, numAnswer);
        ScoreDoc[] hits = docs.scoreDocs;
        Set<String> answers = new HashSet<>();
        for (ScoreDoc h : hits) {
            answers.add(searcher.doc(h.doc).get("Answer"));
        }
        reader.close();
        return answers;
    }

    public int getNumDocs() throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        return reader.numDocs();
    }
}
