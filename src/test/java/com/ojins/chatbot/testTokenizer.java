package com.ojins.chatbot;

import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import com.ojins.chatbot.util.HelperFunction;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

/**

 ___   ___  ________  ___   __      __     __   ________ ________  ______
 /__/\ /__/\/_______/\/__/\ /__/\   /__/\ /__/\ /_______//_______/\/_____/\
 \::\ \\  \ \::: _  \ \::\_\\  \ \  \ \::\\:.\ \\__.::._\\::: _  \ \:::_ \ \
 \::\/_\ .\ \::(_)  \ \:. `-\  \ \  \_\::_\:_\/   \::\ \ \::(_)  \ \:\ \ \ \
 \:: ___::\ \:: __  \ \:. _    \ \   _\/__\_\_/\ _\::\ \_\:: __  \ \:\ \ \ \
 \: \ \\::\ \:.\ \  \ \. \`-\  \ \  \ \ \ \::\ /__\::\__/\:.\ \  \ \:\_\ \ \
 \__\/ \::\/\__\/\__\/\__\/ \__\/   \_\/  \__\\________\/\__\/\__\/\_____\/



 * Created on 11/14/16.
 */
@Slf4j
public class testTokenizer {

    @Test
    public void testSomeInstances() {
        Analyzer analyzer = new ChineseSynonymAnalyzer();

        String[] sent = {"你好", "你好么", "我好",
                "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。",
                "我想咨询一下关于德国博士申请的问题？", "我想问一下申请是怎么样个流程啊", "我想问一下博士需要读几年啊?"};

        for (String s : sent) {
            val strings = HelperFunction.getTokenizerResult(s, analyzer);
            if (strings.isPresent()) log.info(String.format("%s", String.join("|", strings.get())));

        }
    }
}
