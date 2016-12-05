import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.searcher.LuceneIndexer;
import com.ojins.chatbot.searcher.LuceneIndexerBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 12/5/16.
 */
public class testLoader {
    @Test
    public void testLoadFromPreviousIndex() throws IOException {
        LuceneIndexer luceneIndexer = new LuceneIndexerBuilder()
                .setFilePath("index")
                .createLuceneIndexer();

    }
}
