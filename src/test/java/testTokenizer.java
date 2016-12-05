import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.util.HelperFunction;
import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by han on 11/14/16.
 */
public class testTokenizer {

    @Test
    public void testSomeInstances() {
        Analyzer analyzer = new ChineseSynonymAnalyzer(false, false);

        String[] sent = {"你好", "你好么", "我好",
                "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。",
        "我想咨询一下关于德国博士申请的问题？", "我想问一下申请是怎么样个流程啊", "我想问一下博士需要读几年啊?"};

        try {
            for (String s : sent) {
                HelperFunction.printTokenStream(analyzer.tokenStream("myfield", new StringReader(s)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
