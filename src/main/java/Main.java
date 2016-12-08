import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ojins.chatbot.dialog.QAResult;
import com.ojins.chatbot.response.NewQA;
import com.ojins.chatbot.service.QAService;
import com.ojins.chatbot.service.QAServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Created by han on 11/12/16.
 */
public class Main {

    private static transient final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String CORS_ORIGIN = "*";
    private static final String CORS_METHODS = "GET, POST, OPTIONS, PUT, PATCH, DELETE";
    private static final String CORS_HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Authorization";

    private static Map<String, QAService> qaServiceMap;

    public static void initQAService() {
        // add all exisiting
        Set<String> availableTopics = Sets.newHashSet(QAService.getAvailableTopics());
        availableTopics.add("default");

        qaServiceMap = availableTopics.stream().collect(
                Collectors.toMap(s -> s, s -> new QAServiceBuilder()
                        .setTopic(s)
                        .setOverwrite(false)
                        .createQAService()));
    }

    public static void main(final String[] args) throws IOException {
        Gson gson = new Gson();

        initQAService();
        initWebService();


        get("/info/topic",
                (req, res) -> QAService.getAvailableTopics(), gson::toJson);

        get("/info/:topic/size",
                (req, res) -> qaServiceMap.getOrDefault(req.params(":topic"),
                        qaServiceMap.get("default")).getNumDocs(),
                gson::toJson);

        get("/ask/:topic/:quest",
                (req, res) -> {
                    Optional<QAResult> answer = qaServiceMap.getOrDefault(req.params(":topic"),
                            qaServiceMap.get("default")).getAnswer(req.params(":quest"));
                    if (answer.isPresent()) {
                        res.status(200);
                        return answer.get();
                    }
                    qaServiceMap
                            .getOrDefault(req.params(":topic"), qaServiceMap.get("default"))
                            .addQAPair(req.params(":quest"), "UNSOLVED");
                    res.status(204);
                    return "";
                },
                gson::toJson);

        post("teach/",
                (req, res) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    NewQA creation = mapper.readValue(req.body(), NewQA.class);
                    if (!creation.isValid()) {
                        res.status(400);
                        return "";
                    }
                    qaServiceMap
                            .getOrDefault(creation.getTopic(), qaServiceMap.get("default"))
                            .addQAPair(creation.getQuestion(), creation.getAnswer());
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
        });

    }
}

