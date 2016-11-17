import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.searcher.LuceneIndexer;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */
public class testIndexer {

    @Test
    public void testIndexRealQA() throws IOException {
        Set<QAState> qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        LuceneIndexer luceneIndexer = new LuceneIndexer();
        luceneIndexer.index(qaStates);

        String[] testQueries = {"面试一般要等多久会有结果啊", "你能给我讲讲申请的步骤么", "德国冬天冷么", "奖学金怎么申请啊"};
        try {
            for (String q : testQueries) {
                System.out.println("question:" + q);
                System.out.println("answer:" + luceneIndexer.search(q).toArray()[0]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
