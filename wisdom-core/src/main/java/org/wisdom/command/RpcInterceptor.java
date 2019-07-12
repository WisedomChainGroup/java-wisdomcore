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
            if(token.equals("NUMtD0dEXungVX7eLuXkEurH5BCJzw")){
                return true;
            }else{
                return false;
            }
        } else {
//            throw new RuntimeException("无token，调用失败");
            logger.warn("Illegal connection request from " + request.getRemoteHost() + ":" + request.getRemotePort());
            return false;
        }
    }
}