package controller;

import util.HttpRequest;
import util.HttpResponse;

public interface Controller {
    void service(HttpRequest req, HttpResponse res);
}
