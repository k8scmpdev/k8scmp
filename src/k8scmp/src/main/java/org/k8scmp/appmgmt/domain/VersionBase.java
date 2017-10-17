package org.k8scmp.appmgmt.domain;

import java.util.HashSet;
import java.util.Set;

import org.k8scmp.engine.model.DataModelBase;

public class VersionBase extends DataModelBase{
	
	@Override
    public Set<String> excludeForJSON() {
        return toExclude;
    }

    public static Set<String> toExclude = new HashSet<String>() {{
        add("id");
        add("versionName");
        add("description");
        add("state");
        add("createTime");
        add("creatorId");
        add("lastModifiedTime");
        add("lastModifierId");
        add("data");
    }};
	    
    private String id;

    private String versionName;
    
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

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName == null ? null : versionName.trim();
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
	
	
	public <T extends VersionBase> T toModel(Class<T> clazz){
		try{
			T result = clazz.newInstance();
	        if (data != null && data.length() != 0) {
	            result = result.fromString(data);
	        }
	        
	        result.setId(id);
	        result.setVersionName(versionName);
	        result.setDescription(description);
	        result.setState(state);
	        result.setCreateTime(createTime);
	        result.setCreatorId(creatorId);
	        result.setLastModifiedTime(lastModifiedTime);
	        result.setLastModifierId(lastModifierId);
	        return result;
		}catch(Exception e){
			
		}
		return null;
    }
}