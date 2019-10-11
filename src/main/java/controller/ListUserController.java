package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

public class ListUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ListUserController.class);

    @Override
    void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        if (!request.isLogined()) {
            response.sendRedirect("/user/login.html");
            return;
        }
        Collection<User> users = DataBase.findAll();
        StringBuilder userList = new StringBuilder();
        int cnt = 1;
        for (User user : users) {
            userList.append("<tr>")
                    .append("<th scope='row'>").append((cnt++)).append("</th>")
                    .append("<td>").append(user.getUserId()).append("</td>")
                    .append("<td>").append(user.getName()).append("</td>")
                    .append("<td>").append(user.getEmail()).append("</td>")
                    .append("<td><a href='#' class='btn btn-success' role='button'>수정</a></td>")
                    .append("</tr>");
        }

        List<String> allLines = Files.readAllLines(new File("./webapp" + path + ".html").toPath());
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            if (line.contains("사용자 아이디")) {
                allLines.add((i + 4), userList.toString());
                break;
            }
        }

        StringBuilder body = new StringBuilder();
        for (String line : allLines) {
            body.append(line);
        }
        response.forwardBody(body.toString());

    }
}
