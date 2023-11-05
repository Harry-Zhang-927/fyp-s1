package com.example.demo.interceptor;

import com.example.demo.context.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MainInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // This code will execute before the controller method is invoked
        String q = request.getHeader("q");
        String a = request.getHeader("a");
        RequestContext.set("q", q);
        RequestContext.set("a", a);
        System.out.println("Pre Handle method is Calling");
        return true;  // if false, the execution chain will stop here
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        // This code will execute after the controller method is invoked and before the view is rendered
        System.out.println("Post Handle method is Calling");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // This code will execute after the entire request is finished
        RequestContext.clear();
        System.out.println("Request and Response is completed");
    }
}
