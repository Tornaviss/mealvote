package com.mealvote.web.security;

import com.mealvote.util.exception.ExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import static com.mealvote.web.json.JsonUtil.writeValue;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGG = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOGG.debug("Unauthenticated access attempt to URL: {}", request.getRequestURL());
        ExceptionInfo info = new ExceptionInfo(request.getRequestURL(), "you are unauthorized to make this request");
        response.setHeader("WWW-Authenticate", "Basic");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Writer writer = response.getWriter();
        writer.write(writeValue(info));
        writer.flush();
    }
}
