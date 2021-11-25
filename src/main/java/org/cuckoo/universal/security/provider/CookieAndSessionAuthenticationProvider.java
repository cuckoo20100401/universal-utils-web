package org.cuckoo.universal.security.provider;

import org.cuckoo.universal.security.AuthUser;
import org.cuckoo.universal.security.Authentication;
import org.cuckoo.universal.security.Constants;
import org.cuckoo.universal.utils.ResultEntity;
import org.cuckoo.universal.utils.web.ResponseCode;

import javax.servlet.http.HttpServletRequest;

/**
 * CookieAndSessionAuthenticationProvider
 *
 * <p>
 *     仅需要实现非通用的方法
 * </p>
 */
public class CookieAndSessionAuthenticationProvider extends AuthenticationProvider {

    /**
     * validateAuthc
     *
     * <p>
     *     没登录时和token不存在用的是同一个状态码
     * </p>
     *
     * @param authentication
     * @return
     */
    @Override
    public Authentication validateAuthc(Authentication authentication) {

        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
        if (request.getSession().getAttribute(Constants.AUTH_USER) != null) {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", "ok");
            authentication.setAuthUser((AuthUser)request.getSession().getAttribute(Constants.AUTH_USER));
            authentication.setAuthResult(ResultEntity.builder().success().build());
        } else {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", authentication.getAuthResult().message());
            authentication.setAuthResult(ResultEntity.builder().failure().code(ResponseCode.AUTH_TOKEN_IS_NOT_EXIST).message("Sorry, you have not been login").build());
        }
        return authentication;
    }
}