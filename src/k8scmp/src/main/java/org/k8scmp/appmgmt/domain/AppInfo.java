package org.k8scmp.appmgmt.domain;

import java.util.Comparator;

/**
 * Created by KaiRen on 2016/10/13.
 */
public class AppInfo {
    private String name;
    private int id;
    private String description;
    private int creatorId;
    private String creatorName;
    private long createTime;
    private int memberCount;
    private int deployCount;

    public AppInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getDeployCount() {
        return deployCount;
    }

    public void setDeployCount(int deployCount) {
        this.deployCount = deployCount;
    }


    public static class DeployCollectionInfoListComparator implements Comparator<AppInfo> {
        @Override
        public int compare(AppInfo t1, AppInfo t2) { 
            if (t2.getCreateTime() - t1.getCreateTime() > 0) {
                return 1;
            } else if (t2.getCreateTime() - t1.getCreateTime() < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
