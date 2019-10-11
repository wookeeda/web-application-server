package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {


    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();


    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        String line = br.readLine();
        if (line == null) {
            return;
        }

        processRequestLine(line);
        setHeaders(br);

        if (method.isPost()) {
            setParamsOfRequestBody(br);
        }

    }

    private void processRequestLine(String line) {
        log.debug("request line : {}", line);
        String[] tokens = line.split(" ");
        method = HttpMethod.valueOf(tokens[0]);
        path = tokens[1];

        // get 요청에 대해서 queryString을 param으로 정리하는 것은 processRequestLine에서 할 일이긴 하군..
        if (method.isGet() && path.contains("?")) {
            String[] pathTokens = path.split("\\?");
            path = pathTokens[0];
            String queryString = pathTokens[1];
            params = HttpRequestUtils.parseQueryString(queryString);
        }
    }

    private void setParamsOfRequestBody(BufferedReader br) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        String requestBody = URLDecoder.decode(IOUtils.readData(br, contentLength), "utf-8");
        params = HttpRequestUtils.parseQueryString(requestBody);
    }

    private void setHeaders(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null && !"".equals(line)) {
            log.debug("headers : {}", line);
            String[] tokens = line.split(": ");
            headers.put(tokens[0].trim(), tokens[1].trim());

            if ("Cookie".equals(tokens[0])) {
                cookies = HttpRequestUtils.parseCookies(tokens[1]);
            }
        }
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public String getParameter(String key) {
        return this.params.get(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public boolean isLogined() {
        if (this.getCookie("logined") != null) {
            return Boolean.parseBoolean(getCookie("logined"));
        }
        return false;
    }
}
