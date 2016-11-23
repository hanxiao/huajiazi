package com.ojins.chatbot.seq2seq;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.searcher.ChineseSynonymAnalyzer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created by han on 11/22/16.
 */
public class EncodedQASet {

    private static transient final Logger LOG = LoggerFactory.getLogger(EncodedQASet.class);

    private BiMap<String, Integer> word2idx = HashBiMap.create();
    private BiMap<Integer, String> idx2word;

    private List<int[]> questions = new ArrayList<>();
    private List<int[]> answers = new ArrayList<>();

    private Analyzer analyzer = new ChineseSynonymAnalyzer(false, false);
    private int wIdx = 1; // 0 is reserved for SPACE/END
    public final int SPACE_OR_END = 0;

    public int maxQuestionLen = 0;
    public int maxAnswerLen = 0;

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

    public EncodedQASet(Collection<QAState> qaStates) {
        // first build dictionary
        qaStates.stream().forEach(p -> {
            try {
                for (String q: p.getQuestions()) {
                    int[] q_idx = EncodeSentence(q, true).stream().mapToInt(i -> i).toArray();
                    maxQuestionLen = maxQuestionLen > q_idx.length ? maxQuestionLen : q_idx.length;
                    for (String a: p.getAnswers()) {
                        int[] a_idx = EncodeSentence(a, true).stream().mapToInt(i -> i).toArray();
                        maxAnswerLen = maxAnswerLen > a_idx.length ? maxAnswerLen : a_idx.length;
                        questions.add(q_idx);
                        answers.add(a_idx);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // build inverse map
        idx2word = word2idx.inverse();
    }

    public int[] getEncodeSentence(String s) {
        try {
            return EncodeSentence(s, false).stream().mapToInt(i -> i).toArray();
        } catch (IOException|NoSuchElementException ex) {
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
                LOG.error(String.format("%d is out of idx2word map!", j));
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
                    wIdx ++;
                }
                if (curIdx >= 0) {
                    encodedSent.add(curIdx);
                } else {
                    LOG.error(String.format("String: %s contains unknown words", s));
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
