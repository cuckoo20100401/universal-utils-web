package org.cuckoo.universal.security.handler;

import org.cuckoo.universal.security.Authentication;

/**
 * LoginSuccessHandler
 *
 * <p>
 *     指客户端登录时的处理器
 * </p>
 */
public interface LoginSuccessHandler {

    void onLoginSuccess(Authentication authentication);
}