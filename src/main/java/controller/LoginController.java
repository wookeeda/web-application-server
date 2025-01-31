package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpResponse;
import util.HttpSession;

import java.io.IOException;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    void doPost(HttpRequest request, HttpResponse response) throws IOException {

        User user = DataBase.findUserById(request.getParameter("userId"));

        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("/index.html");
            }
        }
        response.forward("/user/login_failed.html");
    }

}
