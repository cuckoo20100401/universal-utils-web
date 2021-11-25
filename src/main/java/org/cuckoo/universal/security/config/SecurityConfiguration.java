package org.cuckoo.universal.security.config;

import org.cuckoo.universal.security.handler.AuthenticationFailureHandler;
import org.cuckoo.universal.security.handler.AuthenticationSuccessHandler;
import org.cuckoo.universal.security.handler.TokenHandler;
import org.cuckoo.universal.security.provider.AuthenticationProvider;

import java.util.Map;

/**
 * SecurityConfiguration
 *
 * <p>
 *     由于在框架的大多数类文件中需要用到配置信息，所以和Authentication一样，基本上也是贯穿认证的始终，但不包括后端业务代码的请求过程
 * </p>
 */
public class SecurityConfiguration {

    private AuthenticationProvider authenticationProvider;
    private Map<String, String> authRules;
    private TokenHandler tokenHandler;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;

    public static SecurityConfigurationBuilder builder() {
        return new SecurityConfigurationBuilder();
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public Map<String, String> getAuthRules() {
        return authRules;
    }

    public void setAuthRules(Map<String, String> authRules) {
        this.authRules = authRules;
    }

    public TokenHandler getTokenHandler() {
        return tokenHandler;
    }

    public void setTokenHandler(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return authenticationSuccessHandler;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }
}