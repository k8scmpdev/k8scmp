package org.k8scmp.login.dao.impl;

import org.k8scmp.login.dao.AuthBiz;
//import org.k8scmp.framework.api.biz.base.impl.BaseBizImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.k8scmp.login.domain.AdminRole;
import org.k8scmp.login.domain.User;
import org.k8scmp.mapper.login.AdminRolesMapper;
import org.k8scmp.mapper.login.UserMapper;

import java.util.List;

/**
 * Created by jason on 2017/8/25.
 */
@Service("authBiz")
public class AuthBizImpl implements AuthBiz {
    @Autowired
    AdminRolesMapper adminRolesMapper;
    @Autowired
    UserMapper userMapper;


    @Override
    public boolean isAdmin(int userId) {
        AdminRole adminRole = adminRolesMapper.getAdminById(userId);
        if (adminRole != null) {
            return true;
        }
        return false;
    }

   
    @Override
    public List<String> getRole(String username) {
        return userMapper.getRole(username);
    }
    
    @Override
    public User getUser(String userName) {
        return userMapper.getUserByName(userName);
    }
    
    @Override
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }


	@Override
	public int getUserId(String userName) {
		// TODO Auto-generated method stub
		return 0;
	}


	


}