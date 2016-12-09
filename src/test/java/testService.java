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
                .setQaStates(qaStates)
                .setTopic("phd")
                .setOverwrite(true)
                .createQAService();

        qaService.addQAPair("这是什么主题的数据库?", "博士申请");

        qaService = new QAServiceBuilder()
                .setQaStates(qaStates)
                .setTopic("quant")
                .setOverwrite(true)
                .createQAService();

        qaService.addQAPair("这是什么主题的数据库?", "量化交易");
    }

    @Test
    public void testSwitchTopic() {
        Assert.assertEquals(qaService.getAnswer("这是什么主题数据库?").get().getAnswer(), "量化交易");
        qaService = QAService.selectTopic("phd").orElse(null);
        if (qaService != null) {
            Assert.assertEquals("博士申请", qaService.getAnswer("这是什么主题数据库?").get().getAnswer());
        }
    }

    @Test
    public void testMultipleQuestions() {
        qaService = QAService.selectTopic("phd").orElse(null);
        System.out.println(qaService.getAnswer(new String[]{"申请步骤", "如何套磁"}));
    }

    @Test
    public void testLoadFromPreviousIndex() throws IOException {
        Assert.assertEquals(181, qaService.getNumDocs());
    }

    @Test
    public void testAddDuplicateTurnOff() throws IOException {
        qaService = new QAServiceBuilder()
                .setTopic("default")
                .setOverwrite(true)
                .createQAService();
        // default service has one default qastate
        qaService.addQAPair("你好我好大家好", "知道了");
        qaService.addQAPair("你好我好大家好", "知道了");
        Assert.assertEquals(3, qaService.getNumDocs());

        qaService.addQAPair("你好我好大家好", "知道了", true);
        Assert.assertEquals(2, qaService.getNumDocs());
    }

    @Test
    public void testAddDuplicateTurnOn() throws IOException {
        qaService = new QAServiceBuilder()
                .setTopic("default")
                .setOverwrite(true)
                .createQAService();
        // default service has one default qastate
        qaService.addQAPair("新中国成立啦", "知道了", true);
        qaService.addQAPair("新中国成立了", "知道了", true);
        qaService.addQAPair("新中国", "知道了", true);
        Assert.assertEquals(2, qaService.getNumDocs());
    }

    @Test
    public void testListingTopics() {
        System.out.println(Arrays.toString(QAService.getAvailableTopics()));
        Assert.assertTrue(QAService.getAvailableTopics().length > 0);
    }
}
