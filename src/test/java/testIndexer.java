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

        try {
            luceneIndexer.search("我想问一下申请步骤是什么啊");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
