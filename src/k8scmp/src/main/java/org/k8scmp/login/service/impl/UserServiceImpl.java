package org.k8scmp.login.service.impl;

import org.k8scmp.login.domain.related.UserInfo;
import org.k8scmp.login.domain.related.Role;
import org.k8scmp.login.dao.AuthBiz;
import org.k8scmp.login.domain.ChangeUserPassword;
import org.k8scmp.login.domain.LoginType;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.UserPassword;
import org.k8scmp.login.service.UserService;
import org.k8scmp.shiro.token.MultiAuthenticationToken;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.CryptoUtil;
import org.k8scmp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.CurrentThreadInfo;
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
        UsernamePasswordToken token = new MultiAuthenticationToken(userPass.getLoginname(), userPass.getPassword(), userPass.getLoginType());
        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
        	return ResultStat.USER_NOT_AUTHORIZED.wrap(null,"username wrong");
//            throw ApiException.wrapMessage(ResultStat.USER_NOT_AUTHORIZED, "username wrong");
        } catch (IncorrectCredentialsException e) {
        	return ResultStat.USER_NOT_AUTHORIZED.wrap(null,"password wrong");
//            throw ApiException.wrapMessage(ResultStat.USER_NOT_AUTHORIZED, "password wrong");
        } catch (ExcessiveAttemptsException e) {
        	return ResultStat.USER_NOT_AUTHORIZED.wrap(null,"login wrong too many times");
//            throw ApiException.wrapMessage(ResultStat.USER_NOT_AUTHORIZED, "login wrong too many times");
        } catch (AuthenticationException e) {
            throw ApiException.wrapUnknownException(e);
        }

        if (userPass.getLoginType() != null && userPass.getLoginType().equals(LoginType.LDAP)) {
            User user = new User();
            user.setUsername(userPass.getLoginname());
//            user.setLoginType(LoginType.LDAP);
//            createUserForLDAP(user);
            logger.info("ldap login success, user=" + userPass.getLoginname());
        } else {
            logger.info("jdbc login success, user=" + userPass.getLoginname());
        }
        return ResultStat.OK.wrap(null);
    }

    @Override
    public User getUser(String loginname) {
        return authBiz.getUserByName(loginname);
    }

	@Override
	public boolean loginWithoutType(String userName, String pass) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HttpResponseTemp<?> createUser(User user, boolean flag) {
		int userId = CurrentThreadInfo.getUserId();
        if (flag) {
//            if (!AuthUtil.isAdmin(userId)) {
//                throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "must be admin");
//            }
        }
        if (user == null) {
            throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "user info is null");
        }

        if (!StringUtils.isBlank(user.checkLegality())) {
            throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, user.checkLegality());
        }

        if (authBiz.getUserByName(user.getUsername()) != null) {
            throw ApiException.wrapResultStat(ResultStat.USER_EXISTED);
        }

        CryptoUtil.encryptPassword(user);
        user.setCreateTime(System.currentTimeMillis());

        authBiz.addUser(user);
        return ResultStat.OK.wrap(user);
	}

	@Override
	public HttpResponseTemp<?> createUser(User user) {
		return createUser(user, true);
	}

	@Override
	public HttpResponseTemp<?> deleteUser(String loginname) {
		int userId = CurrentThreadInfo.getUserId();
//        if (!AuthUtil.isAdmin(userId)) {
//            throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "must be admin");
//        }
        User user = authBiz.getUserByLoginname(loginname);
        if (user == null) {
            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
        }
        user.setUpdateTime(System.currentTimeMillis());
//        user.setState(UserState.DELETED);
        authBiz.deleteUser(user);
        return ResultStat.OK.wrap("");
	}

	@Override
	public HttpResponseTemp<?> modifyUser(User user) {
		int userId = CurrentThreadInfo.getUserId();
        if (user == null) {
            throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "user is blank");
        }
//        User currentUser = new User();
//        if (currentUser == null) {
//            throw ApiException.wrapMessage(ResultStat.USER_NOT_EXIST, "user not exists");
//        }
//        if (AuthUtil.isAdmin(userId) || user.getId() == userId) {
//            if (user.getEmail() != null) {
//                currentUser.setEmail(user.getEmail());
//            }
//            if (user.getPhone() != null) {
//                currentUser.setPhone(user.getPhone());
//            }
            user.setUpdateTime(System.currentTimeMillis());
            authBiz.modifyUser(user);
            return ResultStat.OK.wrap(null);
//        } else {
//            throw ApiException.wrapResultStat(ResultStat.USER_NOT_LEGAL);
//        }
	}

	@Override
	public HttpResponseTemp<?> changePassword(ChangeUserPassword changeUserPassword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpResponseTemp<?> changePasswordByAdmin(UserPassword userPassword) {
		int userId = CurrentThreadInfo.getUserId();
//        if (!AuthUtil.isAdmin(userId)) {
//            throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "must be admin");
//        }
        if (userPassword == null) {
            throw ApiException.wrapMessage(ResultStat.USER_NOT_AUTHORIZED, "userPassword is null");
        }
        User user = new User(userPassword.getLoginname(), userPassword.getPassword());
        CryptoUtil.encryptPassword(user);
        authBiz.changePassword(user);
        return ResultStat.OK.wrap("");
	}

	@Override
    public HttpResponseTemp<List<User>> listAllUserInfo() {
        List<User> users = authBiz.listAllUser();
        return ResultStat.OK.wrap(users);
    }
	
	@Override
	public HttpResponseTemp<List<User>> listUsersByKW(String keyword) {
		List<User> users = authBiz.listUsersByKW(keyword);
        return ResultStat.OK.wrap(users);
	}

	@Override
	public HttpResponseTemp<?> getUserInfo(String keyword) {
		User userInfo = authBiz.getUser(keyword);
        if (userInfo == null) {
            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
        }
        return ResultStat.OK.wrap(userInfo);
	}

	@Override
	public Set<String> findRoles(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> findPermissions(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getUserId(String username) {
        User user = authBiz.getUserByName(username);
        if (user == null) {
            return -1;
        }
        return user.getId();
    }

	@Override
	public boolean createUserForLDAP(User user) {
		// TODO Auto-generated method stub
		return false;
	}
}
