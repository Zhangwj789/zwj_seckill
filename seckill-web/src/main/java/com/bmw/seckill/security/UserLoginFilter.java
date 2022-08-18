package com.bmw.seckill.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bmw.seckill.common.base.BaseResponse;
import com.bmw.seckill.common.entity.CommonWebUser;
import com.bmw.seckill.common.exception.ErrorMessage;
import com.bmw.seckill.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * @author zhangwenjuan
 * @date 2022/8/6
 */
@Slf4j
@WebFilter(filterName = "userLoginFilter",urlPatterns = "/")
public class UserLoginFilter implements Filter {

    @Autowired
    private RedisUtil redisUtil;
    //本filter配置的是拦截所有，urlPattrern是配置的需要拦截的地址，其他地址不做拦截
    @Value("${auth.login.pattern}")
    private String urlPattern;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =  (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpSession session = request.getSession();

        String url = request.getRequestURI();
        log.info("url:=" + url+",pattern:=" +urlPattern);
        if(url.matches(urlPattern)){
            //相当于检查当前会话的token是否存在，如果不为空（存在），则说明在token过期时间内，这个会话的id已经刚登录过了
            if(session.getAttribute(WebUserUtil.SESSION_WEBUSER_KEY)!=null){
                filterChain.doFilter(request,response);
                return;
            }else {
                //这个时候说明token不存在，就是登录时间过期或者就没有登陆过
                //token我们此处约定保存在http协议的header中，也可以保存在cookie中
                //调用我们接口的前端或客户端也会保存cookie。
                String tokenValue = request.getHeader("token");
                //检查一下http的协议header中是否有token，如果有，从redis中获取
                if(StringUtils.isNotEmpty(tokenValue)){
                    Object object = redisUtil.get(tokenValue);
                    //获取出来的token如果不为空，则把这个token放入该会话中，并且不拦截
                    if(object != null){
                        CommonWebUser commonWebUser = JSONObject.parseObject(object.toString(),CommonWebUser.class);
                        session.setAttribute(WebUserUtil.SESSION_WEBUSER_KEY,commonWebUser);
//在用户的请求到达servlet之前，拦截下来做预处理，处理之后便执行chain.doFilter(request, response)这个方法，如果还有别的过滤器，
// 那么将处理好的请求传给下个过滤器，依此类推，当所有的过滤器都把这个请求处理好了之后，再将处理完的请求发给servlet；如果就这一个过滤器，那么就将处理好的请求直接发给servlet。
                        filterChain.doFilter(request,response);
                        return;
                    }else {
                        //返回接口调用方需要登录的错误码，接口调用方开始登录
                        returnJson(response);
                        return;
                    }
                }else{
                    //返回接口调用方需要登录的错误码，接口调用方开始登录
                    returnJson(response);
                    return;
                }
            }
        }

        filterChain.doFilter(request,response);
        return;
    }
    /**
     * 返回需要登录的约定格式的错误码，接口调用方根据错误码进行登录操作
     */
    private void returnJson(ServletResponse response){
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        try {
            writer = response.getWriter();
            BaseResponse baseResponse = new BaseResponse(ErrorMessage.USER_NEED_LOGIN.getCode(),
                    ErrorMessage.USER_NEED_LOGIN.getMessage(), null);
            writer.println(JSON.toJSONString(baseResponse));
        } catch (IOException e) {
            log.error("response error",e);
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }
}