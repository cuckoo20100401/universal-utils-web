package org.cuckoo.universal.utils.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class Test_ResponseEntity {

    public static void main(String[] args) throws JsonProcessingException {

        ResponseEntity responseEntity = null;

        responseEntity = new Test_ResponseEntity().getList();
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity));

        responseEntity = new Test_ResponseEntity().save();
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity));
    }

    @GetMapping("/getList")
    public ResponseEntity getList() {
        ResponseEntity.success();
        ResponseEntity.success().addPayload("pageInfo", null);
        ResponseEntity.success("查询成功").addPayload("pageInfo", null);
        ResponseEntity.success(200000, "查询成功").addPayload("pageInfo", null);
        return ResponseEntity.success().addPayload("allSysRoles", null).addPayload("pageInfo", null);
    }

    @PostMapping("/save")
    public ResponseEntity save() {
        ResponseEntity.failure();
        ResponseEntity.failure("保存失败");
        ResponseEntity.failure(500001, "帐号不能为空");
        ResponseEntity.failure(500002, "密码不能为空");
        return ResponseEntity.failure(500500, "Token is required");
    }
}