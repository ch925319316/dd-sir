package com.web.mundo.config;

public enum ResultStatusEnum {

    SUCCESS(200,"请求成功"),
    FAIL(500,"请求失败"),
    NODATE(202,"无数据");

    public int code;
    public String statu;

     ResultStatusEnum(int code, String statu){
         this.code = code;
         this.statu = statu;
    }

}
