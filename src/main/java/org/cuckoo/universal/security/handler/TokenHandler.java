package org.cuckoo.universal.security.handler;

import org.cuckoo.universal.security.AuthUser;
import org.cuckoo.universal.security.Authentication;

public interface TokenHandler {

    /**
     * 创建Token
     *
     * @param authUser
     * @return
     */
    String createToken(AuthUser authUser);

    /**
     * 创建refresh_token
     *
     * @param user
     * @return
     */
    String createRefreshToken(AuthUser user);

    /**
     * 校验和解析token
     *
     * <p>
     *     在cookie+token的认证方式中，每次请求后可以更新token，以实现session的过期效果，但更新token是可选的、不是必须的，由项目决定是否更新
     * </p>
     * @param authentication
     * @return
     */
    Authentication verifyAndParseToken(Authentication authentication);

    /**
     * 校验和解析refresh_token
     *
     * @param authentication
     * @return
     */
    Authentication verifyAndParseRefreshToken(Authentication authentication);
}