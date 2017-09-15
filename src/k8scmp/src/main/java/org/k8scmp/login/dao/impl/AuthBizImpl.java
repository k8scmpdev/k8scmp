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
    AuthBiz authBiz;
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
    public User getUser(String loginname) {
        return userMapper.getUserByName(loginname);
    }
    
    @Override
    public List<User> listUsersByKW(String keyword) {
    	 List<User> users = userMapper.listUsersByKW(keyword);
        return users;
    }
    
    
    @Override
    public User getUserByName(String loginname) {
        return userMapper.getUserByName(loginname);
    }


    @Override
    public User getUserById(int userId) {
        return userMapper.getUserById(userId);
    }
    
    @Override
    public User getUserByLoginname(String loginname) {
        return userMapper.getUserByName(loginname);
    }
    

    @Override
	public String getUserName(String loginname) {
    	User user = userMapper.getUserByName(loginname);
        if (user == null) {
            return "";
        }
        return user.getUsername();
	}

	@Override
	public String getUserNameById(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
    public int getUserId(String userName) {
        User user = userMapper.getUserByName(userName);
        if (user == null) {
            return -1;
        }
        return user.getId();
    }

	@Override
    public List<User> listAllUser() {
        return userMapper.listAllUserInfo();
    }
	
	@Override
    public void addUser(User user) {
        userMapper.addUser(user);
    }
	
	@Override
    public void deleteUser(User user) {
        userMapper.deleteUser(user);
    }
	
	@Override
    public void changePassword(User user) {
        userMapper.changePassword(user);
    }
	
	@Override
    public void modifyUser(User existUser) {
        userMapper.modifyUser(existUser);
    }

}