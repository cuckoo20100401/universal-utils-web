package org.cuckoo.universal.utils.web;

import java.util.LinkedHashMap;

public class Example_AuthRules {
	
	protected LinkedHashMap<String, String> initAuthRules() {
		
		LinkedHashMap<String, String> authRules = new LinkedHashMap<>();
		//静态资源
		authRules.put("/static/**", "anon");
		//认证模块
		authRules.put("/auth/login", "anon");
		authRules.put("/auth/refreshToken", "anon");
		//系统管理
		authRules.put("/sys/role/**", "authc, roles[超级管理员] or perms[MENU-系统管理-角色管理]");
		authRules.put("/sys/user/**", "authc, roles[超级管理员] or perms[MENU-系统管理-用户管理]");
		//消费管理
		authRules.put("/consumption/**", "authc, roles[超级管理员] or perms[MENU-消费管理-主要消费]");
		authRules.put("/consumption_other/**", "authc, roles[超级管理员] or perms[MENU-消费管理-其它消费]");
		authRules.put("/consumption_statistics/**", "authc, roles[超级管理员] or perms[MENU-消费管理-消费统计]");
		//未来扩展
//		this.authRules.put("/teacher/**", "authc, roles[admin1,admin2]"); //supported
//		this.authRules.put("/teacher/**", "authc, perms[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]"); //supported
//		this.authRules.put("/teacher/**", "authc, roleOR[admin1,admin2]");
//		this.authRules.put("/teacher/**", "authc, permOR[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]");
//		this.authRules.put("/teacher/**", "authc, roles[admin1,admin2] and perms[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]"); //supported
//		this.authRules.put("/teacher/**", "authc, roles[admin1,admin2] or perms[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]"); //supported
//		this.authRules.put("/teacher/**", "authc, roleOR[admin1,admin2] and permOR[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]");
//		this.authRules.put("/teacher/**", "authc, roleOR[admin1,admin2] or permOR[MENU-系统管理-角色管理1,MENU-系统管理-角色管理2]");
		//其它
		authRules.put("/**", "authc");
		return authRules;
	}
}