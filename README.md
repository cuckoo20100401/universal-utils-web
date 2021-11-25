# universal-utils-web

Contains universal functions for java



## Supports

- Spring java web project.



## Features

### Utils

universal utils.

- FileUtils
- ImageUtils
- IPUtils
- MathUtils
- ResultEntity
- StringUtils
- ValidateUtils

### Web Utils

universal web utils.

- PageInfo
- RequestUtils
- ResponseCode
- ResponseEntity
- ResponseUtils
- VisitorUtils

### Security

login, auth roles and permissions, for horizontal scaling of servers.

#### Configuration
1. 配置SpringBoot的启动文件
   - @ComponentScan(basePackages = {"com.cuckoo.project", "org.cuckoo.universal.security"})   //开启Spring注解
   - @ServletComponentScan(basePackages = {"org.cuckoo.universal.security"})                  //开启Servlet注解
```java
package com.cuckoo.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * 需要开启框架中的Spring注解和Servlet注解
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.cuckoo.project", "org.cuckoo.universal.security"})
@ServletComponentScan(basePackages = {"org.cuckoo.universal.security"})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
```
2. SecurityConfig.java
   - 最简配置
```java
package com.cuckoo.project.config;

import com.example.demo.dao.SysUserDAO;
import org.cuckoo.universal.security.AuthUser;
import org.cuckoo.universal.security.config.SecurityConfiguration;
import org.cuckoo.universal.security.mvc.service.AuthenticationService;
import org.cuckoo.universal.security.provider.CookieAndTokenAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinimalSecurityConfig {

    @Autowired
    private SysUserDAO sysUserDAO;

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfiguration.builder()
                .setAuthenticationProvider(new CookieAndTokenAuthenticationProvider())
                .addAuthenticationRuleFromProperties("security.properties")
                .addAuthenticationRule("/**", "authc")
                .build();
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService() {
            @Override
            public AuthUser findAuthUserByUsername(String username) {
                try {
                    return sysUserDAO.findByUsername(username);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String[] findAuthUserRolesById(String id) {
                try {
                    return sysUserDAO.findRolesById(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new String[0];
                }
            }

            @Override
            public String[] findAuthUserPermsById(String id) {
                try {
                    return sysUserDAO.findPermsById(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new String[0];
                }
            }
        };
    }
}
```
   - 最全配置
```java
package com.cuckoo.project.config;

import com.example.demo.dao.SysUserDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cuckoo.universal.security.AuthUser;
import org.cuckoo.universal.security.Authentication;
import org.cuckoo.universal.security.config.SecurityConfiguration;
import org.cuckoo.universal.security.handler.AuthenticationFailureHandler;
import org.cuckoo.universal.security.handler.AuthenticationSuccessHandler;
import org.cuckoo.universal.security.handler.TokenHandler;
import org.cuckoo.universal.security.mvc.service.AuthenticationService;
import org.cuckoo.universal.security.provider.CookieAndTokenAuthenticationProvider;
import org.cuckoo.universal.utils.ResultEntity;
import org.cuckoo.universal.utils.web.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 安全配置
 *
 * <h2>定义安全配置（必须的）</h2>
 * <ol>
 *     <li>设置认证提供者（必须的）</li>
 *     <li>添加认证规则（可选的；注：/auth/login和/auth/refreshToken在框架内部已经开放，不需要额外配置）</li>
 *     <li>添加各种处理器（可选的）</li>
 * </ol>
 * <h2>定义认证服务（必须的）</h2>
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private SysUserDAO sysUserDAO;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfiguration.builder()
                .setAuthenticationProvider(new CookieAndTokenAuthenticationProvider())
                .setCreateTokenSecret("1234")
                .setCreateTokenExpiredMinutes(60)
                .addAuthenticationRuleFromProperties("security.properties")
                .addAuthenticationRule("/**", "authc")
                .addTokenHandler(new TokenHandler() {
                    @Override
                    public String createToken(AuthUser authUser) {
                        return null;
                    }

                    @Override
                    public String createRefreshToken(AuthUser authUser) {
                        return null;
                    }

                    @Override
                    public Authentication verifyAndParseToken(Authentication authentication) {

                        // 1.verify token

                        // 2.parse token
                        AuthUser authUser = authentication.getAuthUser();
                        authUser.setRoles(new String[]{"guest"});
                        authUser.setPerms(new String[]{"book-view", "book-edit"});

                        // 3.update token, is optional
                        HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
                        response.addCookie(new Cookie("Access-Token", "token-created"));

                        // 4.set auth result
                        authentication.setAuthResult(ResultEntity.builder().success().build());
                        return authentication;
                    }

                    @Override
                    public Authentication verifyAndParseRefreshToken(Authentication authentication) {
                        return null;
                    }
                })
                .addAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(Authentication authentication) {
                        // 此处设置的属性currentRequestURI供common-top.html使用
                        HttpServletRequest request = (HttpServletRequest) authentication.getRuntimeInstance().getServletRequest();
                        request.setAttribute("currentRequestURI", request.getRequestURI());
                    }
                })
                .addAuthenticationFailureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(Authentication authentication) {
                        try {
                            HttpServletResponse response = (HttpServletResponse) authentication.getRuntimeInstance().getServletResponse();
                            PrintWriter printWriter = response.getWriter();
                            response.setContentType("application/json;charset=utf-8");
                            printWriter.write(objectMapper.writeValueAsString(ResponseEntity.failure(authentication.getAuthResult().code(), authentication.getAuthResult().message())));
                            printWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .build();
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService() {
            @Override
            public AuthUser findAuthUserByUsername(String username) {
                try {
                    return sysUserDAO.findByUsername(username);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String[] findAuthUserRolesById(String id) {
                try {
                    return sysUserDAO.findRolesById(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new String[0];
                }
            }

            @Override
            public String[] findAuthUserPermsById(String id) {
                try {
                    return sysUserDAO.findPermsById(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new String[0];
                }
            }
        };
    }
}
```
   - security.properties
```
//静态资源
/static/** = anon
//用户管理
/api/user/getList = authc, roles[admin] or perms[user-view]
/api/user/save = authc, roles[admin] or perms[user-add]
/api/user/update = authc, roles[admin] or perms[user-update]
/api/user/delete = authc, roles[admin] or perms[user-delete]
//书籍管理
/api/book/** = authc, roles[admin] or perms[book-view,book-add,book-update,book-delete]
```

#### 开启日志
在application.properties中添加如下配置：
```
logging.level.org.cuckoo.universal.security = DEBUG
```

#### 常见问题
1. 解决过滤器顺序
   - 由于Security组件是使用Servlet过滤器实现的，当在项目中配置了别的过滤器，可能会排在框架中认证过滤器的前面，造成多个过滤器顺序的混乱，从而会引起项目业务功能错误。而使用@WebFilter注解的过滤器是通过文件名称排序的，所以在项目中可以通过继承框架中的认证过滤器并修改名称，让其与自己创建的过滤器融洽相处。
2. 解决跨域
   - 使用SpringBoot的跨域配置
   - Controller中使用@CrossOrigin注解
   - 使用nginx的跨域配置
