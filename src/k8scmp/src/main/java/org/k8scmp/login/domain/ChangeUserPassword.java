package org.k8scmp.login.domain;

/**
 * Created by zhenfengchen on 15-11-17.
 * use this to change password
 */
public class ChangeUserPassword {
    private String loginname;
    private String oldpassword;
    private String newpassword;

    public ChangeUserPassword() {

    }

    public ChangeUserPassword(String loginname, String oldpassword, String newpassword) {
        this.loginname = loginname;
        this.oldpassword = oldpassword;
        this.newpassword = newpassword;
    }

    public String getloginname() {
        return loginname;
    }

    public void setloginname(String loginname) {
        this.loginname = loginname;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    @Override
    public String toString() {
        return "UserPassword{" +
                "loginname='" + loginname + '\'' +
                ", oldpassword='" + oldpassword + '\'' +
                ", newpassword='" + newpassword + '\'' +
                '}';
    }
}
