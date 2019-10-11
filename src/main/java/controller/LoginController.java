package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;

public class LoginController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        User user = DataBase.findUserById(request.getParameter("userId"));

        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                response.addHeader("Set-Cookie", "logined=true;path=/");
                response.sendRedirect("/index.html");
            }
        }
        response.forward("/user/login_failed.html");
    }
}
