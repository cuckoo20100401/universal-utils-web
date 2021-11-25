package org.cuckoo.universal.security.handler.impl;

import com.auth0.jwt.interfaces.Claim;
import org.cuckoo.universal.security.AuthUser;
import org.cuckoo.universal.security.Authentication;
import org.cuckoo.universal.security.TokenUtils;
import org.cuckoo.universal.security.handler.TokenHandler;
import org.cuckoo.universal.utils.ResultEntity;
import org.cuckoo.universal.utils.web.ResponseCode;

import java.util.Map;

/**
 * DefaultTokenHandler
 *
 * <p>
 *     项目配置时可参考这里的代码重写自定义处理器
 * </p>
 */
public class DefaultTokenHandler implements TokenHandler {

    @Override
    public String createToken(AuthUser authUser) {
        return TokenUtils.createToken(authUser);
    }

    @Override
    public String createRefreshToken(AuthUser authUser) {
        return TokenUtils.createRefreshToken(authUser);
    }

    @Override
    public Authentication verifyAndParseToken(Authentication authentication) {

        ResultEntity result = TokenUtils.verify(authentication.getAuthUser().getAccessToken());

        if (result.success()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            AuthUser authUser = authentication.getAuthUser();
            authUser.setId(accessTokenClaims.get("user.id") == null ? null : accessTokenClaims.get("user.id").asString());
            authUser.setUsername(accessTokenClaims.get("user.username") == null ? null : accessTokenClaims.get("user.username").asString());
            authUser.setNickname(accessTokenClaims.get("user.nickname") == null ? null : accessTokenClaims.get("user.nickname").asString());
            authUser.setCellphone(accessTokenClaims.get("user.cellphone") == null ? null : accessTokenClaims.get("user.cellphone").asString());
            authUser.setTelephone(accessTokenClaims.get("user.telephone") == null ? null : accessTokenClaims.get("user.telephone").asString());
            authUser.setEmail(accessTokenClaims.get("user.email") == null ? null : accessTokenClaims.get("user.email").asString());
            authUser.setRoles(accessTokenClaims.get("user.roles").asArray(String.class));
            authUser.setPerms(accessTokenClaims.get("user.perms").asArray(String.class));
            authentication.setAuthResult(ResultEntity.builder().success().build());
        } else {
            if (result.code() == -1) {
                authentication.setAuthResult(ResultEntity.builder().failure().code(ResponseCode.AUTH_TOKEN_IS_NOT_EXIST).message("token is required").build());
            } else if (result.code() == -2) {
                authentication.setAuthResult(ResultEntity.builder().failure().code(ResponseCode.AUTH_TOKEN_IS_EXPIRED).message("token is expired").build());
            } else {
                authentication.setAuthResult(ResultEntity.builder().failure().code(ResponseCode.AUTH_TOKEN_IS_INVALID).message("token is invalid").build());
            }
        }
        return authentication;
    }

    @Override
    public Authentication verifyAndParseRefreshToken(Authentication authentication) {

        ResultEntity result = TokenUtils.verify(authentication.getAuthUser().getRefreshToken());

        if (result.success()) {
            Map<String, Claim> accessTokenClaims = result.getPayload("claims");
            AuthUser authUser = authentication.getAuthUser();
            authUser.setId(accessTokenClaims.get("user.id") == null ? null : accessTokenClaims.get("user.id").asString());
            authUser.setUsername(accessTokenClaims.get("user.username") == null ? null : accessTokenClaims.get("user.username").asString());
            authentication.setAuthResult(ResultEntity.builder().success().build());
        } else {
            if (result.code() == -1) {
                authentication.setAuthResult(ResultEntity.builder().failure().code(ResponseCode.AUTH_TOKEN_IS_NOT_EXIST).message("token is not exist").build());
            } else if (result.code() == -2) {
                authentication.setAuthResult(ResultEntity.builder().failure().code(ResponseCode.AUTH_TOKEN_IS_EXPIRED).message("token is expired").build());
            } else {
                authentication.setAuthResult(ResultEntity.builder().failure().code(ResponseCode.AUTH_TOKEN_IS_INVALID).message("token is invalid").build());
            }
        }
        return authentication;
    }
}