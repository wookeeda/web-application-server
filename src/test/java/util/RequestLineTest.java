package util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestLineTest {

    @Test
    public void getMethod() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");

        assertEquals(HttpMethod.GET, line.getMethod());
        assertEquals("/index.html", line.getPath());

        line = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals(HttpMethod.POST, line.getMethod());
        assertEquals("/index.html", line.getPath());
    }

    @Test
    public void getPath() {
        RequestLine line = new RequestLine("GET /user/create?userId=101&password=101 HTTP/1.1");
        assertEquals(HttpMethod.GET, line.getMethod());
        assertEquals("/user/create", line.getPath());
        Map<String, String> params = line.getParams();

        Map<String, String> expect = new HashMap<>();
        expect.put("userId", "101");
        expect.put("password", "101");
        assertEquals(expect, params);
    }

    @Test
    public void getParams() {
    }
}