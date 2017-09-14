package org.k8scmp.monitormgmt.domain.alarm;

import org.k8scmp.util.StringUtils;

/**
 * Created by baokangwang on 2016/3/31.
 */
public class HostGroupInfoBasic {

    private long id;
    private String hostGroupName;
    private long creatorId;
    private String creatorName;
    private long createTime;
    private long updateTime;
    private String hostName;
    private String IP;
    private String cluster;
    

    public HostGroupInfoBasic(long id, String hostGroupName, long creatorId, String creatorName, long createTime, long updateTime,
    		String hostName,String IP,String cluster) {
        this.id = id;
        this.hostGroupName = hostGroupName;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.hostName = hostName;
        this.IP = IP;
        this.cluster = cluster;
    }
    
    public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public HostGroupInfoBasic() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHostGroupName() {
        return hostGroupName;
    }

    public void setHostGroupName(String hostGroupName) {
        this.hostGroupName = hostGroupName;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
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

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String checkLegality() {
        if (StringUtils.isBlank(hostGroupName)) {
            return "host group name is blank";
        }
        return null;
    }
}
