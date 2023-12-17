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
public class ConnectionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession httpSession = request.getSession();
        String p = request.getParameter("p");
        String a = request.getParameter("a");
        httpSession.setAttribute("p", p);
        httpSession.setAttribute("a", a);

        RequestCtx requestCtx = new RequestCtx();
        requestCtx.setA(Integer.valueOf(a));
        requestCtx.setP(Integer.valueOf(p));
        SystemContextUtils.setRequestCtxThreadLocal(requestCtx);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        // This code will execute after the controller method is invoked and before the view is rendered
//        System.out.println("Post Handle method is Calling");
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("hh", "aa");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // This code will execute after the entire request is finished
        RequestContext.clear();
        System.out.println("Request and Response is completed");
    }
}
