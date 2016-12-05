import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.searcher.LuceneIndexer;
import com.ojins.chatbot.searcher.LuceneIndexerBuilder;
import com.ojins.chatbot.searcher.LuceneReader;
import com.ojins.chatbot.searcher.LuceneReaderBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */
public class testIndexer {

    @Test
    public void testIndexRealQA() throws IOException {
        Set<QAState> qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        LuceneIndexer luceneIndexer = new LuceneIndexerBuilder()
                .setFilePath("index")
                .setQAStates(qaStates)
                .createLuceneIndexer();

        LuceneReader luceneReader = new LuceneReaderBuilder()
                .setIndexer(luceneIndexer)
                .createLuceneReader();

        String[] testQueries = {"面试一般要等多久会有结果啊", "你能给我讲讲申请的步骤么", "冬天冷么", "奖学金怎么申请啊"};
        getAnswerForQuestions(luceneReader, testQueries);
    }

    public static void getAnswerForQuestions(LuceneReader luceneReader, String[] testQueries) {
        try {
            for (String q : testQueries) {
                System.out.println("question:" + q);
                luceneReader.getAnswers(q).forEach(p -> {
                    System.out.println("answer:" + p);
                });
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
