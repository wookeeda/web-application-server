package webserver;

import controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String path = request.getPath();
            Controller controller = RequestMapping.getController(path);
            if (controller == null) {
                path = getDefaultPath(request);
                response.forward(path);
            } else {
                controller.service(request, response);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private String getDefaultPath(HttpRequest request) {
        String path = request.getPath();
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
