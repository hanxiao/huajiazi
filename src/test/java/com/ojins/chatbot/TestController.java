package com.ojins.chatbot;

import com.despegar.sparkjava.test.SparkClient;
import com.despegar.sparkjava.test.SparkServer;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import com.ojins.chatbot.controller.QAControllerBuilder;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import lombok.val;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.lang.reflect.Type;
import java.util.Set;

import static com.ojins.chatbot.controller.RouteMap.ALL_TOPICS;
import static com.ojins.chatbot.controller.RouteMap.TEACH;
import static org.junit.Assert.*;

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
    public static SparkServer<TestWebServer> testServer = new SparkServer<>(TestWebServer.class, 9090);

    @Test
    public void testGetTopic() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient()
                .doMethod("GET", ALL_TOPICS, null);
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        //assertTrue(response.body.contains("\"test0\",\"test1\",\"test2\",\"test3\""));
    }

    @Test
    public void testGetTopicSize() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient()
                .doMethod("GET", "/test0/size", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        assertEquals("1", response.body);
    }

    @Test
    public void testGetKnownQuestion() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient()
                .doMethod("GET", "/test1?query=你的作者是?", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
    }

    @Test
    public void testGeAllQuestion() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient()
                .doMethod("GET", "/test0/list?filter=all", null,
                        "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        Type setType = new TypeToken<Set<QAPair>>() {
        }.getType();
        val result = QAPair.fromJsonArray(response.body);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetUnknownQuestion() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient()
                .doMethod("GET", "/test1?query=天上有多少颗星星?", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(202, response.status);

        response = testServer.getClient()
                .doMethod("GET", "/test1/size", null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        assertEquals("2", response.body);
    }

    @Test
    public void testTeachThenGet() throws Exception {
        val topic = "test3";
        val qaPair = new QAPairBuilder()
                .setQuestion("new question")
                .setAnswer("new answer")
                .setTopic(topic)
                .build();

        SparkClient.UrlResponse response = testServer.getClient().doMethod("POST", TEACH,
                qaPair.toJson(), "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(201, response.status);

        response = testServer.getClient()
                .doMethod("GET", "/" + topic + "?query=new%20question",
                        null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);
        val received = QAPair.fromJson(response.body);
        assertEquals(qaPair.getAnswer(), received.getAnswer());
        assertEquals(qaPair.getQuestion(), received.getQuestion());
    }

    @Test
    public void testSumOverCheck() throws Exception {
        val topic = "test2";
        SparkClient.UrlResponse response = testServer.getClient()
                .doMethod("GET", "/" + topic + "?filter=unsolved",
                        null, "application/json");
        assertNotNull(testServer.getApplication());
        int unsolved = response.status == 200 ? QAPair.fromJsonArray(response.body).size() : 0;

        response = testServer.getClient()
                .doMethod("GET", "/" + topic + "?filter=solved",
                        null, "application/json");
        int solved = response.status == 200 ? QAPair.fromJsonArray(response.body).size() : 0;

        response = testServer.getClient()
                .doMethod("GET", "/" + topic + "?filter=all",
                        null, "application/json");
        int all = response.status == 200 ? QAPair.fromJsonArray(response.body).size() : 0;

        assertTrue(all == solved + unsolved);
    }

    @Test
    public void testUnsolved() throws Exception {
        // by default there is no unsolved question
        val topic = "test2";
        SparkClient.UrlResponse response = testServer.getClient()
                .doMethod("GET", "/" + topic + "/list?filter=unsolved",
                        null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(204, response.status);

        // ask a new question
        response = testServer.getClient()
                .doMethod("GET", "/" + topic + "?query=new%20question",
                        null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(202, response.status);

        // now we should be able to get one unsolved question
        response = testServer.getClient()
                .doMethod("GET", "/" + topic + "/list?filter=unsolved",
                        null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(200, response.status);

        val qaPair = new QAPairBuilder()
                .setQuestion("new question")
                .setAnswer("new answer")
                .setTopic(topic)
                .build();

        response = testServer.getClient().doMethod("POST", TEACH,
                qaPair.toJson(), "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(201, response.status);

        // after teach, there is no unsolved question
        response = testServer.getClient()
                .doMethod("GET", "/" + topic + "/list?filter=unsolved",
                        null, "application/json");
        assertNotNull(testServer.getApplication());
        assertEquals(204, response.status);

    }

    public static class TestWebServer implements SparkApplication {
        @Override
        public void init() {
            new QAControllerBuilder()
                    .setNewTopics(Sets.newHashSet("test0", "test1", "test2", "test3"))
                    .setIndexDir("tmp-test-idx/")
                    .build();
        }
    }
}
