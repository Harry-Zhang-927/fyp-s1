package com.example.demo.context;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.HashMap;

@Component
public class RequestContext implements HandlerInterceptor {
    private static final ThreadLocal<Map<String, Object>> contextData = ThreadLocal.withInitial(HashMap::new);

    public static Map<String, Object> get() {
        return contextData.get();
    }

    public static void set(String key, Object value) {
        contextData.get().put(key, value);
    }

    public static Object get(String key) {
        return contextData.get().get(key);
    }

    public static void clear() {
        contextData.remove();
    }
}
