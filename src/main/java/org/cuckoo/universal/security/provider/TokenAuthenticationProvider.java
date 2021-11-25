package org.cuckoo.universal.security.provider;

import org.cuckoo.universal.security.AuthUser;
import org.cuckoo.universal.security.Authentication;
import org.cuckoo.universal.security.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * TokenAuthenticationProvider
 *
 * <p>
 *     仅需要实现非通用的方法
 * </p>
 */
public class TokenAuthenticationProvider extends AuthenticationProvider {

    @Override
    public Authentication validateAuthc(Authentication authentication) {

        //* obtain token from header
        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
        AuthUser authUser = new AuthUser();
        authUser.setAccessToken(request.getHeader(Constants.ACCESS_TOKEN));
        authentication.setAuthUser(authUser);

        //* verify and parse token, or update token by method verifyToken. update token is optional
        authentication = securityConfiguration.getTokenHandler().verifyAndParseToken(authentication);
        if (authentication.getAuthResult().success()) {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", "ok");
        } else {
            authentication.getRuntimeInstance().getLogInfo().put("auth-authc", authentication.getAuthResult().message());
        }
        return authentication;
    }
}