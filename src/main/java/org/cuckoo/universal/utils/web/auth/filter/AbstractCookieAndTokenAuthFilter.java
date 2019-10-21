package org.cuckoo.universal.utils.web.auth.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cuckoo.universal.utils.web.ResponseEntity;
import org.cuckoo.universal.utils.web.ResponseUtils;
import org.cuckoo.universal.utils.web.auth.AuthUtils;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 注：
 * 	1、只有配置this.authRules.put("/**", "authc")拦截所有请求的情况下，才会配置匿名访问，故匿名访问时不需要更新token。
 * 	2、因为cookie的作用域只跟域名和路径有关，跟端口无关，所以当一台服务器部署多个项目时，可能会出现token被相互覆盖的情况，待测试。
 */
public abstract class AbstractCookieAndTokenAuthFilter implements Filter {
	
	private static final Logger logger = LogManager.getFormatterLogger(AbstractCookieAndTokenAuthFilter.class);
	
	private FilterConfig filterConfig;
	private Map<String, String> authRules;
	private ObjectMapper jacksonObjectMapper = new ObjectMapper();
	
	@Override
	public void destroy() {}

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
			String[] currAuthUserRoles = null;
			String[] currAuthUserPerms = null;
			if (matchedAuthRule.indexOf("authc") != -1) {
				logInfo.put("authAuthc", "yes");
				String accessToken = null;
				Cookie accessTokenCookie = Arrays.asList(request.getCookies()).stream().filter(cookie -> cookie.getName().equals("token")).findFirst().orElse(null);
				if (accessTokenCookie != null) {
					accessToken = accessTokenCookie.getValue();
				}
				ResponseEntity re = this.verifyToken(accessToken);
				if (re.success()) {
					logInfo.put("authAuthcResult", "ok");
					currAuthUserRoles = this.getCurrAuthUserRoles(accessToken);
					currAuthUserPerms = this.getCurrAuthUserPerms(accessToken);
					this.updateToken(accessToken, response);
				} else {
					logInfo.put("authAuthcResult", re.message());
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					if (re.get("toLoginPage") != null) {
						response.sendRedirect(re.get("toLoginPage"));
					} else {
						ResponseUtils.writeJson(re.asJSON(), response);
					}
					return;
				}
			}
			// validate roles and perms
			Integer code = AuthUtils.validateAuthRuleForRolesAndPerms(matchedAuthRule, currAuthUserRoles, currAuthUserPerms);
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
					ResponseEntity re = this.responseUnauthorizedRequest();
					if (re.get("toLoginPage") != null) {
						response.sendRedirect(re.get("toLoginPage"));
					} else {
						ResponseUtils.writeJson(re.asJSON(), response);
					}
					return;
				}
			}
			// validate roles or perms
			code = AuthUtils.validateAuthRuleForRolesOrPerms(matchedAuthRule, currAuthUserRoles, currAuthUserPerms);
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
					ResponseEntity re = this.responseUnauthorizedRequest();
					if (re.get("toLoginPage") != null) {
						response.sendRedirect(re.get("toLoginPage"));
					} else {
						ResponseUtils.writeJson(re.asJSON(), response);
					}
					return;
				}
			}
			// validate roles
			code = AuthUtils.validateAuthRuleForRoles(matchedAuthRule, currAuthUserRoles, currAuthUserPerms);
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
					ResponseEntity re = this.responseUnauthorizedRequest();
					if (re.get("toLoginPage") != null) {
						response.sendRedirect(re.get("toLoginPage"));
					} else {
						ResponseUtils.writeJson(re.asJSON(), response);
					}
					return;
				}
			}
			// validate perms
			code = AuthUtils.validateAuthRuleForPerms(matchedAuthRule, currAuthUserRoles, currAuthUserPerms);
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
					ResponseEntity re = this.responseUnauthorizedRequest();
					if (re.get("toLoginPage") != null) {
						response.sendRedirect(re.get("toLoginPage"));
					} else {
						ResponseUtils.writeJson(re.asJSON(), response);
					}
					return;
				}
			}
		}
		
		logInfo.put("authType", "have no matchedAuthRule");
		logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.authRules = this.initAuthRules();
	}
	
	public abstract LinkedHashMap<String, String> initAuthRules();
	public abstract ResponseEntity verifyToken(String accessToken);
	public abstract String[] getCurrAuthUserRoles(String accessToken);
	public abstract String[] getCurrAuthUserPerms(String accessToken);
	public abstract ResponseEntity responseUnauthorizedRequest();
	public abstract void updateToken(String accessToken, HttpServletResponse response);
}
