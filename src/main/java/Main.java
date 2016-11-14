import com.ojins.chatbot.searcher.ChineseSynonymAnalyzer;
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
        Analyzer analyzer = new ChineseSynonymAnalyzer(true);
        //IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。
        //我想咨询一下关于德国博士申请的问题？
        //我想问一下申请是怎么样个流程啊
        //我想问一下博士需要读几年啊
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("我想咨询一下关于德国博士申请的问题？"));


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
