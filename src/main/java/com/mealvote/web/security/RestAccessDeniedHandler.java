package com.mealvote.web.security;

import com.mealvote.AuthorizedUser;
import com.mealvote.util.exception.ExceptionInfo;
import com.mealvote.web.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import static com.mealvote.web.json.JsonUtil.writeValue;

public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger LOGG = LoggerFactory.getLogger(RestAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        AuthorizedUser authUser = SecurityUtil.safeGet();
        if (authUser != null) {
            LOGG.warn("{} attempted to access the protected URL: {}", authUser.getUser(), request.getRequestURL());
        }

        ExceptionInfo info = new ExceptionInfo(request.getRequestURL(), "you are not allowed to make this request");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Writer pw = response.getWriter();
        pw.write(writeValue(info));
        pw.flush();
    }
}
