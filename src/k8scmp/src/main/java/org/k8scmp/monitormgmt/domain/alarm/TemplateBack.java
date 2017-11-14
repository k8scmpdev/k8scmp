package org.k8scmp.monitormgmt.domain.alarm;


import java.util.List;

import org.k8scmp.login.domain.User;
import org.k8scmp.util.StringUtils;

/**
 * Created by baokangwang on 2016/3/31.
 */
public class TemplateBack {

    private int id;
    private String templateName;
    private String templateType;
    private int creatorId;
    private String creatorName;
    private String createTime;
    private String updateTime;
    private List<HostGroupInfoBasic> hostGroupList;
    private List<DeploymentInfo> deploymentInfos;
    private List<StrategyInfo> strategyList;
    private List<UserGroupInfo> userGroupList;
//    private List<User> userList;
    private String callback;

    public TemplateBack() {
    }

    public TemplateBack(int id, String templateName, String templateType, int creatorId, String creatorName,
                        String createTime, String updateTime, List<HostGroupInfoBasic> hostGroupList, List<UserGroupInfo> userGroupList,
                        List<DeploymentInfo> deploymentInfos, List<StrategyInfo> strategyList, String callback) {
        this.id = id;
        this.templateName = templateName;
        this.templateType = templateType;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.hostGroupList = hostGroupList;
        this.deploymentInfos = deploymentInfos;
        this.strategyList = strategyList;
//        this.userList = userList;
        this.userGroupList = userGroupList;
        this.callback = callback;
    }

    public TemplateBack(TemplateInfoBasic templateInfoBasic) {
        this.id = templateInfoBasic.getId();
        this.templateName = templateInfoBasic.getTemplateName();
        this.templateType = templateInfoBasic.getTemplateType();
//        this.creatorId = templateInfoBasic.getCreatorId();
//        this.creatorName = templateInfoBasic.getCreatorName();
        this.createTime = templateInfoBasic.getCreateTime();
        this.updateTime = templateInfoBasic.getUpdateTime();
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

    public List<DeploymentInfo> getDeploymentInfos() {
        return deploymentInfos;
    }

    public void setDeploymentInfos(List<DeploymentInfo> deploymentInfos) {
        this.deploymentInfos = deploymentInfos;
    }

    public List<StrategyInfo> getStrategyList() {
        return strategyList;
    }

    public void setStrategyList(List<StrategyInfo> strategyList) {
        this.strategyList = strategyList;
    }

    public List<UserGroupInfo> getUserGroupList() {
        return userGroupList;
    }

    public void setUserGroupList(List<UserGroupInfo> userGroupList) {
        this.userGroupList = userGroupList;
    }
    
//    public List<User> getUserList() {
//	      return userList;
//	  }
//	
//	  public void setUserList(List<User> userList) {
//	      this.userList = userList;
//	  }
    
    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

}