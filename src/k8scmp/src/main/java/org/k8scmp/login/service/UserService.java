package org.k8scmp.login.service;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.UserPassword;

import java.util.List;
import java.util.Set;

/**
 * Created by jason on 17-8-24.
 */
public interface UserService {
    /**
     * login with username and password for a register user
     *
     * @param userPassword
     * @return
     */
    HttpResponseTemp<?> normalLogin(UserPassword userPassword);

    User getUser(String username);
    
    boolean loginWithoutType(String userName, String pass);
}
