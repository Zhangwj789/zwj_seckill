package com.bmw.seckill.security.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangwenjuan
 * @date 2022/8/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Token implements Serializable {
    private static final long serialVersionUID = 6314027741784310221L;
    private String token;
    private Long loginTime;
    private Long userId;
}