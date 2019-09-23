package org.cuckoo.universal.utils.web;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseEntity {
	
	private Boolean success;
	private String message;
	private Integer code;
	private Map<String, Object> payload = new HashMap<>();
	
	public class Builder {
		
		private ResponseEntity re = new ResponseEntity();
		
		public Builder success() {
			this.re.success(true);
			return this;
		}
		
		public Builder failure() {
			this.re.success(false);
			return this;
		}
		
		public Builder code(Integer code) {
			this.re.code(code);
			return this;
		}
		
		public Builder message(String message) {
			this.re.message(message);
			return this;
		}
		
		public Builder put(String key, Object value) {
			this.re.put(key, value);
			return this;
		}
		
		public ResponseEntity as() {
			return this.re;
		}
		public Map<String, Object> asMap() {
			return this.re.asMap();
		}
		public Map<String, Object> asMapWithout() {
			return this.re.asMapWithout();
		}
		public String asJSON() throws JsonProcessingException {
			return this.re.asJSON();
		}
		public String asJSONWithout() throws JsonProcessingException {
			return this.re.asJSONWithout();
		}
	}
	
	public static Builder create() {
		return new ResponseEntity().new Builder();
	}
	public static Builder build() {
		return create();
	}

	public Boolean success() {
		return success;
	}
	public void success(Boolean success) {
		this.success = success;
	}
	public String message() {
		return message;
	}
	public void message(String message) {
		this.message = message;
	}
	public Integer code() {
		return code;
	}
	public void code(Integer code) {
		this.code = code;
	}
	public void put(String key, Object value) {
		this.payload.put(key, value);
	}
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Object value = this.payload.get(key);
		if (value != null) {
			return (T) value;
		}
		return null;
	}
	public Map<String, Object> asMap() {
		Map<String, Object> map = new HashMap<>();
		if (this.success != null) map.put("success", this.success);
		if (this.message != null) map.put("message", this.message);
		if (this.code != null) map.put("code", this.code);
		if (!this.payload.isEmpty()) map.put("payload", this.payload);
		return map;
	}
	public Map<String, Object> asMapWithout() {
		Map<String, Object> map = new HashMap<>();
		if (this.success != null) map.put("success", this.success);
		if (this.message != null) map.put("message", this.message);
		if (this.code != null) map.put("code", this.code);
		if (!this.payload.isEmpty()) map.putAll(this.payload);
		return map;
	}
	public String asJSON() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this.asMap());
	}
	public String asJSONWithout() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this.asMapWithout());
	}
}
