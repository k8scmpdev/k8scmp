package org.k8scmp.login.domain;

import org.k8scmp.util.StringUtils;
import org.k8scmp.login.domain.related.LoginType;
import org.k8scmp.login.domain.related.UserState;
import org.k8scmp.util.CryptoUtil;

/**
 * Created by jason on 2017/9/5.
 */
public class User {
    private int id;
    private String loginname;
    private String username;
    private String password;
    private String salt;
    private String email;
    private String phone;
    private LoginType loginType;
    private UserState state;
    private long createTime;
    private long lastModifiedTime;

    public User() {
    }

    public User(String loginname, String password) {
    	this.loginname = loginname;
        this.password = password;
        this.salt = CryptoUtil.generateSalt();
        this.loginType = LoginType.USER;
        this.state = UserState.NORMAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return id == user.getId();
    }

    

    public String checkLegality() {
        String error = null;
        if (StringUtils.isBlank(loginname)) {
            error = "loginname is blank";
        } else if (StringUtils.isBlank(username)) {
            error = "username is blank";
        } else if (StringUtils.isBlank(password)) {
            error = "password is blank";
        } 
        return error;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return lastModifiedTime;
    }

    public void setUpdateTime(long updateTime) {
        this.lastModifiedTime = updateTime;
    }

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
}
