package org.k8scmp.login.service;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.login.domain.ChangeUserPassword;
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
    
    HttpResponseTemp<?> createUser(User user, boolean flag);

    HttpResponseTemp<?> createUser(User user);

    HttpResponseTemp<?> deleteUser(String loginname);

    HttpResponseTemp<?> modifyUser(User user);

    HttpResponseTemp<?> changePassword(ChangeUserPassword changeUserPassword);

    HttpResponseTemp<?> changePasswordByAdmin(UserPassword userPassword);

    HttpResponseTemp<List<User>> listAllUserInfo();

    HttpResponseTemp<?> getUserInfo(String username);

    Set<String> findRoles(String username);

    Set<String> findPermissions(String username);

    int getUserId(String username);

    boolean createUserForLDAP(User user);

	HttpResponseTemp<List<User>> listUsersByKW(String keyword);

}
