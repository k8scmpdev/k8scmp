package org.k8scmp.monitormgmt.domain.alarm;

import org.k8scmp.util.StringUtils;

/**
 * Created by baokangwang on 2016/3/31.
 */
public class HostInfo {

    private int id;
    private String hostname;
    private String ip;
    private String cluster;
    private String createTime;

    public HostInfo() {
    }

    public HostInfo(int id, String hostname, String ip, String cluster, String createTime) {
        this.id = id;
        this.hostname = hostname;
        this.ip = ip;
        this.cluster = cluster;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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