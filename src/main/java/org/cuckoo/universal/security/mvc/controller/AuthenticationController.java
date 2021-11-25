package org.cuckoo.universal.security.mvc.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cuckoo.universal.security.AuthUser;
import org.cuckoo.universal.security.Authentication;
import org.cuckoo.universal.security.Constants;
import org.cuckoo.universal.security.config.SecurityConfiguration;
import org.cuckoo.universal.security.mvc.service.AuthenticationService;
import org.cuckoo.universal.utils.StringUtils;
import org.cuckoo.universal.utils.web.ResponseCode;
import org.cuckoo.universal.utils.web.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AuthenticationController
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthenticationController {

	private static final Logger logger = LogManager.getLogger(AuthenticationController.class);
	
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private SecurityConfiguration securityConfiguration;

	/**
	 * login
	 *
	 * <p>
	 *     会同时返回token和refresh_token，若客户端对refresh_token没需求可以不关注，也不影响什么
	 * </p>
	 * @param username
	 * @param password
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity login(HttpServletRequest request, String username, String password) {

		if (StringUtils.isNullOrEmpty(username)) {
			return ResponseEntity.failure(ResponseCode.LOGIN_USERNAME_IS_EMPTY, "username cannot be empty");
		}
		if (StringUtils.isNullOrEmpty(password)) {
			return ResponseEntity.failure(ResponseCode.LOGIN_PASSWORD_IS_EMPTY, "password cannot be empty");
		}
		
		AuthUser authUser = authenticationService.findAuthUserByUsername(username);
		if (authUser == null) {
			return ResponseEntity.failure(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST, "the account does not exist");
		}
		if (!authUser.getPassword().equals(StringUtils.encryptByAES(password))) {
			return ResponseEntity.failure(ResponseCode.LOGIN_PASSWORD_IS_INCORRECT, "the password is incorrect");
		}
		if (authUser.getStatus() == -1) {
			return ResponseEntity.failure(ResponseCode.LOGIN_USERNAME_IS_DISABLED, "the account is disabled");
		}

		authUser.setRoles(authenticationService.findAuthUserRolesById(authUser.getId()));
		authUser.setPerms(authenticationService.findAuthUserPermsById(authUser.getId()));

		Map<String, Object> authUserX = new LinkedHashMap<>();
		authUserX.put("id", authUser.getId());
		authUserX.put("username", authUser.getUsername());
		authUserX.put("nickname", authUser.getNickname());
		authUserX.put("roles", authUser.getRoles());

		String authenticationProviderName = securityConfiguration.getAuthenticationProvider().getClass().getSimpleName();
		if (authenticationProviderName.equals("CookieAndSessionAuthenticationProvider")) {
			request.getSession().setAttribute(Constants.AUTH_USER, authUser);
			return ResponseEntity.success().addPayload("auth_user", authUserX);
		} else if (authenticationProviderName.equals("CookieAndTokenAuthenticationProvider") || authenticationProviderName.equals("TokenAuthenticationProvider")) {
			authUserX.put("access_token", securityConfiguration.getTokenHandler().createToken(authUser));
			authUserX.put("refresh_token", securityConfiguration.getTokenHandler().createRefreshToken(authUser));
			return ResponseEntity.success().addPayload("auth_user", authUserX);
		} else {
			logger.error("configuration error, there is no such authentication provider["+authenticationProviderName+"]");
			return ResponseEntity.failure("login failed");
		}
	}

	/**
	 * refreshToken
	 *
	 * @param refreshToken
	 * @return
	 */
	@PostMapping("/refreshToken")
	public ResponseEntity refreshToken(String refreshToken) {

		AuthUser authUser = new AuthUser();
		authUser.setRefreshToken(refreshToken);
		Authentication authentication = new Authentication();
		authentication.setAuthUser(authUser);

		authentication = securityConfiguration.getTokenHandler().verifyAndParseRefreshToken(authentication);

		if (authentication.getAuthResult().success()) {

			authUser = authenticationService.findAuthUserByUsername(authentication.getAuthUser().getUsername());
			if (authUser == null) {
				return ResponseEntity.failure(ResponseCode.LOGIN_USERNAME_IS_NOT_EXIST, "该帐号不存在");
			}
			if (authUser.getStatus() == -1) {
				return ResponseEntity.failure(ResponseCode.LOGIN_USERNAME_IS_DISABLED, "该帐号不可用");
			}

			authUser.setRoles(authenticationService.findAuthUserRolesById(authUser.getId()));
			authUser.setPerms(authenticationService.findAuthUserPermsById(authUser.getId()));
			return ResponseEntity.success().addPayload("token", securityConfiguration.getTokenHandler().createToken(authUser)).addPayload("refresh_token", securityConfiguration.getTokenHandler().createRefreshToken(authUser));
		}
		return ResponseEntity.failure(authentication.getAuthResult().code(), authentication.getAuthResult().message());
	}
}