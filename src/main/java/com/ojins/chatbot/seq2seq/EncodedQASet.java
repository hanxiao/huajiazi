package com.ojins.chatbot.seq2seq;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.ojins.chatbot.analyzer.AnalyzerManager;
import com.ojins.chatbot.model.QAPair;
import lombok.Data;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by han on 11/22/16.
 */

@Data
public class EncodedQASet {

    static transient final Logger LOG = LoggerFactory.getLogger(EncodedQASet.class);
    final int SPACE_OR_END = 0;
    int maxQuestionLen = 0;
    int maxAnswerLen = 0;
    BiMap<String, Integer> word2idx = HashBiMap.create();
    BiMap<Integer, String> idx2word;
    List<int[]> questions = new ArrayList<>();
    List<int[]> answers = new ArrayList<>();
    Analyzer analyzer = AnalyzerManager.chinesePlainAnalyzer;
    int wIdx = 1; // 0 is reserved for SPACE/END

    public EncodedQASet(Collection<QAPair> qaStates) {
        // first build dictionary
        qaStates.forEach(p -> {
            try {
                int[] q_idx = EncodeSentence(p.getQuestion(), true).stream().mapToInt(i -> i).toArray();
                maxQuestionLen = maxQuestionLen > q_idx.length ? maxQuestionLen : q_idx.length;
                int[] a_idx = EncodeSentence(p.getAnswer(), true).stream().mapToInt(i -> i).toArray();
                maxAnswerLen = maxAnswerLen > a_idx.length ? maxAnswerLen : a_idx.length;
                questions.add(q_idx);
                answers.add(a_idx);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // build inverse map
        idx2word = word2idx.inverse();
    }

    public int size() {
        return questions.size();
    }

    public int[] getAQuestion(int idx) {
        return questions.get(idx);
    }

    public int[] getAnAnswer(int idx) {
        return answers.get(idx);
    }

    public int getVocabularySize() {
        return wIdx;
    }

    public void printSummary() {
        System.out.println(String.format("vocabulary size:\t%10d\n" +
                        "max question len:\t%10d\n" +
                        "max answer len:\t\t%10d\n" +
                        "num questions:\t\t%10d\n" +
                        "num answers:\t\t%10d\n", wIdx,
                maxQuestionLen, maxAnswerLen,
                questions.size(),
                answers.size()));
    }

    public int[] getEncodeSentence(String s) {
        try {
            return EncodeSentence(s, false).stream().mapToInt(i -> i).toArray();
        } catch (IOException | NoSuchElementException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getDecodeSentence(int[] s) {
        String sent = "";
        for (int j : s) {
            if (idx2word.containsKey(j)) {
                sent += idx2word.get(j) + " ";
            } else {
                LOG.error("{} is out of idx2word map!", j);
            }
        }
        return sent.trim();
    }

    private List<Integer> EncodeSentence(String s, boolean add2Dict) throws IOException, NoSuchElementException {
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader(s));
        CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
        List<Integer> encodedSent = new ArrayList<>();
        try {
            ts.reset(); // Resets this stream to the beginning. (Required)

            while (ts.incrementToken()) {
                String curWord = termAtt.toString();
                int curIdx = word2idx.getOrDefault(curWord, -1);
                if (add2Dict && !word2idx.containsKey(curWord)) {
                    word2idx.put(termAtt.toString(), wIdx);
                    curIdx = wIdx;
                    wIdx++;
                }
                if (curIdx >= 0) {
                    encodedSent.add(curIdx);
                } else {
                    LOG.error("String: {} contains unknown words", s);
                    throw new NoSuchElementException();
                }
            }
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } finally {
            ts.close(); // Release resources associated with this stream.
        }
        return encodedSent;
    }
}
