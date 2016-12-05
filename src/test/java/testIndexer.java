import com.ojins.chatbot.dialog.QAState;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.searcher.LuceneIndexer;
import com.ojins.chatbot.searcher.LuceneIndexerBuilder;
import com.ojins.chatbot.searcher.LuceneReader;
import com.ojins.chatbot.searcher.LuceneReaderBuilder;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * Created by han on 11/14/16.
 */
public class testIndexer {
    private LuceneIndexer luceneIndexer;
    private LuceneReader luceneReader;

    public testIndexer() throws FileNotFoundException {
        Set<QAState> qaStates = StateIO.loadStatesFromJson("src/test/statedb-small.json");
        luceneIndexer = new LuceneIndexerBuilder()
                .setFilePath("index")
                .setQAStates(qaStates)
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
    public void testAdding() throws IOException {
        luceneIndexer.addQAState(new QAState("世界上最伟大的国家是哪个国家", "中国"));
        getAnswerForQuestions(luceneReader, "谁是世界上最伟大的国家");
    }

    public static void getAnswerForQuestions(LuceneReader luceneReader, String testQuery) {
        getAnswerForQuestions(luceneReader, new String[]{testQuery});
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
