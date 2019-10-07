package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        ) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            boolean logined = false;
            boolean isCss = false;

            String line = br.readLine();
            log.debug("[request line] : {}", line);
            String[] tokens = line.split(" ");
            String method = tokens[0];
            String url = tokens[1];

            Map<String, String> headers = new HashMap<>();

            while ((line = br.readLine()) != null && !"".equals(line)) {
                log.debug("        headers : {}", line);
                tokens = line.split(": ");
                headers.put(tokens[0], tokens[1]);
            }

            if (headers.containsKey("cookie")) {
                String cookieStr = headers.get("cookie");
                Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieStr);
                if (cookies.containsKey("logined")) {
                    logined = Boolean.parseBoolean(cookies.get("logined"));
                }
            }
            if (headers.containsKey("Accept")) {
                String accept = headers.get("Accept");
                if (accept.contains("text/css")) {
                    isCss = true;
                }
            }

            DataOutputStream dos = new DataOutputStream(out);

            if ("GET".equals(method)) {
                if (isCss) {
                    Path path = new File("./webapp" + url).toPath();
                    byte[] body = Files.readAllBytes(path);
                    response200HeaderWhenCss(dos, body.length);
                    responseBody(dos, body);
                    return;
                }
                if (url.contains("/user/list")) {
                    if (!logined) {
                        response302Header(dos, "/user/login.html");
                        return;
                    }
                    // 목록 출력
                    // 기존문서를 가져온 다음
                    // 지금 있는 유저 목록으로 바꾸기
                    Collection<User> result = DataBase.findAll();
                    List<User> users = new ArrayList<>(result);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < users.size(); i++) {
                        User user = users.get(i);
                        sb.append("<tr><th scope='row'>")
                                .append((i + 1))
                                .append("</th><td>")
                                .append(user.getUserId())
                                .append("</td><td>")
                                .append(user.getName())
                                .append("</td><td>")
                                .append(user.getEmail())
                                .append("</td><td><a href='#' class='btn btn-success' role='button'>수정</a></td></tr>");
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
                    for (String str : allLines)
                        numBytes += str.getBytes().length;
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

                    response200Header(dos, numBytes);
                    responseBody(dos, b2);
                } else {
                    Path path = new File("./webapp" + url).toPath();
                    byte[] body = Files.readAllBytes(path);
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
            } else if ("POST".equals(method)) {
//                br.readLine();
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                String requestBody = IOUtils.readData(br, contentLength);
                Map<String, String> q = HttpRequestUtils.parseQueryString(requestBody);
                if (url.contains("/user/create")) {
                    User user = new User(q.get("userId"), q.get("password"), q.get("name"), q.get("email"));

                    DataBase.addUser(user);
                    response302Header(dos, "/index.html");

                } else if (url.contains("/user/login")) {
                    String userId = q.get("userId");
                    String password = q.get("password");

                    User user = DataBase.findUserById(userId);
                    if (user == null || !user.getPassword().equals(password)) {
                        response302HeaderWithLocationUrlWithLogined(dos, "/user/login_failed.html", "false");
                    }
                    response302HeaderWithLocationUrlWithLogined(dos, "/index.html", "true");
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void response302HeaderWithLocationUrlWithLogined(DataOutputStream dos, String url, String logined) {
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

    private void response200HeaderWhenCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
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
