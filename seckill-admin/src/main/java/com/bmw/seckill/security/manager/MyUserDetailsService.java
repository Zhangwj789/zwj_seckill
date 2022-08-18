package com.bmw.seckill.security.manager;

import com.bmw.seckill.model.SeckillAdmin;
import com.bmw.seckill.service.IAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



/**
 * @author zhangwenjuan
 * @date 2022/8/9
 * 实际的后台admin用户名和密码验证方法
 */
@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private IAdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户的用户名获取该用户的信息
        SeckillAdmin seckillAdmin = adminService.findByUsername(username);

        if(seckillAdmin == null) {
            //用户不存在报错
            throw new UsernameNotFoundException("用户不存在");
        }

        AdminUser adminUser = new AdminUser();
        try {
            BeanUtils.copyProperties(adminUser,seckillAdmin);
            adminUser.setUsername(seckillAdmin.getLoginName());
            adminUser.setCreateDate(seckillAdmin.getCreateTime());

            //直接写角色（实际上应该是从数据库读取）
            dealAdminRoles(adminUser);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        /**
         * 将roles信息转换成SpringSecurity内部的形式，即Authorities
         * commaSeparateStringToAuthorityList可以将使用隔开的角色列表切割出来并赋值list
         * 如果不行的话，也可以自己实现这个方法
         */
        //注意，这里放入Authorities中的信息，都需要是以Role_开头的，所以我们在数据库中配置的都是这种格式的。
        // 当我们使用hasRole做比对的时候，必须要是带Role_开头的。否则可以使用hasAuthority方法做比对
        adminUser.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(adminUser.getRoles()));
        //log.info（JSON.toJSONString(adminUser);

        return adminUser;
    }

    /**
     * 应该从数据库中读取用户的role角色记录，此处读取的是普通的数据.
     */
    private void dealAdminRoles(AdminUser adminUser) {
        if (adminUser.getUsername().toLowerCase().indexOf("super") >= 0) {
            adminUser.setRoles("ADMIN,SUPERADMIN");
        } else {
            adminUser.setRoles("ADMIN");
        }
    }
}