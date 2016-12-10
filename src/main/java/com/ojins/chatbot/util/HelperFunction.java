package com.ojins.chatbot.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by han on 11/14/16.
 */

@Slf4j
public class HelperFunction {
    public static Optional<List<String>> getTokenizerResult(String input, Analyzer analyzer) {
        val strings = new ArrayList<String>();

        try (TokenStream ts = analyzer.tokenStream("myfield", new StringReader(input))) {
            CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
            ts.reset(); // Resets this stream to the beginning. (Required)
            while (ts.incrementToken()) {
                strings.add(termAtt.toString());
            }
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } catch (IOException ignored) {
        }

        return strings.isEmpty() ? Optional.empty() : Optional.of(strings);
    }
}
