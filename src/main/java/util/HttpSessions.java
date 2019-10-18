package util;

import java.util.HashMap;
import java.util.Map;

class HttpSessions {

    private static Map<String, HttpSession> sessionMap = new HashMap<>();

    static HttpSession getSession(String id) {
        HttpSession session = sessionMap.get(id);
        if (session == null) {
            session = new HttpSession(id);
            sessionMap.put(session.getId(), session);
        }
        return session;
    }

    static void remove(String id) {
        sessionMap.remove(id);
    }


}
