package org.k8scmp.appmgmt.domain;

import java.util.HashSet;
import java.util.Set;

import org.k8scmp.engine.model.DataModelBase;

public class ServiceInfo extends DataModelBase{
	
	@Override
    public Set<String> excludeForJSON() {
        return toExclude;
    }

    public static Set<String> toExclude = new HashSet<String>() {{
        add("id");
        add("serviceCode");
        add("startSeq");
        add("appId");
        add("appCode");
        add("description");
        add("state");
        add("createTime");
        add("creatorId");
        add("lastModifiedTime");
        add("lastModifierId");
        add("data");
    }};
	    
    private String id;

    private String serviceCode;
    
    private int startSeq;
    
    private String appId;
    
    private String appCode;

    private String description;

    private String state;

    private String createTime;

    private String creatorId;

    private String lastModifiedTime;

    private String lastModifierId;
    
    private String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode == null ? null : serviceCode.trim();
    }
    
    public int getStartSeq() {
		return startSeq;
	}

	public void setStartSeq(int startSeq) {
		this.startSeq = startSeq;
	}
	
    public String getAppId() {
    	return appId;
    }
    
    public void setAppId(String appId) {
    	this.appId = appId == null ? null : appId.trim();
    }
    
    public String getAppCode() {
    	return appCode;
    }
    
    public void setAppCode(String appCode) {
    	this.appCode = appCode == null ? null : appCode.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId == null ? null : creatorId.trim();
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime == null ? null : lastModifiedTime.trim();
    }

    public String getLastModifierId() {
        return lastModifierId;
    }

    public void setLastModifierId(String lastModifierId) {
        this.lastModifierId = lastModifierId == null ? null : lastModifierId.trim();
    }

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
	public <T extends ServiceInfo> T toModel(Class<T> clazz) throws Exception{
        T result = clazz.newInstance();
        if (data != null && data.length() != 0) {
            result = result.fromString(data);
        }
        
        result.setId(id);
        result.setServiceCode(serviceCode);
        result.setStartSeq(startSeq);
        result.setAppId(appId);
        result.setAppCode(appCode);
        result.setDescription(description);
        result.setState(state);
        result.setCreateTime(createTime);
        result.setCreatorId(creatorId);
        result.setLastModifiedTime(lastModifiedTime);
        result.setLastModifierId(lastModifierId);
        return result;
    }
}