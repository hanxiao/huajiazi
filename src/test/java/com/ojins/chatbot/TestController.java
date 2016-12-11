package com.ojins.chatbot;

import com.despegar.sparkjava.test.SparkClient;
import com.despegar.sparkjava.test.SparkServer;
import com.ojins.chatbot.controller.QAMain;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import lombok.val;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * ___   ___  ________  ___   __      __     __   ________ ________  ______
 * /__/\ /__/\/_______/\/__/\ /__/\   /__/\ /__/\ /_______//_______/\/_____/\
 * \::\ \\  \ \::: _  \ \::\_\\  \ \  \ \::\\:.\ \\__.::._\\::: _  \ \:::_ \ \
 * \::\/_\ .\ \::(_)  \ \:. `-\  \ \  \_\::_\:_\/   \::\ \ \::(_)  \ \:\ \ \ \
 * \:: ___::\ \:: __  \ \:. _    \ \   _\/__\_\_/\ _\::\ \_\:: __  \ \:\ \ \ \
 * \: \ \\::\ \:.\ \  \ \. \`-\  \ \  \ \ \ \::\ /__\::\__/\:.\ \  \ \:\_\ \ \
 * \__\/ \::\/\__\/\__\/\__\/ \__\/   \_\/  \__\\________\/\__\/\__\/\_____\/
 * <p>
 * <p>
 * <p>
 * Created on 2016/12/11.
 */
public class TestController {
    @ClassRule
    public static SparkServer<TestWebServer> testServer = new SparkServer<>(TestWebServer.class, QAMain.SERVER_PORT);

    @Test
    public void testGetTopic() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient().doMethod("GET", "/topic", null);
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        assertEquals("[\"default\",\"phd\",\"quant\"]", response.body);
    }

    @Test
    public void testGetTopicSize() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient().doMethod("GET", "/quant/size", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        assertEquals("1", response.body);
    }

    @Test
    public void testGetKnownQuestion() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient().doMethod("GET", "/default/你的作者是?", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
    }

    @Test
    public void testGetUnknownQuestion() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient().doMethod("GET", "/default/天上有多少颗星星?", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(202, response.status);

        response = testServer.getClient().doMethod("GET", "/default/size", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        assertEquals("2", response.body);
    }

    @Test
    public void testTeachThenGet() throws Exception {
        val qaPair = new QAPairBuilder()
                .setQuestion("new question")
                .setAnswer("new answer")
                .setTopic("phd")
                .build();

        SparkClient.UrlResponse response = testServer.getClient().doMethod("POST", "/teach",
                qaPair.toJson(), "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(201, response.status);

        response = testServer.getClient().doMethod("GET", "/phd/new%20question",
                null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        val received = QAPair.fromJson(response.body);
        assertEquals(qaPair.getAnswer(), received.getAnswer());
        assertEquals(qaPair.getQuestion(), received.getQuestion());
    }

    public static class TestWebServer implements SparkApplication {
        @Override
        public void init() {
            new QAMain(true);
        }
    }
}
