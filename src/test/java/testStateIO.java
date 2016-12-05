import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */
public class testStateIO {

    @Test
    public void testWrite() {
        Set<QAState> qaStates = new HashSet<>();
        qaStates.add(new QAState(Arrays.asList("q1", "q2"), Arrays.asList("a1", "a2")));

        StateIO.writeStatesToJson(qaStates, "statedb-test.json");
    }

    @Test
    public void testRead() throws IOException {
        Set<QAState> qaStates = StateIO.loadStatesFromJson("statedb-test.json");
        Assert.assertEquals(qaStates.size(), 1);
    }


}
