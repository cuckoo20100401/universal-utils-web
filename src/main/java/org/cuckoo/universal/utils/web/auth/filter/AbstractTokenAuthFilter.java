package org.cuckoo.universal.utils.web.auth.filter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cuckoo.universal.utils.web.ResponseEntity;
import org.cuckoo.universal.utils.web.ResponseUtils;
import org.cuckoo.universal.utils.web.auth.AuthUtils;
import org.cuckoo.universal.utils.web.auth.VerifyTokenResult;
import org.springframework.util.AntPathMatcher;

public abstract class AbstractTokenAuthFilter extends AbstractAuthFilter {
	
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
			logInfo.put("auth-null", "No auth run, because authRules is null");
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
				logInfo.put("auth-anon", "ok");
				logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			}
			// validate authc
			VerifyTokenResult verifyTokenResult = null;
			if (matchedAuthRule.indexOf("authc") != -1) {
				String accessToken = request.getHeader("Access-Token");
				verifyTokenResult = this.verifyToken(accessToken);
				if (verifyTokenResult.getResult()) {
					logInfo.put("auth-authc", "ok");
				} else {
					logInfo.put("auth-authc", verifyTokenResult.getResultMessage());
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseJson = ResponseEntity.create().failure().code(verifyTokenResult.getResultCode()).message(verifyTokenResult.getResultMessage()).asJSON();
					ResponseUtils.writeJson(responseJson, response);
					return;
				}
			}
			// validate roles and perms
			Integer code = AuthUtils.validateAuthRuleForRolesAndPerms(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				if (code == 1) {
					logInfo.put("auth-roles_and_perms", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("auth-roles_and_perms", "unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseJson = this.responseUnauthorizedRequest();
					ResponseUtils.writeJson(responseJson, response);
					return;
				}
			}
			// validate roles or perms
			code = AuthUtils.validateAuthRuleForRolesOrPerms(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				if (code == 1) {
					logInfo.put("auth-roles_or_perms", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("auth-roles_or_perms", "unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseJson = this.responseUnauthorizedRequest();
					ResponseUtils.writeJson(responseJson, response);
					return;
				}
			}
			// validate roles
			code = AuthUtils.validateAuthRuleForRoles(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				if (code == 1) {
					logInfo.put("auth-roles", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("auth-roles", "unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseJson = this.responseUnauthorizedRequest();
					ResponseUtils.writeJson(responseJson, response);
					return;
				}
			}
			// validate perms
			code = AuthUtils.validateAuthRuleForPerms(matchedAuthRule, verifyTokenResult.getCurrAuthUserRoles(), verifyTokenResult.getCurrAuthUserPerms());
			if (code != 0) {
				if (code == 1) {
					logInfo.put("auth-perms", "ok");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else if (code == -1) {
					logInfo.put("auth-perms", "unauthorized request");
					logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
					String responseJson = this.responseUnauthorizedRequest();
					ResponseUtils.writeJson(responseJson, response);
					return;
				}
			}
		}
		
		logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logInfo));
		filterChain.doFilter(servletRequest, servletResponse);
	}

	protected abstract VerifyTokenResult verifyToken(String accessToken);
	protected abstract String responseUnauthorizedRequest();
}
