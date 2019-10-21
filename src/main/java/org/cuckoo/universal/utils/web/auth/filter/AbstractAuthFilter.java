package org.cuckoo.universal.utils.web.auth.filter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cuckoo.universal.utils.web.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @advantage
 * 		1、登录认证
 * 		2、角色和权限认证
 * 		3、服务器的横向扩展
 */
public abstract class AbstractAuthFilter implements Filter {

	protected static final Logger logger = LogManager.getFormatterLogger(AbstractAuthFilter.class);
	
	protected FilterConfig filterConfig;
	protected Map<String, String> authRules;
	protected ObjectMapper jacksonObjectMapper = new ObjectMapper();
	
	@Override
	public abstract void init(FilterConfig filterConfig) throws ServletException;
	
	@Override
	public abstract void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException;
	
	@Override
	public void destroy() {}
	
	public abstract LinkedHashMap<String, String> initAuthRules();
	public abstract ResponseEntity verifyToken(String accessToken);
	public abstract String[] getCurrAuthUserRoles(String accessToken);
	public abstract String[] getCurrAuthUserPerms(String accessToken);
	public abstract ResponseEntity responseUnauthorizedRequest();
}
