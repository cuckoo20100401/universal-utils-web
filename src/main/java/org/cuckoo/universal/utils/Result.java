package org.cuckoo.universal.utils;

import org.cuckoo.universal.utils.web.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Result
 */
public class Result {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> payload = new LinkedHashMap<>();

    public static ResultBuilder builder() {
        return new ResultBuilder();
    }

    public void success(Boolean success) {
        this.success = success;
    }

    public Boolean success() {
        return this.success;
    }

    public void code(Integer code) {
        this.code = code;
    }

    public Integer code() {
        return this.code;
    }

    public void message(String message) {
        this.message = message;
    }

    public String message() {
        return this.message;
    }

    public void addPayload(String key, Object value) {
        this.payload.put(key, value);
    }

    public <T> T getPayload(String key) {
        Object value = this.payload.get(key);
        if (value != null) {
            return (T) value;
        }
        return null;
    }

    /**
     * 转换到响应实体
     *
     * <p>
     *     用于 Controller 中直接返回响应实体
     * </p>
     * @return
     */
    public ResponseEntity toResponseEntity() {
        ResponseEntity responseEntity = new ResponseEntity(success, code, message);
        if (!payload.isEmpty()) {
            responseEntity.put("payload", payload);
        }
        return responseEntity;
    }
}
