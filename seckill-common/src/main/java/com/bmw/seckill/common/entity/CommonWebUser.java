package com.bmw.seckill.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 这个类是用来存放下发好的token，以JSON形式展示
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CommonWebUser implements Serializable {

    private Long id;

    private String name;

    private String phone;

    private Date createTime;

    private Date updateTime;
}
