package com.ojins.chatbot;

import com.ojins.chatbot.dialog.QAPair;
import com.ojins.chatbot.dialog.QAPairBuilder;
import com.ojins.chatbot.dialog.StateIO;
import com.ojins.chatbot.seq2seq.EncodedQASet;
import lombok.val;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
