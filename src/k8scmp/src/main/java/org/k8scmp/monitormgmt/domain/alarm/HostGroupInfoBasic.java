package org.k8scmp.monitormgmt.domain.alarm;

import org.k8scmp.util.StringUtils;

/**
 * Created by baokangwang on 2016/3/31.
 */
public class HostGroupInfoBasic {

    private int id;
    private String hostGroupName;
    private int creatorId;
    private String creatorName;
    private String createTime;
    private String updateTime;

    public HostGroupInfoBasic() {
    }

    public HostGroupInfoBasic(int id, String hostGroupName, int creatorId, String creatorName, String createTime, String updateTime) {
        this.id = id;
        this.hostGroupName = hostGroupName;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHostGroupName() {
        return hostGroupName;
    }

    public void setHostGroupName(String hostGroupName) {
        this.hostGroupName = hostGroupName;
    }

    public int getCreatorId() {
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

    public String checkLegality() {
        if (StringUtils.isBlank(hostGroupName)) {
            return "host group name is blank";
        }
        return null;
    }
}
