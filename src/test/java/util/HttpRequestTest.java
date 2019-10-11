package util;

import org.junit.Test;
import util.HttpRequest;

import java.io.*;

import static junit.framework.TestCase.assertEquals;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("101", request.getParameter("userId"));
    }

    @Test
    public void request_POST() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("102", request.getParameter("userId"));
    }
}
