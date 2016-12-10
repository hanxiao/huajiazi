import com.google.common.collect.Sets;
import com.ojins.chatbot.dialog.QAPair;
import com.ojins.chatbot.dialog.QAPairBuilder;
import com.ojins.chatbot.dialog.StateIO;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */
public class testStateIO {

    @Test
    public void testWrite() {
        Set<QAPair> qaStates = Sets.newHashSet(new QAPairBuilder().setQuestion("测试问题1").setAnswer("回答").build(),
                new QAPairBuilder().setQuestion("测试问题2").setAnswer("回答").build());

        StateIO.writeStatesToJson(qaStates, "statedb-test.json");
    }

    @Test
    public void testRead() throws IOException {
        Set<QAPair> qaStates = StateIO.loadStatesFromJson("statedb-test.json");
        Assert.assertEquals(qaStates.size(), 1);
    }


}
