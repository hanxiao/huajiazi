import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by han on 11/12/16.
 */
public class Main {

    private static transient final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws IOException {
        Analyzer analyzer = new SmartChineseAnalyzer();

        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("我想咨询一下德国博士的申请步骤"));


        CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
        try {
            ts.reset(); // Resets this stream to the beginning. (Required)

            List<String> strings = new LinkedList<String>();
            while (ts.incrementToken()) {
                // Use AttributeSource.reflectAsString(boolean)
                // for token stream debugging.
                strings.add(termAtt.toString());
            }
            if (strings.size() > 1) {
                System.out.println(String.format("%s", String.join("|", strings)));
            }
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } finally {
            ts.close(); // Release resources associated with this stream.
        }
    }
}
