package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        log.debug("request line : {}", requestLine);

        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException(requestLine + " 형식이 맞지 않다.");
        }
        method = HttpMethod.valueOf(tokens[0]);
        path = tokens[1];

        if (method.isGet() && path.contains("?")) {
            String[] pathTokens = path.split("\\?");
            path = pathTokens[0];
            params = HttpRequestUtils.parseQueryString(pathTokens[1]);
            return;
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
