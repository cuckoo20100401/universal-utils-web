package org.cuckoo.universal.security.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cuckoo.universal.security.Authentication;
import org.cuckoo.universal.security.handler.AuthenticationFailureHandler;
import org.cuckoo.universal.utils.web.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * DefaultAuthenticationFailureHandler
 *
 * <p>
 *     项目配置时可参考这里的代码重写自定义处理器
 * </p>
 */
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(Authentication authentication) {
        try {
            HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
            PrintWriter printWriter = response.getWriter();
            response.setContentType("application/json;charset=utf-8");
            printWriter.write(new ObjectMapper().writeValueAsString(ResponseEntity.failure(authentication.getAuthResult().code(), authentication.getAuthResult().message())));
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}