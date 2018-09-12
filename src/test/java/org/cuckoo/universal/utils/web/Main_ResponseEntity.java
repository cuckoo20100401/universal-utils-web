package org.cuckoo.universal.utils.web;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main_ResponseEntity {

	public static void main(String[] args) throws JsonProcessingException {

		System.out.println(ResponseEntity.create().success().code(200).message("操作成功").asJSON());
		System.out.println(ResponseEntity.create().failure().code(400).message("操作失败").asJSON());
		
		Map<String, Object> msg = new HashMap<>();
		msg.put("success", true);
		msg.put("message", "操作成功");
		System.out.println(ResponseEntity.create().put(msg).asJSON());
	}

}
