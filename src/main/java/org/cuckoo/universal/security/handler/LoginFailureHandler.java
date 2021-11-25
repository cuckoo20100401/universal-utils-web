package org.cuckoo.universal.security.handler;

import org.cuckoo.universal.security.Authentication;

/**
 * LoginFailureHandler
 *
 * <p>
 *     指客户端登录时的处理器
 * </p>
 */
public interface LoginFailureHandler {

    void onLoginFailure(Authentication authentication);
}