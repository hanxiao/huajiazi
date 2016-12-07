import com.ojins.chatbot.service.QAService;
import com.ojins.chatbot.service.QAServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static spark.Spark.*;

/**
 * Created by han on 11/12/16.
 */
public class Main {

    private static transient final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static Map<String, QAService> qaServiceMap;

    public static void init() {
        // add all exisiting
        qaServiceMap = Stream.concat(Arrays.stream(QAService.getAvailableTopics()), Stream.of("default")).collect(
                Collectors.toMap(s -> s, s -> new QAServiceBuilder()
                        .setTopic(s)
                        .setOverwrite(false)
                        .createQAService()));
    }

    public static void main(final String[] args) throws IOException {
        init();
        get("/hello", (req, res) -> "Hello World");

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
    }
}

