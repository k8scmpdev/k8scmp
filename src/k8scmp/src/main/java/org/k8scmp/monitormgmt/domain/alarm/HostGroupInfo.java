package org.k8scmp.monitormgmt.domain.alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.k8scmp.util.DateUtil;


public class HostGroupInfo {

    private int id;
    private String hostGroupName;
    private int creatorId;
    private String creatorName;
    private String createTime;
    private String updateTime;
    private List<HostInfo> hostList;
    private List<TemplateInfoBasic> templateList;

    public HostGroupInfo() {
    }

    public HostGroupInfo(int id, String hostGroupName, int creatorId, String creatorName, String createTime,
                         String updateTime, List<HostInfo> hostList, List<TemplateInfoBasic> templateList) {
        this.id = id;
        this.hostGroupName = hostGroupName;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.hostList = hostList;
        this.templateList = templateList;
    }

    public HostGroupInfo(HostGroupInfoBasic hostGroupInfoBasic) {
        this.id = hostGroupInfoBasic.getId();
        this.hostGroupName = hostGroupInfoBasic.getHostGroupName();
        this.creatorId = hostGroupInfoBasic.getCreatorId();
        this.creatorName = hostGroupInfoBasic.getCreatorName();
        this.createTime = hostGroupInfoBasic.getCreateTime();
        this.updateTime = hostGroupInfoBasic.getUpdateTime();
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

    public List<HostInfo> getHostList() {
        return hostList;
    }

    public void setHostList(List<HostInfo> hostList) {
        this.hostList = hostList;
    }

    public List<TemplateInfoBasic> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<TemplateInfoBasic> templateList) {
        this.templateList = templateList;
    }

    public static class HostGroupInfoComparator implements Comparator<HostGroupInfo> {
        @Override
        public int compare(HostGroupInfo t1, HostGroupInfo t2) {
            try {
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            	long T1 = sdf.parse(t1.getCreateTime()).getTime();
            	long T2 = sdf.parse(t2.getCreateTime()).getTime();
				if ((T2 - T1) > 0) {
				    return 1;
				} else if (T2 - T1 < 0) {
				    return -1;
				} else {
				    return 0;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
            return 0;
        }
    }
}
