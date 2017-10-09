package org.k8scmp.monitormgmt.domain.alarm;

import java.util.List;

import org.k8scmp.login.domain.User;

public class TemplateIn {

    private int id;
    private String templateName;
    private String templateType;
    private int creatorId;
    private String creatorName;
    private String createTime;
    private String updateTime;
    private List<HostGroupInfoBasic> hostGroupList;
    private DeploymentInfo deploymentInfo;
//    private List<StrategyInfo> strategyList;
//    private List<UserGroupInfo> userGroupList;
    private List<User> userList;
    private String[] metricList;
    private int[] pointNumList;
    private String[] aggregateTypeList;
    private String[] operatorList;
    private int[] rightValueList;
    private String[] noteValueList;
    private int[] maxStepList;
    private CallBackInfo callback;
    
    public TemplateIn() {
    }

    public TemplateIn(int id, String templateName, String templateType, int creatorId, String creatorName,
                        String createTime, String updateTime, List<HostGroupInfoBasic> hostGroupList, List<User> userList,
                        DeploymentInfo deploymentInfo, String[] metricList, int[] pointNumList, String[] aggregateTypeList,
                        String[] operatorList, int[] rightValueList, String[] noteValueList, int[] maxStepList, CallBackInfo callback) {
        this.id = id;
        this.templateName = templateName;
        this.templateType = templateType;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.hostGroupList = hostGroupList;
        this.deploymentInfo = deploymentInfo;
        this.userList = userList;
        this.metricList = metricList;
        this.pointNumList = pointNumList;
        this.aggregateTypeList = aggregateTypeList;
        this.operatorList = operatorList;
        this.rightValueList = rightValueList;
        this.noteValueList = noteValueList;
        this.maxStepList = maxStepList;
        this.callback = callback;
    }
    
    public String[] getMetricList() {
		return metricList;
	}

	public void setMetricList(String[] metricList) {
		this.metricList = metricList;
	}

	public int[] getPointNumList() {
		return pointNumList;
	}

	public void setPointNumList(int[] pointNumList) {
		this.pointNumList = pointNumList;
	}

	public String[] getAggregateTypeList() {
		return aggregateTypeList;
	}

	public void setAggregateTypeList(String[] aggregateTypeList) {
		this.aggregateTypeList = aggregateTypeList;
	}

	public String[] getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(String[] operatorList) {
		this.operatorList = operatorList;
	}

	public int[] getRightValueList() {
		return rightValueList;
	}

	public void setRightValueList(int[] rightValueList) {
		this.rightValueList = rightValueList;
	}

	public String[] getNoteValueList() {
		return noteValueList;
	}

	public void setNoteValueList(String[] noteValueList) {
		this.noteValueList = noteValueList;
	}

	public int[] getMaxStepList() {
		return maxStepList;
	}

	public void setMaxStepList(int[] maxStepList) {
		this.maxStepList = maxStepList;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
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

    public List<HostGroupInfoBasic> getHostGroupList() {
        return hostGroupList;
    }

    public void setHostGroupList(List<HostGroupInfoBasic> hostGroupList) {
        this.hostGroupList = hostGroupList;
    }

    public DeploymentInfo getDeploymentInfo() {
        return deploymentInfo;
    }

    public void setDeploymentInfo(DeploymentInfo deploymentInfo) {
        this.deploymentInfo = deploymentInfo;
    }

    public List<User> getUserList() {
	      return userList;
	  }
	
	  public void setUserList(List<User> userList) {
	      this.userList = userList;
	  }
    
    public CallBackInfo getCallback() {
        return callback;
    }

    public void setCallback(CallBackInfo callback) {
        this.callback = callback;
    }

}
