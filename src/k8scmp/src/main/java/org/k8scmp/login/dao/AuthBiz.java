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
}
