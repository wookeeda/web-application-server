package util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class HttpRequest {

    private String method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Map<String, String> cookies;


    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        // method 1. line 읽기 -> this.method, this.path 설정해줌
        String line = br.readLine();
        String[] tokens = line.split(" ");
        method = tokens[0];
        path = tokens[1];

        params = setParams(br);
        headers = setHeaders(br);
        cookies = setCookie();
    }

    private Map<String, String> setParams(BufferedReader br) throws IOException {
        if ("GET".equals(method) && path.contains("?")) {
            String queryString = path.split("\\?")[1];
            return HttpRequestUtils.parseQueryString(queryString);
        } else if ("POST".equals(method)) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            String requestBody = IOUtils.readData(br, contentLength);
            return HttpRequestUtils.parseQueryString(requestBody);
        }
        return null;
    }

    private Map<String, String> setHeaders(BufferedReader br) throws IOException {
        String line;
        String[] tokens;
        Map<String, String> result = new HashMap<>();
        while ((line = br.readLine()) != null && !"".equals(line)) {
            tokens = line.split(": ");
            result.put(tokens[0], tokens[1]);
        }
        return result;
    }

    public Map<String, String> setCookie() {
        String cookieString = headers.get("Cookie");
        if ("".equals(cookieString) || cookieString == null) {
            return null;
        }
        return HttpRequestUtils.parseCookies(cookieString);
    }

    public String getMethod() {
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
}
