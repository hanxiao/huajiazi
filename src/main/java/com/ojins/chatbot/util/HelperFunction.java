package com.ojins.chatbot.util;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by han on 11/14/16.
 */
public class HelperFunction {
    public static void printTokenStream(TokenStream ts) throws IOException {
        CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
        try {
            ts.reset(); // Resets this stream to the beginning. (Required)

            List<String> strings = new LinkedList<String>();
            while (ts.incrementToken()) {
                // Use AttributeSource.reflectAsString(boolean)
                // for token stream debugging.
                strings.add(termAtt.toString());
            }
            System.out.println(String.format("%s", String.join("|", strings)));
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } finally {
            ts.close(); // Release resources associated with this stream.
        }
    }
}
