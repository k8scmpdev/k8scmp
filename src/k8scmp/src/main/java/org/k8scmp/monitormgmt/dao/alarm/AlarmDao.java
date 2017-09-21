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

	void addTemplateHostGroupBind(long templateId, long id, long current);

	void setTemplateDeployIdByTemplateId(long templateId, int id);


	void addStrategyInfo(StrategyInfo strategyInfo);

	void addTemplateUserGroupBind(long templateId, int id, long current);

	void addCallBackInfo(CallBackInfo callbackInfo);

	void setTemplateCallbackIdByTemplateId(long templateId, long id);

	List<TemplateInfoBasic> listTemplateInfoBasic();

	User getUserById(long userId);

	TemplateInfoBasic getTemplateInfoBasicById(long id);

	void updateTemplateInfoBasicById(TemplateInfoBasic updatedTemplateInfoBasic);

	List<AlarmEventInfoDraft> listAlarmEventInfoDraft();

	List<HostGroupInfoBasic> listHostGroupInfoBasicByTemplateId(long id);

	List<StrategyInfo> listStrategyInfoByTemplateId(long id);

	List<Long> listUserIdByTemplateId(long id);

	CallBackInfo getCallbackInfoByTemplateId(long id);

	void deleteTemplateHostGroupBindByTemplateId(long templateId);

	void deleteStrategyInfoByTemplateId(long templateId);

	void deleteTemplateUserBindByTemplateId(long templateId);

	void deleteCallbackInfoByTemplateId(long templateId);

	void deleteTemplateInfoBasicById(long id);

	List<HostGroupInfoBasic> listHostGroupInfoBasic();

	HostGroupInfoBasic getHostGroupInfoBasicByName(String hostGroupName);

	void addHostGroupInfoBasic(HostGroupInfoBasic hostGroupInfoBasic);

	HostGroupInfoBasic getHostGroupInfoBasicById(long id);

	void updateHostGroupInfoBasicById(HostGroupInfoBasic updatedHostGroupInfoBasic);

	void deleteTemplateHostGroupBindByHostGroupId(long id);

	void deleteHostGroupInfoBasicById(long id);

	void deleteHostGroupHostBindByHostGroupId(long id);

	HostInfo getHostInfoById(long id);

	Long getHostGroupHostBindTime(long id, long hostId);

	void updateHostGroupHostBind(long id, long hostId, long bindTime);

	void addHostGroupHostBind(long id, long hostId, long bindTime);

	void deleteHostGroupHostBind(long id, long hostId);

	void addHostInfo(HostInfo hostInfo);

	List<HostInfo> getHostInfoByHostGroupId(long hostGroupId);

	List<TemplateInfoBasic> getTemplateInfoBasicByHostGroupId(long hostGroupId);







	
	
}
