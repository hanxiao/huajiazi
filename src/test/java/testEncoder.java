import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.seq2seq.EncodedQASet;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by han on 11/22/16.
 */
public class testEncoder {
    @Test
    public void testEncoding() throws FileNotFoundException{
        Set<QAState> qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        EncodedQASet encodedQASet = new EncodedQASet(qaStates);
        encodedQASet.printSummary();
        System.out.println(Arrays.toString(encodedQASet.getEncodeSentence("德国博士咨询")));
        System.out.println(encodedQASet.getDecodeSentence(new int[] {47, 48, 75}));
    }
}
