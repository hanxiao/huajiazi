import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.service.QAService;
import com.ojins.chatbot.service.QAServiceBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by han on 12/5/16.
 */
public class testService {
    Set<QAState> qaStates;
    QAService qaService;

    public testService() throws FileNotFoundException {
        qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        qaService = new QAServiceBuilder()
                .setQAStates(qaStates)
                .setTopic("phd")
                .createQAService();

        qaService.addQAPair("这是什么主题的数据库?", "博士申请");

        qaService = new QAServiceBuilder()
                .setQAStates(qaStates)
                .setTopic("quant")
                .createQAService();

        qaService.addQAPair("这是什么主题的数据库?", "量化交易");
    }

    @Test
    public void testLoadFromPreviousIndex() throws IOException {
        Assert.assertEquals(qaService.getNumDocs(), 180);
    }

    @Test
    public void testListingTopics() {
        System.out.println(Arrays.toString(QAService.getAvailableTopics()));
        Assert.assertTrue(QAService.getAvailableTopics().length > 0);
    }
}
