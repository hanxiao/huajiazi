import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.seq2seq.EncodedQASet;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by han on 11/22/16.
 */
public class testEncoder {
    @Test
    public void testEncoding() throws FileNotFoundException {
        Set<QAState> qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        EncodedQASet encodedQASet = new EncodedQASet(qaStates);
        encodedQASet.printSummary();
        System.out.println(Arrays.toString(encodedQASet.getEncodeSentence("德国博士咨询")));
        System.out.println(encodedQASet.getDecodeSentence(new int[]{47, 48, 75}));
    }


    @Test
    public void testSample() {
        Set<QAState> qaStates = new HashSet<>();
        qaStates.add(new QAState(Collections.singletonList("苹果好"), Collections.singletonList("橘子不好")));
        qaStates.add(new QAState(Collections.singletonList("橘子好"), Collections.singletonList("苹果不好")));
        qaStates.add(new QAState(Collections.singletonList("苹果不好"), Collections.singletonList("橘子好")));

        EncodedQASet encodedQASet = new EncodedQASet(qaStates);
        encodedQASet.printSummary();
    }
}
