package org.cuckoo.universal.utils.web;

import java.util.LinkedHashMap;

/**
 * ResponseEntity
 */
public class ResponseEntity extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 2614846568297522329L;

	public ResponseEntity(boolean success, Integer code, String message) {
        super.put("success", success);
        if (code != null) {
            super.put("code", code);
        }
        super.put("message", message);
    }

    public static ResponseEntity success() {
        return new ResponseEntity(true, 200000, "ok");
    }
    public static ResponseEntity success(String message) {
        return new ResponseEntity(true, 200000, message);
    }
    public static ResponseEntity success(Integer code, String message) {
        return new ResponseEntity(true, code, message);
    }

    public static ResponseEntity failure() {
        return new ResponseEntity(false, 500000, "error");
    }
    public static ResponseEntity failure(String message) {
        return new ResponseEntity(false, 500000, message);
    }
    public static ResponseEntity failure(Integer code, String message) {
        return new ResponseEntity(false, code, message);
    }

    /**
     * 添加消息负载
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
	public ResponseEntity addPayload(String key, Object value) {

        LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>) this.get("payload");
        if (payload == null) {
            payload = new LinkedHashMap<>();
        }

        payload.put(key, value);
        this.put("payload", payload);
        return this;
    }
}