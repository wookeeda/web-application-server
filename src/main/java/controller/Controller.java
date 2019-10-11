package controller;


import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;

public interface Controller {
    void service(HttpRequest req, HttpResponse res) throws IOException;
}
