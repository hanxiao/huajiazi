import com.ojins.chatbot.dialog.QAPair;
import com.ojins.chatbot.dialog.QAPairBuilder;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.seq2seq.EncodedQASet;
import lombok.val;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by han on 11/22/16.
 */
public class testEncoder {
    @Test
    public void testEncoding() throws FileNotFoundException {
        val qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        EncodedQASet encodedQASet = new EncodedQASet(qaStates);
        encodedQASet.printSummary();
        System.out.println(Arrays.toString(encodedQASet.getEncodeSentence("德国博士咨询")));
        System.out.println(encodedQASet.getDecodeSentence(new int[]{47, 48, 75}));
    }


    @Test
    public void testSample() {
        Set<QAPair> qaStates = new HashSet<>();
        qaStates.add(new QAPairBuilder().setQuestion("苹果好").setAnswer("橘子不好").build());
        qaStates.add(new QAPairBuilder().setQuestion("橘子好").setAnswer("苹果不好").build());
        qaStates.add(new QAPairBuilder().setQuestion("苹果不好").setAnswer("橘子好").build());

        EncodedQASet encodedQASet = new EncodedQASet(qaStates);
        encodedQASet.printSummary();
    }
}
