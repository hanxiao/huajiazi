package com.ojins.chatbot;

import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import com.ojins.chatbot.seq2seq.EncodedQASet;
import com.ojins.chatbot.util.StateIO;
import lombok.val;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**

 ___   ___  ________  ___   __      __     __   ________ ________  ______
 /__/\ /__/\/_______/\/__/\ /__/\   /__/\ /__/\ /_______//_______/\/_____/\
 \::\ \\  \ \::: _  \ \::\_\\  \ \  \ \::\\:.\ \\__.::._\\::: _  \ \:::_ \ \
 \::\/_\ .\ \::(_)  \ \:. `-\  \ \  \_\::_\:_\/   \::\ \ \::(_)  \ \:\ \ \ \
 \:: ___::\ \:: __  \ \:. _    \ \   _\/__\_\_/\ _\::\ \_\:: __  \ \:\ \ \ \
 \: \ \\::\ \:.\ \  \ \. \`-\  \ \  \ \ \ \::\ /__\::\__/\:.\ \  \ \:\_\ \ \
 \__\/ \::\/\__\/\__\/\__\/ \__\/   \_\/  \__\\________\/\__\/\__\/\_____\/



 * Created on 11/22/16.
 */
public class testEncoder {
    @Test
    public void testEncoding() throws IOException {
        val fp = getClass().getClassLoader().getResource("test-load.json").getPath();
        val qaStates = StateIO.loadStatesFromJson(fp);
        EncodedQASet encodedQASet = new EncodedQASet(qaStates);
        encodedQASet.printSummary();
        System.out.println(Arrays.toString(encodedQASet.getEncodeSentence("问题测试")));
        System.out.println(encodedQASet.getDecodeSentence(new int[]{2, 1}));
    }


    @Test
    public void testSample() {
        val qaStates = new HashSet<QAPair>();
        qaStates.add(new QAPairBuilder().setQuestion("苹果好").setAnswer("橘子不好").build());
        qaStates.add(new QAPairBuilder().setQuestion("橘子好").setAnswer("苹果不好").build());
        qaStates.add(new QAPairBuilder().setQuestion("苹果不好").setAnswer("橘子好").build());

        EncodedQASet encodedQASet = new EncodedQASet(qaStates);
        encodedQASet.printSummary();
    }
}
