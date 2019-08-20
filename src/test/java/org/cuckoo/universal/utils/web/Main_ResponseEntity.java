package org.cuckoo.universal.utils.web;

import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main_ResponseEntity {

	public static void main(String[] args) throws JsonProcessingException {

		ResponseEntity re = test01();
		System.out.println("success: "+re.success());
		System.out.println("message: "+re.message());
		System.out.println("code: "+re.code());
		
		// 自动类型转换
		String name = re.get("user.name");
		Integer age = re.get("user.age");
		LocalDate birthday = re.get("user.birthday");
		System.out.println("user.name: "+name);
		System.out.println("user.age: "+age);
		System.out.println("user.birthday: "+birthday.toString());
		
		// 转化为 JSON or Map
		System.out.println(re.asJSON());
		System.out.println(re.asJSONWithout());
		System.out.println(re.asMap());
		System.out.println(re.asMapWithout());
		
		// 直接输出JSON
		System.out.println(ResponseEntity.create().success().code(200).message("操作成功").asJSON());
		System.out.println(ResponseEntity.create().failure().code(400).message("操作失败，客户端异常").asJSON());
	}
	
	public static ResponseEntity test01() {
		return ResponseEntity.create()
				.success()
				.code(200)
				.message("ok")
				.put("user.name", "Jim")
				.put("user.age", 31)
				.put("user.birthday", LocalDate.of(1988, 07, 28))
				.as();
	}

}
