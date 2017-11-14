package org.k8scmp.monitormgmt.domain.alarm;

/**
 * Created by KaiRen on 2016/9/27.
 */
public class UserGroupBasic {
    private int id;
    private String userGroupName;
    private int creatorId;
    private String creatorName;
    private String createTime;
    private String updateTime;

    public UserGroupBasic() {
    }

    public UserGroupBasic(int id, String userGroupName, int creatorId, String creatorName, String createTime, String updateTime) {
        this.id = id;
        this.userGroupName = userGroupName;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updateTime = updateTime;
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

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}
