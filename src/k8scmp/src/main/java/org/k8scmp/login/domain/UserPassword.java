package org.k8scmp.login.domain;

import org.k8scmp.login.domain.LoginType;

/**
 * Created by zhenfengchen on 15-11-16.
 * User use this info to login or change a user's password by admin
 */
public class UserPassword {
    private String loginname;
    private String password;
    /**
     * to distiguish different type of login, such as LDAP or USER
     */
    private LoginType loginType;

    public UserPassword() {

    }

    public UserPassword(String loginname, String password) {
        this.loginname = loginname;
        this.password = password;
    }

    public UserPassword(String loginname, String password, LoginType loginType) {
        this.loginname = loginname;
        this.password = password;
        this.loginType = loginType;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "UserPassword{" +
                "loginname='" + loginname + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
