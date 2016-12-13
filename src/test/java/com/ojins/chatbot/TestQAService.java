package com.ojins.chatbot;

import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.service.QAService;
import com.ojins.chatbot.service.QAServiceBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
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
 * Created on 12/5/16.
 */

@Slf4j
public class TestQAService {
    private static QAService qaService;
    private static String indexDir = "tmp-test-idx/";

    @BeforeClass
    public static void testQAService() throws IOException {
        val fp = TestQAService.class.getClassLoader().getResource("test-load.json").getPath();
        val qaStates = QAPair.fromJsonFile(fp);
        qaService = new QAServiceBuilder()
                .setQaStates(qaStates)
                .setIndexDir(indexDir)
                .setTopic("phd")
                .setOverwrite(true)
                .createQAService();

        qaService.addQAPair("这是什么主题的数据库?", "博士申请");

        qaService = new QAServiceBuilder()
                .setQaStates(qaStates)
                .setIndexDir(indexDir)
                .setTopic("quant")
                .setOverwrite(true)
                .createQAService();

        qaService.addQAPair("这是什么主题的数据库?", "量化交易");
    }

    @Test
    public void testSwitchTopic() {
        qaService = QAService.selectTopic("quant", indexDir).orElse(null);
        Assert.assertEquals(qaService.getAnswer("这是什么主题数据库?").get().getAnswer(), "量化交易");
        qaService = QAService.selectTopic("phd", indexDir).orElse(null);
        if (qaService != null) {
            Assert.assertEquals("博士申请", qaService.getAnswer("这是什么主题数据库?").get().getAnswer());
        }
    }

    @Test
    public void testMultipleQuestions() {
        qaService = QAService.selectTopic("phd", indexDir).orElse(null);
        val answers = qaService.getAnswer(new String[]{"这是什么数据库?", "你的作者?"});
        Assert.assertEquals(2, answers.size());
        log.info(answers.toString());
    }

    @Test
    public void testAddDuplicateTurnOff() throws IOException {
        qaService = new QAServiceBuilder()
                .setIndexDir(indexDir)
                .setTopic("default")
                .setOverwrite(true)
                .createQAService();
        // default service has one default qastate
        qaService.addQAPair("你好我好大家好", "知道了", false);
        qaService.addQAPair("你好我好大家好", "知道了", false);
        Assert.assertEquals(3, qaService.getNumDocs());

        qaService.addQAPair("你好我好大家好", "知道了", true);
        Assert.assertEquals(2, qaService.getNumDocs());
    }

    @Test
    public void testAddDuplicateTurnOn() throws IOException {
        qaService = new QAServiceBuilder()
                .setIndexDir(indexDir)
                .setTopic("default")
                .setOverwrite(true)
                .createQAService();
        // default service has one default qastate
        qaService.addQAPair("新中国成立啦", "知道了", true);
        qaService.addQAPair("新中国成立了", "知道了", true);
        qaService.addQAPair("新中国", "知道了", true);
        Assert.assertEquals(4, qaService.getNumDocs());
    }

    @Test
    public void testListingTopics() {
        val topics = QAService.getAvailableTopics(indexDir);
        Assert.assertTrue(topics.isPresent());
        val topics2 = QAService.getAvailableTopics("123456");
        Assert.assertFalse(topics2.isPresent());
    }

    @AfterClass
    public static void deleteTmpFolder() throws IOException {
        FileUtils.deleteDirectory(new File(indexDir));
        log.info("clear the test directory");
    }
}
