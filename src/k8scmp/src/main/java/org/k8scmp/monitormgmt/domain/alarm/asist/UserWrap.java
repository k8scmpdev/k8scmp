package org.k8scmp.monitormgmt.domain.alarm.asist;

import java.util.List;

import org.k8scmp.login.domain.User;

/**
 * Created by baokangwang on 2016/3/10.
 */
public class UserWrap {

    private String msg;
    private List<User> users;

    public UserWrap() {
    }

    public UserWrap(String msg, List<User> users) {
        this.msg = msg;
        this.users = users;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

