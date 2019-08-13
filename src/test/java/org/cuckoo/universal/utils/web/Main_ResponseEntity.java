package org.cuckoo.universal.utils.web;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main_ResponseEntity {

	public static void main(String[] args) throws JsonProcessingException {

		System.out.println(ResponseEntity.create().success().code(200).message("操作成功").asJSON());
		System.out.println(ResponseEntity.create().failure().code(400).message("操作失败，客户端异常").asJSON());
		
		Map<String, Object> msg = new HashMap<>();
		msg.put("success", false);
		msg.put("message", "操作失败，服务端异常");
		msg.put("code", 500);
		System.out.println(ResponseEntity.build().put(msg).asJSON());
	}

}
