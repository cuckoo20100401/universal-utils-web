package org.cuckoo.universal.utils.web.auth.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cuckoo.universal.utils.web.ResponseEntity;
import org.cuckoo.universal.utils.web.ResponseUtils;
import org.cuckoo.universal.utils.web.auth.AuthUtils;
import org.cuckoo.universal.utils.web.auth.VerifyTokenResult;
import org.springframework.util.AntPathMatcher;

/**
 * 注：
 * 	1、只有配置this.authRules.put("/**", "authc")拦截所有请求的情况下，才会配置匿名访问，故匿名访问时不需要更新token。
 * 	2、因为cookie的作用域只跟域名和路径有关，跟端口无关，所以当一台服务器部署多个项目时，可能会出现token被相互覆盖的情况（解决办法：一台服务器只有一个IP，但可以有不同的域名，按域名保存token即可）。
 */
public abstract class AbstractCookieAndTokenAuthFilter extends AbstractAuthFilter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.authRules = this.initAuthRules();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		
		Map<String, String> logInfo = new LinkedHashMap<>();
		logInfo.put("filterName", this.filterConfig.getFilterName());
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		String currURI = request.getRequestURI();
		logInfo.put("currURI", currURI);
		String contextPath = request.getContextPath();
		if (!contextPath.equals("/")) {
			currURI = currURI.replaceFirst(contextPath, "");
		}
		
		String currReqPath = currURI;
		logInfo.put("currReqPath", currReqPath);
		
		if (this.authRules == null) {
			logInfo.put("authType", "No auth run, because authRules is null");
			logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		AntPathMatcher pathMatcher = new AntPathMatcher();
		String matchedAuthPattern = this.authRules.keySet().stream().filter(authPattern -> pathMatcher.match(authPattern, currReqPath)).findFirst().orElse(null);
		logInfo.put("matchedAuthPattern", matchedAuthPattern);
		if (matchedAuthPattern != null) {
			String matchedAuthRule = this.authRules.get(matchedAuthPattern);
			logInfo.put("matchedAuthRule", matchedAuthRule);
			// validate anon
			if (matchedAuthRule.trim().equals("anon")) {
				logInfo.put("authType", "anon");
				logInfo.put("authResult", "ok");
				logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			}
			// validate authc
			VerifyTokenResult verifyTokenResult = null;
			if (matchedAuthRule.indexOf("authc") != -1) {
				logInfo.put("authAuthc", "yes");
				String accessToken = null;
				if (request.getCookies() != null) {
					Cookie accessTokenCookie = Arrays.asList(request.getCookies()).stream().filter(cookie -> cookie.getName().equals("token")).findFirst().orElse(null);
					if (accessTokenCookie != null) {
						accessToken = accessTokenCookie.getValue();
					}
				}
				verifyTokenResult = this.verifyToken(accessToken);
				if (verifyTokenResult.getResult()) {
					logInfo.put("authAuthcResult", "ok");
					this.updateToken(verifyTokenResult, response);
				} else {
					logInfo.put("authAuthcResult", verifyTokenResult.getResultMessage());
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseTypeString = this.responseVerityTokenFailureRequest();
					if (responseTypeString != null && responseTypeString.startsWith("url:")) {
						response.sendRedirect(responseTypeString.substring(4));
					} else {
						String responseJson = ResponseEntity.create().failure().code(verifyTokenResult.getResultCode()).message(verifyTokenResult.getResultMessage()).asJSON();
						ResponseUtils.writeJson(responseJson, response);
					}
					return;
				}
			}
			// validate roles and perms
			Integer code = AuthUtils.validateAuthRuleForRolesAndPerms(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				logInfo.put("authType", "roles_and_perms");
				if (code == 1) {
					logInfo.put("authResult", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("authResult", "Unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseTypeString = this.responseUnauthorizedRequest();
					if (responseTypeString != null && responseTypeString.startsWith("url:")) {
						response.sendRedirect(responseTypeString.substring(4));
					} else {
						String responseJson = responseTypeString.substring(5);
						ResponseUtils.writeJson(responseJson, response);
					}
					return;
				}
			}
			// validate roles or perms
			code = AuthUtils.validateAuthRuleForRolesOrPerms(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				logInfo.put("authType", "roles_or_perms");
				if (code == 1) {
					logInfo.put("authResult", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("authResult", "Unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseTypeString = this.responseUnauthorizedRequest();
					if (responseTypeString != null && responseTypeString.startsWith("url:")) {
						response.sendRedirect(responseTypeString.substring(4));
					} else {
						String responseJson = responseTypeString.substring(5);
						ResponseUtils.writeJson(responseJson, response);
					}
					return;
				}
			}
			// validate roles
			code = AuthUtils.validateAuthRuleForRoles(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				logInfo.put("authType", "roles");
				if (code == 1) {
					logInfo.put("authResult", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("authResult", "Unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseTypeString = this.responseUnauthorizedRequest();
					if (responseTypeString != null && responseTypeString.startsWith("url:")) {
						response.sendRedirect(responseTypeString.substring(4));
					} else {
						String responseJson = responseTypeString.substring(5);
						ResponseUtils.writeJson(responseJson, response);
					}
					return;
				}
			}
			// validate perms
			code = AuthUtils.validateAuthRuleForPerms(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				logInfo.put("authType", "perms");
				if (code == 1) {
					logInfo.put("authResult", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("authResult", "Unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseTypeString = this.responseUnauthorizedRequest();
					if (responseTypeString != null && responseTypeString.startsWith("url:")) {
						response.sendRedirect(responseTypeString.substring(4));
					} else {
						String responseJson = responseTypeString.substring(5);
						ResponseUtils.writeJson(responseJson, response);
					}
					return;
				}
			}
		}
		
		logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
		filterChain.doFilter(servletRequest, servletResponse);
	}

	protected abstract VerifyTokenResult verifyToken(String accessToken);
	protected abstract void updateToken(VerifyTokenResult verifyTokenResult, HttpServletResponse response);
	protected abstract String responseVerityTokenFailureRequest();
	protected abstract String responseUnauthorizedRequest();
}
