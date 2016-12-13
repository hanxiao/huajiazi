package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.AnalyzerManager;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by han on 12/5/16.
 */

@Slf4j
@AllArgsConstructor
public class LuceneReader {

    private Analyzer chineseAnalyzer;
    private Directory index;
    private int numAnswer;

    Optional<List<QAPair>> getUnsolved() throws IOException, ParseException {
        TermQuery term1 = new TermQuery(new Term("Answer", QAService.UNSOLVED_MARKER));
        Query q = new BooleanQuery.Builder()
                .add(term1, BooleanClause.Occur.MUST)
                .build();

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, 10000);
        ScoreDoc[] hits = docs.scoreDocs;
        Map<String, QAPair> answers = new HashMap<>();
        for (ScoreDoc h : hits) {
            String curAnswer = searcher.doc(h.doc).get("Answer");
            String curQuestion = searcher.doc(h.doc).get("Question");

            answers.putIfAbsent(curQuestion,
                    new QAPairBuilder()
                            .setQuestion(curQuestion)
                            .setAnswer(curAnswer)
                            .setScore(h.score)
                            .setHits(1)
                            .build());
        }
        reader.close();

        if (answers.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(answers.values().stream().collect(Collectors.toList()));

    }

    Optional<QAPair> getAnswers(String question) throws IOException, ParseException {
        AnalyzerManager.getTokenizerResult(question, chineseAnalyzer);

        Query q = new QueryParser("Question", chineseAnalyzer)
                .parse(QueryParser.escape(question));

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs docs = searcher.search(q, numAnswer);
        ScoreDoc[] hits = docs.scoreDocs;
        Map<String, QAPair> answers = new HashMap<>();
        for (ScoreDoc h : hits) {
            String curAnswer = searcher.doc(h.doc).get("Answer");
            String curQuestion = searcher.doc(h.doc).get("Question");
            if (curAnswer.startsWith(QAService.UNSOLVED_MARKER)) {
                continue;
            }
            if (answers.containsKey(curAnswer)) {
                answers.get(curAnswer).incrementScore(h.score);
                answers.get(curAnswer).incrementHitsByOne();
            } else {
                answers.putIfAbsent(curAnswer,
                        new QAPairBuilder()
                                .setQuestion(curQuestion)
                                .setAnswer(curAnswer)
                                .setScore(h.score).build());
            }
        }
        reader.close();

        if (answers.size() == 0) {
            return Optional.empty();
        }

        answers.values().forEach(p -> p.setScore(p.getScore() / p.getHits()));

        Optional<QAPair> bestAnswer = answers.values().stream().max(Comparator.comparingDouble(QAPair::getScore));
        Set<String> didYouMean = answers.values().stream().map(QAPair::getQuestion).collect(Collectors.toSet());
        String bestAns = bestAnswer.get().getAnswer();
        String bestQus = bestAnswer.get().getQuestion();
        didYouMean.remove(bestQus);
        if (!didYouMean.isEmpty()) answers.get(bestAns).setDidYouMean(didYouMean);

        return Optional.of(answers.get(bestAns));
    }

    int getNumDocs() throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        return reader.numDocs();
    }
}
