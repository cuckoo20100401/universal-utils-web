package org.cuckoo.universal.utils;

public class Test_Result {

    public Result save() {
//        return Result.builder().failure().build();
        return Result.builder().success().build();
    }

    public void test() {
        Result result = this.save();
        if (result.success()) {
            System.out.println("执行成功");
        }
    }
}
