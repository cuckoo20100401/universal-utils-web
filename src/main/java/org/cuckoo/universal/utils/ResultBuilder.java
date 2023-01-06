package org.cuckoo.universal.utils;

/**
 * ResultBuilder
 */
public class ResultBuilder {

    private Result result;

    public ResultBuilder() {
        this.result = new Result();
    }

    public ResultBuilder success() {
        result.success(true);
        return this;
    }

    public ResultBuilder failure() {
        result.success(false);
        return this;
    }

    public ResultBuilder nothing() {
        result.success(null);
        return this;
    }

    public ResultBuilder code(Integer code) {
        result.code(code);
        return this;
    }

    public ResultBuilder message(String message) {
        result.message(message);
        return this;
    }

    public ResultBuilder addPayload(String key, Object value) {
        result.addPayload(key, value);
        return this;
    }

    public Result build() {
        return result;
    }
}
