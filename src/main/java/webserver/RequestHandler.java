package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    public static final String LOGINED = "logined";
    public static final String COOKIE = "Cookie";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();
        ) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            String method = request.getMethod();
            String url = request.getPath();

            if (url.endsWith(".css")) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
                return;
            }

            HttpResponse response = new HttpResponse(out);

            if ("GET".equals(method)) {
                if (url.contains("/user/list")) {
                    boolean logined = false;
                    if (request.getCookie("logined") != null) {
                        logined = Boolean.parseBoolean(request.getCookie(LOGINED));
                    }
                    if (!logined) {
                        response.sendRedirect("/user/login.html");
                        return;
                    }
                    // 목록 출력
                    // 기존문서를 가져온 다음
                    // 지금 있는 유저 목록으로 바꾸기
                    Collection<User> users = DataBase.findAll();
                    StringBuilder sb = new StringBuilder();
                    int cnt = 1;
                    for (User user : users) {
                        sb.append("<tr>")
                                .append("<th scope='row'>").append((cnt++)).append("</th>")
                                .append("<td>").append(user.getUserId()).append("</td>")
                                .append("<td>").append(user.getName()).append("</td>")
                                .append("<td>").append(user.getEmail()).append("</td>")
                                .append("<td><a href='#' class='btn btn-success' role='button'>수정</a></td>")
                                .append("</tr>");
                    }

                    Path path = new File("./webapp" + url).toPath();
                    byte[] body = Files.readAllBytes(path);
                    List<String> allLines = Files.readAllLines(path);
                    for (int i = 0; i < allLines.size(); i++) {
                        String l = allLines.get(i);
                        if (l.contains("사용자 아이디")) {
                            allLines.add((i + 4), sb.toString());
                            break;
                        }
                    }

                    int numBytes = 0;
                    for (String str : allLines) {
                        numBytes += str.getBytes().length;
                    }
                    List<Byte> byteList = new ArrayList<>();
                    for (String str : allLines) {
                        byte[] currentByteArr = str.getBytes();
                        for (byte b : currentByteArr)
                            byteList.add(b);
                    }
                    Byte[] byteArr = byteList.toArray(new Byte[numBytes]);
                    byte[] b2 = new byte[byteArr.length];
                    for (int i = 0; i < byteArr.length; i++) {
                        b2[i] = byteArr[i];
                    }

                    DataOutputStream dos = new DataOutputStream(out);
                    response200Header(dos, numBytes);
                    responseBody(dos, b2);
                } else {
                    response.forward(url);
                }
            } else if ("POST".equals(method)) {
//                br.readLine();
                if (url.contains("/user/create")) {
                    User user = new User(request.getParameter("userId")
                            , request.getParameter("password")
                            , request.getParameter("name")
                            , request.getParameter("email"));
                    DataBase.addUser(user);
                    response.sendRedirect( "/index.html");
                } else if (url.contains("/user/login")) {
                    String userId = request.getParameter("userId");
                    String password = request.getParameter("password");

                    User user = DataBase.findUserById(userId);
                    if (user == null || !user.getPassword().equals(password)) {
                        response.forward("/user/login_failed.html");
                    }
                    response.addHeader("Set-Cookie", "logined=true;path=/");
                    response.sendRedirect("/index.html");
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void response200CssHeader(DataOutputStream dos, int length) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + length + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(DataOutputStream dos, String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response302LoginSuccessHeader(DataOutputStream dos, String url, String logined) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("Set-Cookie: logined=" + logined + ";path=/\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
