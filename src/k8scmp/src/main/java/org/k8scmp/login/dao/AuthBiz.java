package org.k8scmp.login.dao;

import java.util.List;

import org.k8scmp.login.domain.User;

/**
 * Created by jason on 2017/8/24.
 */
public interface AuthBiz {
    boolean isAdmin(int userId);

    int getUserId(String userName);
    
    User getUser(String userName);
    
    User getUserByName(String username);

    List<String> getRole(String username);

	String getUserNameById(int id);

	User getUserById(int userId);

	List<User> listAllUser();
	
	void addUser(User user);
	
	void deleteUser(User user);
	
	void changePassword(User user);

	List<User> listUsersByKW(String keyword);

	void modifyUser(User existUser);
}
