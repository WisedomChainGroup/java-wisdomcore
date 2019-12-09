/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wisdom.util.JWTUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LocalRpcInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(LocalRpcInterceptor.class);

    @Value("${wisdom.localonly}")
    private boolean localonly;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(localonly){
            String token = request.getHeader("token");
            //判断
            if (token != null && token != "") {
                return JWTUtil.parseJWT(token);
            } else {
//            throw new RuntimeException("无token，调用失败");
                logger.warn("The local connection has been started,Illegal connection request from " + request.getRemoteHost() + ":" + request.getRemotePort());
                return false;
            }
        }
        return true;
    }
}