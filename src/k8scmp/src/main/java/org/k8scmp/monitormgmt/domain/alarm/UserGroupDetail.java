package org.k8scmp.monitormgmt.domain.alarm;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.related.UserInfo;

/**
 * Created by KaiRen on 2016/9/27.
 */
public class UserGroupDetail {
    private int id;
    private String userGroupName;
    private int creatorId;
    private String creatorName;
    private String createTime;
    private String updateTime;
    private List<User> userList;
    private List<TemplateInfoBasic> templateList;

    public UserGroupDetail() {
    }

    public UserGroupDetail(UserGroupBasic userGroupBasic) {
        this.id = userGroupBasic.getId();
        this.userGroupName = userGroupBasic.getUserGroupName();
        this.creatorId = userGroupBasic.getCreatorId();
        this.creatorName = userGroupBasic.getCreatorName();
        this.createTime = userGroupBasic.getCreateTime();
        this.updateTime = userGroupBasic.getUpdateTime();
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public Integer getCreatorId() {
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

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<TemplateInfoBasic> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<TemplateInfoBasic> templateList) {
        this.templateList = templateList;
    }

    public static class UserGroupDetailComparator implements Comparator<UserGroupDetail> {
        @Override
        public int compare(UserGroupDetail t1, UserGroupDetail t2) {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        	try {
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
//            if (t2.getCreateTime() - t1.getCreateTime() > 0) {
//                return 1;
//            } else if (t2.getCreateTime() - t1.getCreateTime() < 0) {
//                return -1;
//            } else {
//                return 0;
//            }
			return 0;
        }
    }
}
