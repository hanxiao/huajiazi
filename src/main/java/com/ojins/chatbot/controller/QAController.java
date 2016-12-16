package com.ojins.chatbot.controller;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import com.ojins.chatbot.service.QAService;
import com.ojins.chatbot.service.QAServiceBuilder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.ojins.chatbot.controller.RouteMap.*;
import static spark.Spark.*;

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
 * Created on 11/12/16.
 */

@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QAController {

    static Gson gson = new Gson();
    static String CORS_ORIGIN = "*";
    static String CORS_METHODS = "GET, POST, OPTIONS, PUT, PATCH, DELETE";
    static String CORS_HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Authorization";
    static QAPair fallbackUnknown = new QAPairBuilder()
            .setAnswer("这个问题我现在没法回答……不过我已经记下啦, 过一会儿回答你。")
            .build();

    String indexDir;
    @NonFinal
    static Map<String, QAService> qaServiceMap;

    QAController(Set<String> newTopics, boolean overwrite, int serverPort, int numThread, String dir) {
        indexDir = dir;
        initQAService(dir, newTopics, overwrite);
        initWebService(serverPort, numThread);
        initRouter();
    }

    public static void main(final String[] args) throws IOException {
        new QAControllerBuilder().setNewTopics(Sets.newHashSet("phd", "quant")).build();
    }

    private void initRouter() {

        get(ALL_TOPICS,
                (req, res) -> {
                    Optional<String[]> result = QAService.getAvailableTopics(indexDir);
                    if (result.isPresent()) {
                        return result.get();
                    }
                    res.status(204);
                    return "";
                }, gson::toJson);
        get(TOPIC_SIZE,
                (req, res) -> QAService.selectTopic(qaServiceMap, req.params(TOPIC_PARAM)).getNumDocs(),
                gson::toJson);

        get(QA_LIST,
                (req, res) -> {
                    val topic = req.params(TOPIC_PARAM);
                    val filter = Optional.ofNullable(req.queryParams(FILTER_PARAM));
                    Optional<List<QAPair>> allQA;
                    if (filter.isPresent()) {
                        val qaService = QAService.selectTopic(qaServiceMap, topic);
                        switch (filter.get()) {
                            case FILTER_SOLVED:
                                allQA = qaService.getFiltered(QAService.FilterCondition.SOLVED);
                                break;
                            case FILTER_UNSOLVED:
                                allQA = qaService.getFiltered(QAService.FilterCondition.UNSOLVED);
                                break;
                            default:
                                allQA = qaService.getFiltered(QAService.FilterCondition.ALL);
                                break;
                        }
                        if (allQA.isPresent()) {
                            return allQA.get();
                        }
                        res.status(204);
                        return "";
                    }
                    res.status(400);
                    return "";
                },
                gson::toJson);
        get(RouteMap.ASK,
                (req, res) -> {
                    val topic = req.params(TOPIC_PARAM);
                    val quest = Optional.ofNullable(req.queryParams(QUEST_PARAM));

                    if (quest.isPresent()) {
                        val qaService = QAService.selectTopic(qaServiceMap, topic);

                        Optional<QAPair> answer = qaService.getAnswer(quest.get());
                        if (answer.isPresent()) {
                            res.status(200);
                            return answer.get();
                        }
                        QAService.selectTopic(qaServiceMap, topic)
                                .addQAPair(quest.get(), QAService.UNSOLVED_MARKER, true);
                        res.status(202);
                        return fallbackUnknown;
                    }
                    res.status(400);
                    return "";
                },
                gson::toJson);

        post(TEACH,
                (req, res) -> {
                    QAPair qa = QAPair.fromJson(req.body());
                    if (!qa.isValid()) {
                        log.warn("invalid QAPair: {}", req.body());
                        res.status(400);
                        return "";
                    }
                    QAService.selectTopic(qaServiceMap, qa.getTopic()).addQAPair(qa, true);
                    res.status(201);
                    return "";
                },
                gson::toJson);
    }

    private void initQAService(String indexDir, Set<String> newTopics, boolean overwrite) {
        // add all exisiting
        val existTopics = QAService.getAvailableTopics(indexDir);
        val availableTopics = newTopics == null ? new HashSet<String>() : new HashSet<String>(newTopics);
        existTopics.ifPresent(strings -> availableTopics.addAll(Sets.newHashSet(strings)));
        availableTopics.add("default");

        qaServiceMap = availableTopics.stream().collect(
                Collectors.toMap(
                        s -> s,
                        s -> new QAServiceBuilder()
                                .setIndexDir(indexDir)
                                .setTopic(s)
                                .setOverwrite(overwrite)
                                .createQAService()));
    }

    private void initWebService(int serverPort, int numThread) {
        port(serverPort); // Spark will run on port 9090
        threadPool(numThread);
        enableCORS(CORS_ORIGIN, CORS_METHODS, CORS_HEADERS);
    }

    // Enables CORS on requests. This method is an initialization method and should be called once.
    private void enableCORS(final String origin, final String methods, final String headers) {

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            // Note: this may or may not be necessary in your particular application
            response.type("application/json");
        });

        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
            qaServiceMap.values().forEach(QAService::printServiceInfo);
        });

    }
}

