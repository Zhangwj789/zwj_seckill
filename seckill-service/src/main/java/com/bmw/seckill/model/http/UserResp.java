package com.bmw.seckill.model.http;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangwenjuan
 * @date 2022/8/6
 */
@Data
public class UserResp implements Serializable {
    @Data
    public static class BaseUserResp implements Serializable{
        private String token;
    }
}