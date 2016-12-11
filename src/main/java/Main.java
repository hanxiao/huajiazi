import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import com.ojins.chatbot.service.QAService;
import com.ojins.chatbot.service.QAServiceBuilder;
import com.ojins.chatbot.util.CollectionAdapter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

@Slf4j
public class Main {

    private static final String CORS_ORIGIN = "*";
    private static final String CORS_METHODS = "GET, POST, OPTIONS, PUT, PATCH, DELETE";
    private static final String CORS_HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Authorization";
    private static final QAPair fallbackUnknown = new QAPairBuilder()
            .setAnswer("这个问题我现在没法回答……不过我已经记下啦, 过一会儿回答你。")
            .build();
    private static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();
    private static Map<String, QAService> qaServiceMap;

    public static void initQAService() {
        // add all exisiting
        val availableTopics = Sets.newHashSet(QAService.getAvailableTopics());
        availableTopics.add("default");

        qaServiceMap = availableTopics.stream().collect(
                Collectors.toMap(
                        s -> s,
                        s -> new QAServiceBuilder()
                                .setTopic(s)
                                .setOverwrite(false)
                                .createQAService()));
    }

    public static void main(final String[] args) throws IOException {
        Gson gson = new Gson();

        initQAService();
        initWebService();

        get("/topic",
                (req, res) -> QAService.getAvailableTopics(), gson::toJson);

        get("/:topic/size",
                (req, res) -> QAService.selectTopic(qaServiceMap, req.params(":topic")).getNumDocs(),
                gson::toJson);

        get("/:topic/unsolved",
                (req, res) -> {
                    Optional<List<QAPair>> unsolved = QAService.selectTopic(qaServiceMap, req.params(":topic"))
                            .getUnsolved();
                    if (unsolved.isPresent()) {
                        return unsolved.get();
                    }
                    res.status(204);
                    return "";
                },
                gson::toJson);

        get("/:topic/:quest",
                (req, res) -> {
                    Optional<QAPair> answer = QAService.selectTopic(qaServiceMap, req.params(":topic"))
                            .getAnswer(req.params(":quest"));
                    if (answer.isPresent()) {
                        res.status(200);
                        return answer.get();
                    }
                    QAService.selectTopic(qaServiceMap, req.params(":topic"))
                            .addQAPair(req.params(":quest"), "UNSOLVED", true);
                    res.status(202);
                    return fallbackUnknown;
                },
                gson::toJson);

        post("/teach",
                (req, res) -> {
                    QAPair qa = QAPair.buildStateFromJson(req.body());
                    if (!qa.isValid()) {
                        log.warn("invalid QAPair: {}", req.body());
                        res.status(400);
                        return "";
                    }
                    QAService.selectTopic(qaServiceMap, qa.getTopic()).addQAPair(qa);
                    res.status(201);
                    return "";
                },
                gson::toJson);
    }

    private static void initWebService() {
        port(9090); // Spark will run on port 9090
        threadPool(4);
        enableCORS(CORS_ORIGIN, CORS_METHODS, CORS_HEADERS);
    }

    // Enables CORS on requests. This method is an initialization method and should be called once.
    private static void enableCORS(final String origin, final String methods, final String headers) {

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
            //qaServiceMap.values().forEach(QAService::printServiceInfo);
        });

    }
}

