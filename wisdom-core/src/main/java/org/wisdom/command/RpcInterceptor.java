package org.wisdom.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RpcInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(RpcInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws RuntimeException {
        String url = request.getRequestURI();
        String token = request.getHeader("token");
        //判断
        if (token != null && token != "") {
            return true;
        } else {
            //throw new RuntimeException("无token，调用失败");
            logger.warn("Illegal connection request from " + request.getRemoteHost() + ":" + request.getRemotePort());
            return false;
        }
    }
}
