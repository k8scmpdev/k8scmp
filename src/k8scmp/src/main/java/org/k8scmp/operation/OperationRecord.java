package org.k8scmp.operation;


import java.io.Serializable;

import org.k8scmp.basemodel.ResourceType;

/**
 */
public class OperationRecord implements Serializable {
	private String id;
    private String resourceId;
    private ResourceType resourceType;
    private OperationType Operation;
    private String userId;
    private String userName = "";
    private String status = "";
    private String message = "";
    private String operateTime;

    public OperationRecord() {
    }

    public OperationRecord(String resourceId, ResourceType resourceType, OperationType operation,
                           String userId, String userName, String status, String message, String operateTime) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        Operation = operation;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
        this.message = message;
        this.operateTime = operateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OperationType getOperation() {
        return Operation;
    }

    public void setOperation(OperationType operation) {
        Operation = operation;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
