package com.bmw.seckill.security;

import com.alibaba.fastjson.JSONObject;
import com.bmw.seckill.common.entity.CommonWebUser;
import com.bmw.seckill.util.RedisUtil;
import com.bmw.seckill.util.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebUserUtil {

    public static final String SESSION_WEBUSER_KEY = "web_user_key";

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static CommonWebUser getLoginUser() {

        // 获取相关对象
        //RequestContextHolder：持有上下文的Request容器，通过RequestContextHolder的静态方法可以随时随地取到当前请求的request对象
        //requestContextHolder原理类似于ThreadLocal
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpSession session = request.getSession();

        CommonWebUser commonWebUser = null;
        if (session.getAttribute(SESSION_WEBUSER_KEY) != null) {
            commonWebUser = (CommonWebUser)session.getAttribute(SESSION_WEBUSER_KEY);
        } else {
            //由于WebUserUtil是一个普通类，不被spring管理，没有Autowired注入
            //Spring的IOC机制我们知道Spring的bean由ApplicationContext管理
            //所以添加一个类SpringContextHolder，这个类由Spring管理，把ApplicationContext注入
            RedisUtil redisUtil = SpringContextHolder.getBean("redisUtil");
            if (StringUtils.isNotEmpty(request.getHeader("token"))) {
                Object object = redisUtil.get(request.getHeader("token"));
                System.out.println(object);
                if (object != null) {
                    commonWebUser = JSONObject.parseObject(object.toString(), CommonWebUser.class);
                    session.setAttribute(SESSION_WEBUSER_KEY, commonWebUser);
                }
            }
        }
        return commonWebUser;
    }

}
