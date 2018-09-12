package org.cuckoo.universal.utils.web;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseEntity {
	
	public class Builder {
		
		private Map<String, Object> msg = new HashMap<>();
		
		public Builder success() {
			msg.put("success", true);
			return this;
		}
		
		public Builder failure() {
			msg.put("success", false);
			return this;
		}
		
		public Builder code(Integer code) {
			msg.put("code", code);
			return this;
		}
		
		public Builder message(String message) {
			msg.put("message", message);
			return this;
		}
		
		public Builder put(String key, Object value) {
			msg.put(key, value);
			return this;
		}
		
		public Builder put(Map<String, Object> msg) {
			this.msg = msg;
			return this;
		}
		
		public Map<String, Object> asMap() {
			return msg;
		}
		
		public String asJSON() throws JsonProcessingException {
			return new ObjectMapper().writeValueAsString(msg);
		}
	}
	
	public static Builder create() {
		return new ResponseEntity().new Builder();
	}
}
