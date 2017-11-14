package org.k8scmp.monitormgmt.domain.alarm;

/**
 * Created by baokangwang on 2016/3/31.
 */
public class UserGroupInfo {

    private int id;
    private String userGroupName;

    public UserGroupInfo() {
    }

    public UserGroupInfo(int id, String userGroupName) {
        this.id = id;
        this.userGroupName = userGroupName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }
}
