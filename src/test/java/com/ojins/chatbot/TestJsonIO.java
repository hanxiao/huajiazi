package com.ojins.chatbot;

import com.google.common.collect.Sets;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * ___   ___  ________  ___   __      __     __   ________ ________  ______
 * /__/\ /__/\/_______/\/__/\ /__/\   /__/\ /__/\ /_______//_______/\/_____/\
 * \::\ \\  \ \::: _  \ \::\_\\  \ \  \ \::\\:.\ \\__.::._\\::: _  \ \:::_ \ \
 * \::\/_\ .\ \::(_)  \ \:. `-\  \ \  \_\::_\:_\/   \::\ \ \::(_)  \ \:\ \ \ \
 * \:: ___::\ \:: __  \ \:. _    \ \   _\/__\_\_/\ _\::\ \_\:: __  \ \:\ \ \ \
 * \: \ \\::\ \:.\ \  \ \. \`-\  \ \  \ \ \ \::\ /__\::\__/\:.\ \  \ \:\_\ \ \
 * \__\/ \::\/\__\/\__\/\__\/ \__\/   \_\/  \__\\________\/\__\/\__\/\_____\/
 * <p>
 * Created on 11/14/16.
 */

@Slf4j
public class TestJsonIO {

    @Test
    public void testRead() throws IOException {
        val fileName = getClass().getClassLoader().getResource("test-load.json").getPath();
        val qaStates = QAPair.fromJsonFile(fileName);
        Assert.assertEquals(Sets.newHashSet(
                new QAPairBuilder().setQuestion("测试问题1").setAnswer("回答").setHits(0).build()), qaStates);
    }

    @Test
    public void testWrite() throws IOException {

        val qaStates = Sets.newHashSet(
                new QAPairBuilder().setQuestion("测试问题1").setAnswer("回答").build(),
                new QAPairBuilder().setQuestion("测试问题2").setAnswer("回答").build());

        val fileName = getClass().getClassLoader().getResource("test-write-load.json").getPath();
        QAPair.toJsonFile(qaStates, fileName);
        Assert.assertEquals(2, QAPair.fromJsonFile(fileName).size());
    }
}
