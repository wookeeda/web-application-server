package controller;

import util.HttpRequest;
import util.HttpResponse;

public abstract class AbstractController implements Controller{


    public void service(HttpRequest req, HttpResponse res) {
    }

    void doGet(HttpRequest req, HttpResponse res) {
    }

    void doPost(HttpRequest req, HttpResponse res){
    }

}
