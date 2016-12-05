import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.util.HelperFunction;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by han on 11/12/16.
 */
public class Main {

    private static transient final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws IOException {
        Analyzer analyzer = new ChineseSynonymAnalyzer(true, true);
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("我想咨询一下关于德国博士申请的问题？"));
        HelperFunction.printTokenStream(ts);
    }
}
