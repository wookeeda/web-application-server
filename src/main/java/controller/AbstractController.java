package controller;

import util.HttpMethod;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {


    public void service(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();

        if (method.isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }

    void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }

}
