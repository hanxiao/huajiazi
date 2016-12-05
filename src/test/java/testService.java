import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.searcher.LuceneReader;
import com.ojins.chatbot.searcher.LuceneReaderBuilder;
import com.ojins.chatbot.searcher.QAService;
import com.ojins.chatbot.searcher.QAServiceBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 12/5/16.
 */
public class testService {

    @Test
    public void testLoadFromPreviousIndex() throws IOException {
        Set<QAState> qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        QAService qaService = new QAServiceBuilder()
                .setTopic("quant")
                .createQAService();

        Assert.assertEquals(qaService.getNumDocs(), 180);
    }
}
