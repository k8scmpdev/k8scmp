package org.k8scmp.login.service.impl;

import org.k8scmp.login.domain.related.UserInfo;
import org.k8scmp.login.domain.related.Role;
import org.k8scmp.login.dao.AuthBiz;
import org.k8scmp.login.domain.LoginType;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.UserPassword;
import org.k8scmp.login.service.UserService;
import org.k8scmp.shiro.token.MultiAuthenticationToken;
import org.k8scmp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jason on 17-8-24.
 */
@Service
public class UserServiceImpl implements UserService {

    protected static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    AuthBiz authBiz;

    /**
     * Verify current user has permission to modify user info related to
     * this 'username'
     *
     * @param username user name
     * @return
     */
    private boolean verify(String username) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole(Role.ADMINISTRATOR.name())) {
            // if it's ADMINISTRATOR
            return true;
        } else {
            if (username.equals(((String) subject.getPrincipal()))) {
                // normal user can only modify own info
                return true;
            }
        }
        return false;
    }

    @Override
    public HttpResponseTemp<?> normalLogin(UserPassword userPass) {
        Subject subject = SecurityUtils.getSubject();
        String ldapEmailSuffix = null;
//        if (userPass.getLoginType() != null && userPass.getLoginType().equals(LoginType.LDAP)) {
//            LdapInfo ldapInfo = globalBiz.getLdapInfo();
//            if (ldapInfo == null) {
//                return ResultStat.PARAM_ERROR.wrap(null, "ldap info must be set");
//            }
//            ldapEmailSuffix = ldapInfo.getEmailSuffix();
//            String userName = userPass.getUsername();
//            if (ldapEmailSuffix != null && !userName.endsWith(ldapEmailSuffix)) {
//                userPass.setUsername(userName + ldapEmailSuffix);
//            }
//        }
        UsernamePasswordToken token = new MultiAuthenticationToken(userPass.getUsername(), userPass.getPassword(), userPass.getLoginType());
        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
            throw ApiException.wrapMessage(ResultStat.USER_NOT_AUTHORIZED, "username wrong");
        } catch (IncorrectCredentialsException e) {
            throw ApiException.wrapMessage(ResultStat.USER_NOT_AUTHORIZED, "password wrong");
        } catch (ExcessiveAttemptsException e) {
            throw ApiException.wrapMessage(ResultStat.USER_NOT_AUTHORIZED, "login wrong too many times");
        } catch (AuthenticationException e) {
            throw ApiException.wrapUnknownException(e);
        }

        if (userPass.getLoginType() != null && userPass.getLoginType().equals(LoginType.LDAP)) {
            User user = new User();
            user.setUsername(userPass.getUsername());
//            user.setLoginType(LoginType.LDAP);
//            createUserForLDAP(user);
            logger.info("ldap login success, user=" + userPass.getUsername());
        } else {
            logger.info("jdbc login success, user=" + userPass.getUsername());
        }
        return ResultStat.OK.wrap(null);
    }

    @Override
    public User getUser(String username) {
        return authBiz.getUserByName(username);
    }

	@Override
	public boolean loginWithoutType(String userName, String pass) {
		// TODO Auto-generated method stub
		return false;
	}
}
