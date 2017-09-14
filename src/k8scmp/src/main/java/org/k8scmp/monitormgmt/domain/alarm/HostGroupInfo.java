package org.k8scmp.monitormgmt.domain.alarm;

import org.k8scmp.util.StringUtils;

public class HostGroupInfo {
	private long id;
	private String hostGroupName;
	private long creatorId;
	private String creatorName;
    private String hostname;
    private String ip;
    private String cluster;
    private long createTime;
    private long updateTime;
    
    public HostGroupInfo(){
    	
    }
	public HostGroupInfo(long id, String hostGroupName, long creatorId, String creatorName, String hostname, String ip,
			String cluster, long createTime, long updateTime) {
		this.id = id;
		this.hostGroupName = hostGroupName;
		this.creatorId = creatorId;
		this.creatorName = creatorName;
		this.hostname = hostname;
		this.ip = ip;
		this.cluster = cluster;
		this.createTime = createTime;
		this.updateTime = updateTime;
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
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
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
        if (StringUtils.isBlank(hostname)) {
            return "hostname is blank";
        }
        if (StringUtils.isBlank(ip)) {
            return "ip is blank";
        }
        if (StringUtils.isBlank(cluster)) {
            return "cluster is blank";
        }
        return null;
    }
}
