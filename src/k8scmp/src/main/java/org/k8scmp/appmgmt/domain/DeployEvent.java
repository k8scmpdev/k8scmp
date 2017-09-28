package org.k8scmp.appmgmt.domain;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.k8scmp.engine.model.DataModelBase;
import org.k8scmp.login.domain.User;
import org.k8scmp.model.DeployEventStatus;
import org.k8scmp.model.DeployOperation;
import org.k8scmp.util.DateUtil;

/**
 */
public class DeployEvent extends DataModelBase {
    @Override
    public Set<String> excludeForJSON() {
        return toExclude;
    }

    public static Set<String> toExclude = new HashSet<String>() {{
        add("id");
        add("serviceId");
        add("operation");
        add("state");
        add("content");
        add("expireTime");
    }};

    String id;
    String serviceId;
    DeployOperation operation;
    DeployEventStatus state;
    String expireTime;
    String content;

	String startTime;
	String lastModifiedTime;
	String operatorId;
    String userName;
    String message;
    List<DeploymentSnapshot> primarySnapshot;
    List<DeploymentSnapshot> targetSnapshot;
    List<DeploymentSnapshot> currentSnapshot;

    public DeployEvent() {
    }

    public DeployEvent(String serviceId, DeployOperation operation, DeployEventStatus eventStatus, User user,
                       List<DeploymentSnapshot> primarySnapshot, List<DeploymentSnapshot> targetSnapshot, List<DeploymentSnapshot> currentSnapshot) {
        this.serviceId = serviceId;
        this.operation = operation;
        this.state = eventStatus;
        this.startTime = DateUtil.dateFormatToMillis(new Date());
        this.expireTime = this.startTime;
        this.operatorId = user.getLoginname();
        this.userName = user.getUsername();
        this.primarySnapshot = primarySnapshot;
        this.targetSnapshot = targetSnapshot;
        this.currentSnapshot = currentSnapshot;
    }

    public List<DeploymentSnapshot> getCurrentSnapshot() {
        return currentSnapshot;
    }

    public void setCurrentSnapshot(List<DeploymentSnapshot> currentSnapshot) {
        this.currentSnapshot = currentSnapshot;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeployEventStatus getState() {
        return state;
    }

    public void setState(DeployEventStatus state) {
        this.state = state;
    }


    public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DeployOperation getOperation() {
        return operation;
    }

    public void setOperation(DeployOperation operation) {
        this.operation = operation;
    }

    public List<DeploymentSnapshot> getPrimarySnapshot() {
        return primarySnapshot;
    }

    public void setPrimarySnapshot(List<DeploymentSnapshot> primarySnapshot) {
        this.primarySnapshot = primarySnapshot;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public List<DeploymentSnapshot> getTargetSnapshot() {
        return targetSnapshot;
    }

    public void setTargetSnapshot(List<DeploymentSnapshot> targetSnapshot) {
        this.targetSnapshot = targetSnapshot;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DeployEvent toModel(){
    	DeployEvent result = new DeployEvent();
        if (content != null && content.length() != 0) {
        	result = result.fromString(content);
        }
        result.setId(id);
        result.setServiceId(serviceId);
        result.setOperation(operation);
        result.setState(state);
        return result;
    }
    
    public boolean eventTerminated() {
        if (getState() == null) {
            return false;
        }
        return DeployEventStatus.FAILED.equals(state) || DeployEventStatus.SUCCESS.equals(state)
                || DeployEventStatus.ABORTED.equals(state);
    }
}
