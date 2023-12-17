package com.example.demo.utils;

import com.example.demo.model.RequestCtx;

public class SystemContextUtils {
    static ThreadLocal<RequestCtx> requestCtxThreadLocal = new ThreadLocal<>();

    public static void setRequestCtxThreadLocal(RequestCtx requestCtx) {
        requestCtxThreadLocal.remove();
        requestCtxThreadLocal.set(requestCtx);
    }

    public static RequestCtx getRequestCtxThreadLocal() {
        return requestCtxThreadLocal.get();
    }


}
