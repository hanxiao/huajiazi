package com.ojins.chatbot.service;

import com.ojins.chatbot.dialog.QAResult;
import com.ojins.chatbot.dialog.QAResultBuilder;
import com.ojins.chatbot.dialog.QAScoreTuple;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

    public Optional<QAResult> getAnswers(String question) throws IOException, ParseException {
        TokenStream ts = chineseAnalyzer.tokenStream("myfield", new StringReader(question));
        HelperFunction.printTokenStream(ts);

        Query q = new QueryParser("Question", chineseAnalyzer)
                .parse(QueryParser.escape(question));

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs docs = searcher.search(q, numAnswer);
        ScoreDoc[] hits = docs.scoreDocs;
        Map<String, QAScoreTuple> answers = new HashMap<>();
        for (ScoreDoc h : hits) {
            String curAnswer = searcher.doc(h.doc).get("Answer");
            String curQuestion = searcher.doc(h.doc).get("Question");
            if (curAnswer.startsWith("UNSOLVED")) { continue; }
            if (answers.containsKey(curAnswer)) {
                answers.get(curAnswer).score += h.score;
                answers.get(curAnswer).hits++;
            } else {
                answers.putIfAbsent(curAnswer, new QAScoreTuple(curQuestion, curAnswer, h.score, 1));
            }
        }
        reader.close();

        if (answers.size() == 0) {
            return Optional.empty();
        }

        answers.values().forEach(p -> {
            p.score = p.score / p.hits;
        });

        Optional<QAScoreTuple> bestAnswer = answers.values().stream().max(Comparator.comparingDouble(QAScoreTuple::getScore));
        answers.remove(bestAnswer.get().answer);
        Stream<String> tmp = answers.values().stream().map(p -> p.question);
        String[] didYouMean = tmp.toArray(String[]::new);

        return Optional.of(new QAResultBuilder()
                .setAnswer(bestAnswer.get().answer)
                .setQuestion(bestAnswer.get().question)
                .setDidYouMean(didYouMean)
                .setScore(bestAnswer.get().score)
                .createQAResult());
    }

    public int getNumDocs() throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        return reader.numDocs();
    }
}
