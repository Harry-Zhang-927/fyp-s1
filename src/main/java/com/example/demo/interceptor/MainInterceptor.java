package com.example.demo.interceptor;

import com.example.demo.context.RequestContext;
import com.example.demo.model.RequestCtx;
import com.example.demo.utils.SystemContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MainInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession httpSession = request.getSession();
        String p = (String) httpSession.getAttribute("p");
        String a = (String) httpSession.getAttribute("a");

        p = "23";
        a = "5";

        RequestCtx requestCtx = new RequestCtx();
        requestCtx.setA(Integer.valueOf(a));
        requestCtx.setP(Integer.valueOf(p));
        SystemContextUtils.setRequestCtxThreadLocal(requestCtx);
        return true;
    }

}
