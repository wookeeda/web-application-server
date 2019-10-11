package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

        if (url.endsWith(".css")) {
            addHeader("Content-Type", "text/css");
        } else if (url.endsWith(".js")) {
            addHeader("Content-Type", "application/javascript");
        } else {
            addHeader("Content-Type", "text/html;charset=utf-8");
        }
        addHeader("Content-Length", String.valueOf(body.length));

        response200Header();
        responseBody(body);
    }

    public void forwardBody(String body) throws IOException {
        byte[] contents = body.getBytes();
        addHeader("Content-Type", "text/html;charset=utf-8");
        addHeader("Content-Length", String.valueOf(contents.length));
        response200Header();
        responseBody(contents);
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.writeBytes("\r\n");
        dos.flush();
    }

    private void response200Header() throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        processHeaders();
        dos.writeBytes("\r\n");
    }

    private void processHeaders() throws IOException {
        for (String key : headers.keySet()) {
            dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
        }
    }

    public void sendRedirect(String url) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        processHeaders();
        dos.writeBytes("Location: " + url + "\r\n");
        dos.writeBytes("\r\n");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}
