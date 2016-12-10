import com.google.common.collect.Sets;
import com.ojins.chatbot.dialog.QAPairBuilder;
import com.ojins.chatbot.dialog.StateIO;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by han on 11/14/16.
 */
public class testStateIO {

    @Test
    public void testWrite() throws IOException {
        val fileName = "statedb-test.json";
        val qaStates = Sets.newHashSet(
                new QAPairBuilder().setQuestion("测试问题1").setAnswer("回答").build(),
                new QAPairBuilder().setQuestion("测试问题2").setAnswer("回答").build());

        StateIO.writeStatesToJson(qaStates, fileName);
        Assert.assertEquals(StateIO.loadStatesFromJson(fileName).size(), 2);
    }
}
