import com.ojins.chatbot.dialog.QAPair;
import com.ojins.chatbot.dialog.QAPairBuilder;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.service.LuceneIndexer;
import com.ojins.chatbot.service.LuceneIndexerBuilder;
import com.ojins.chatbot.service.LuceneReader;
import com.ojins.chatbot.service.LuceneReaderBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */
public class testIndexer {
    private LuceneIndexer luceneIndexer;
    private LuceneReader luceneReader;

    public testIndexer() throws IOException {
        Set<QAPair> qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        luceneIndexer = new LuceneIndexerBuilder()
                .setFilePath("index")
                .setQaStates(qaStates)
                .createLuceneIndexer();

        luceneReader = new LuceneReaderBuilder()
                .setIndexer(luceneIndexer)
                .createLuceneReader();
    }

    @Test
    public void testIndexRealQA() throws IOException {
        String[] testQueries = {"面试一般要等多久会有结果啊", "你能给我讲讲申请的步骤么", "冬天冷么", "奖学金怎么申请啊"};
        getAnswerForQuestions(luceneReader, testQueries);
    }

    @Test
    public void testAdding() {
        luceneIndexer.addQAState(new QAPairBuilder().setQuestion("世界上最伟大的国家是哪个国家").setAnswer("中国").build());
        getAnswerForQuestions(luceneReader, "谁是世界上最伟大的国家");
    }

    @Test
    public void testUpdating() throws IOException {

    }

    public static void getAnswerForQuestions(LuceneReader luceneReader, String testQuery) {
        getAnswerForQuestions(luceneReader, new String[]{testQuery});
    }

    public static void getAnswerForQuestions(LuceneReader luceneReader, String[] testQueries) {
        try {
            for (String q : testQueries) {
                Optional<QAPair> tmp = luceneReader.getAnswers(q);
                if (tmp.isPresent()) {
                    System.out.println(tmp.get().toString());
                } else {
                    System.out.println(String.format("question: %s has no answer!", q));
                }
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
