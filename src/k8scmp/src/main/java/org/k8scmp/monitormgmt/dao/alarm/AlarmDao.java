package org.k8scmp.monitormgmt.dao.alarm;

import java.util.List;

import org.k8scmp.login.domain.User;
import org.k8scmp.monitormgmt.domain.alarm.AlarmEventInfoDraft;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.HostInfo;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;

/**
 * Created by baokangwang on 2016/4/13.
 */
public interface AlarmDao {

	TemplateInfoBasic getTemplateInfoBasicByName(String templateName);

	void addTemplateInfoBasic(TemplateInfoBasic templateInfoBasic);

	void addTemplateHostGroupBind(int templateId, int id, String current);

	void setTemplateDeployIdByTemplateId(int templateId, int id);


	void addStrategyInfo(StrategyInfo strategyInfo);

	void addTemplateUserGroupBind(int templateId, int id, String current);

	void addCallBackInfo(CallBackInfo callbackInfo);

	void setTemplateCallbackIdByTemplateId(int templateId, int id);

	List<TemplateInfoBasic> listTemplateInfoBasic();

	User getUserById(long userId);

	TemplateInfoBasic getTemplateInfoBasicById(int id);

	void updateTemplateInfoBasicById(TemplateInfoBasic updatedTemplateInfoBasic);

	List<AlarmEventInfoDraft> listAlarmEventInfoDraft();

	List<HostGroupInfoBasic> listHostGroupInfoBasicByTemplateId(int id);

	List<StrategyInfo> listStrategyInfoByTemplateId(int id);

	List<Long> listUserIdByTemplateId(int id);

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









	
	
}
