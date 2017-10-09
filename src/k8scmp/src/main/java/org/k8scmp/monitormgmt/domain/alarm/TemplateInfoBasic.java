package org.k8scmp.monitormgmt.domain.alarm;

/**
 * Created by baokangwang on 2016/3/31.
 */
public class TemplateInfoBasic {

    private int id;
    private String templateName;
    private String templateType;
    private int creatorId;
    private String creatorName;
    private String createTime;
    private String updateTime;
    //private String isRemoved;

    public TemplateInfoBasic() {
    }

    public TemplateInfoBasic(int id, String templateName, String templateType, int creatorId, String creatorName, String createTime, String updateTime) {
        this.id = id;
        this.templateName = templateName;
        this.templateType = templateType;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.updateTime = updateTime;
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
}