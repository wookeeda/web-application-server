package util;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private String id;
    private Map<String, Object> data;

    HttpSession() {
    }

    HttpSession(String id) {
        this.id = id;
        this.data = new HashMap<>();
    }

    String getId() {
        return this.id;
    }

    public void setAttribute(String name, Object value) {
        data.put(name, value);
    }

    public Object getAttribute(String name) {
        return data.get(name);
    }

    public void removeAttribute(String name) {
        data.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(id);
    }
}
