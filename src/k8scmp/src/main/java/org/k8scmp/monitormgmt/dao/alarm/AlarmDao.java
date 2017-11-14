package org.k8scmp.monitormgmt.dao.alarm;

import java.util.List;

import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.related.UserInfo;
import org.k8scmp.monitormgmt.domain.alarm.AlarmEventInfoDraft;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;
import org.k8scmp.monitormgmt.domain.alarm.DeploymentInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.HostInfo;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupBasic;
import org.k8scmp.monitormgmt.domain.alarm.asist.Link;

/**
 * Created by baokangwang on 2016/4/13.
 */
public interface AlarmDao {

	TemplateInfoBasic getTemplateInfoBasicByName(String templateName);

	void addTemplateInfoBasic(TemplateInfoBasic templateInfoBasic);

	void addTemplateHostGroupBind(int templateId, int id, String current);

	void setTemplateDeployIdByTemplateId(int templateId, String deploymentName);


	void addStrategyInfo(StrategyInfo strategyInfo);

	void addTemplateUserGroupBind(int templateId, int id, String current);

	void addCallBackInfo(CallBackInfo callbackInfo);

	void setTemplateCallbackIdByTemplateId(int templateId, int id);

	List<TemplateInfoBasic> listTemplateInfoBasic();

	User getUserById(int userId);

	TemplateInfoBasic getTemplateInfoBasicById(int id);

	void updateTemplateInfoBasicById(TemplateInfoBasic updatedTemplateInfoBasic);

	List<AlarmEventInfoDraft> listAlarmEventInfoDraft();

	List<HostGroupInfoBasic> listHostGroupInfoBasicByTemplateId(int id);

	List<StrategyInfo> listStrategyInfoByTemplateId(int id);

	List<Integer> listUserIdByTemplateId(int id);

	CallBackInfo getCallbackInfoByTemplateId(int id);

	void deleteTemplateHostGroupBindByTemplateId(int templateId);

	void deleteStrategyInfoByTemplateId(int templateId);

	void deleteTemplateUserBindByTemplateId(int templateId);

	void deleteCallbackInfoByTemplateId(int templateId);

	void deleteTemplateInfoBasicById(int id);

	List<HostGroupInfoBasic> listHostGroupInfoBasic();

	HostGroupInfoBasic getHostGroupInfoBasicByName(String hostGroupName);

	int addHostGroupInfoBasic(HostGroupInfoBasic hostGroupInfoBasic);

	HostGroupInfoBasic getHostGroupInfoBasicById(int id);

	void updateHostGroupInfoBasicById(HostGroupInfoBasic updatedHostGroupInfoBasic);

	void deleteTemplateHostGroupBindByHostGroupId(int id);

	void deleteHostGroupInfoBasicById(int id);

	void deleteHostGroupHostBindByHostGroupId(int id);

	HostInfo getHostInfoById(int id);

	Long getHostGroupHostBindTime(int id, int hostId);

	void updateHostGroupHostBind(int id, int hostId, String bindTime);

	void addHostGroupHostBind(int id, int hostId, String bindTime);

	void deleteHostGroupHostBind(int id, int hostId);

	void addHostInfo(HostInfo hostInfo);

	List<HostInfo> getHostInfoByHostGroupId(int hostGroupId);

	List<TemplateInfoBasic> getTemplateInfoBasicByHostGroupId(int hostGroupId);

	List<HostGroupInfoBasic> listHostGroupInfoBasicByName(String hostGroupName);

	List<TemplateInfoBasic> getTemplateInfoByName(String templateName);

	ServiceInfo getDeploymentByTemplateId(int id);

	HostInfo getHostInfoByHostname(String endpoint);

	void addLink(Link link);

	Link getLinkById(int linkId);

	List<UserGroupBasic> listUserGroupInfoBasic();

	UserGroupBasic getUserGroupInfoBasicByName(String userGroupName);

	void addUserGroupInfoBasic(UserGroupBasic userGroupBasic);

	UserGroupBasic getUserGroupInfoBasicById(int id);

	void updateUserGroupInfoBasicById(UserGroupBasic updatedUserGroupBasic);

	void deleteUserGroupUserBindByUserGroupId(int id);

	void deleteUserGroupInfoBasicById(int id);

	String getUserGroupUserBindTime(int id, int userId);

	void updateUserGroupUserBind(int id, int userId, String currentTimeMillis);

	void addUserGroupUserBind(int id, int userId, String currentTimeMillis);

	void deleteUserGroupUserBind(int id, int userId);

	List<User> getUserInfoByUserGroupId(int userGroupId);

	List<TemplateInfoBasic> getTemplateInfoBasicByUserGroupId(int userGroupId);

	void deleteTemplateStrategyBindByTemplateId(int templateId);

	void addTemplateStrategyBind(int templateId, int strategyId, String current);

	List<Integer> listUserGroupIdByTemplateId(int id);

}
