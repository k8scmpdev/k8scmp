package org.k8scmp.monitormgmt.dao.alarm.impl;

import java.util.List;

import org.k8scmp.login.domain.User;
import org.k8scmp.mapper.login.UserMapper;
import org.k8scmp.mapper.monitor.AlarmEventInfoMapper;
import org.k8scmp.mapper.monitor.CallbackInfoMapper;
import org.k8scmp.mapper.monitor.HostGroupHostBindMapper;
import org.k8scmp.mapper.monitor.HostGroupInfoBasicMapper;
import org.k8scmp.mapper.monitor.HostInfoMapper;
import org.k8scmp.mapper.monitor.StrategyInfoMapper;
import org.k8scmp.mapper.monitor.TemplateHostGroupBindMapper;
import org.k8scmp.mapper.monitor.TemplateInfoBasicMapper;
import org.k8scmp.mapper.monitor.TemplateUserBindMapper;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.domain.alarm.AlarmEventInfoDraft;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.HostInfo;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by baokangwang on 2016/4/13.
 */
@Service("alarmDao")
public class AlarmDaoImpl implements AlarmDao{
	
	@Autowired
	TemplateInfoBasicMapper templateInfoBasicMapper;
	@Autowired
    TemplateHostGroupBindMapper templateHostGroupBindMapper;
	@Autowired
    StrategyInfoMapper strategyInfoMapper;
	@Autowired
    TemplateUserBindMapper templateUserBindMapper;
	@Autowired
    CallbackInfoMapper callbackInfoMapper;
	@Autowired
	UserMapper userMapper;
	@Autowired
	AlarmEventInfoMapper alarmEventInfoMapper;
	@Autowired
	HostGroupInfoBasicMapper hostGroupInfoBasicMapper;
	@Autowired
	HostGroupHostBindMapper hostGroupHostBindMapper;
	@Autowired
	HostInfoMapper hostInfoMapper;
	
	@Override
	public TemplateInfoBasic getTemplateInfoBasicByName(String templateName) {
		return templateInfoBasicMapper.getTemplateInfoBasicByName(templateName);
	}

	@Override
	public void addTemplateInfoBasic(TemplateInfoBasic templateInfoBasic) {
		templateInfoBasicMapper.addTemplateInfoBasic(templateInfoBasic);
	}

	@Override
	public void addTemplateHostGroupBind(long templateId, long hostGroupId, long bindTime) {
		templateHostGroupBindMapper.addTemplateHostGroupBind(templateId, hostGroupId, bindTime);
	}

	@Override
	public void setTemplateDeployIdByTemplateId(long templateId, int deployId) {
		templateInfoBasicMapper.setTemplateDeployIdByTemplateId(templateId, deployId);
	}

	@Override
	public void addStrategyInfo(StrategyInfo strategyInfo) {
		strategyInfoMapper.addStrategyInfo(strategyInfo);
	}

	@Override
	public void addTemplateUserGroupBind(long templateId, int userId, long bindTime) {
		templateUserBindMapper.addTemplateUserGroupBind(templateId, userId, bindTime);
	}

	@Override
	public void addCallBackInfo(CallBackInfo callbackInfo) {
		callbackInfoMapper.addCallBackInfo(callbackInfo);
	}

	@Override
	public void setTemplateCallbackIdByTemplateId(long templateId, long callbackId) {
		templateInfoBasicMapper.setTemplateCallbackIdByTemplateId(templateId, callbackId);
	}

	@Override
	public List<TemplateInfoBasic> listTemplateInfoBasic() {
		return templateInfoBasicMapper.listTemplateInfoBasic();
	}

	@Override
	public User getUserById(long id) {
		return userMapper.getUserById(id);
	}

	@Override
	public TemplateInfoBasic getTemplateInfoBasicById(long id) {
		return templateInfoBasicMapper.getTemplateInfoBasicById(id);
	}

	@Override
	public void updateTemplateInfoBasicById(TemplateInfoBasic templateInfoBasic) {
		templateInfoBasicMapper.updateTemplateInfoBasicById(templateInfoBasic);
	}

	@Override
	public List<AlarmEventInfoDraft> listAlarmEventInfoDraft() {
		return alarmEventInfoMapper.listAlarmEventInfoDraft();
	}

	@Override
	public List<HostGroupInfoBasic> listHostGroupInfoBasicByTemplateId(long templateId) {
		return hostGroupInfoBasicMapper.listHostGroupInfoBasicByTemplateId(templateId);
	}

	@Override
	public List<StrategyInfo> listStrategyInfoByTemplateId(long templateId) {
		return strategyInfoMapper.listStrategyInfoByTemplateId(templateId);
	}

	@Override
	public List<Long> listUserIdByTemplateId(long templateId) {
		return templateUserBindMapper.listUserIdByTemplateId(templateId);
	}

	@Override
	public CallBackInfo getCallbackInfoByTemplateId(long templateId) {
		return callbackInfoMapper.getCallbackInfoByTemplateId(templateId);
	}

	@Override
	public void deleteTemplateHostGroupBindByTemplateId(long templateId) {
		templateHostGroupBindMapper.deleteTemplateHostGroupBindByTemplateId(templateId);
	}

	@Override
	public void deleteStrategyInfoByTemplateId(long templateId) {
		strategyInfoMapper.deleteStrategyInfoByTemplateId(templateId);
	}

	@Override
	public void deleteTemplateUserBindByTemplateId(long templateId) {
		templateUserBindMapper.deleteTemplateUserBindByTemplateId(templateId);
	}

	@Override
	public void deleteCallbackInfoByTemplateId(long templateId) {
		callbackInfoMapper.deleteCallbackInfoByTemplateId(templateId);
	}

	@Override
	public void deleteTemplateInfoBasicById(long id) {
		templateInfoBasicMapper.deleteTemplateInfoBasicById(id);
	}

	@Override
	public List<HostGroupInfoBasic> listHostGroupInfoBasic() {
		
		return hostGroupInfoBasicMapper.listHostGroupInfoBasic();
	}

	@Override
	public HostGroupInfoBasic getHostGroupInfoBasicByName(String hostGroupName) {
		return hostGroupInfoBasicMapper.getHostGroupInfoBasicByName(hostGroupName);
	}

	@Override
	public void addHostGroupInfoBasic(HostGroupInfoBasic hostGroupInfoBasic) {
		hostGroupInfoBasicMapper.addHostGroupInfoBasic(hostGroupInfoBasic);
	}

	@Override
	public HostGroupInfoBasic getHostGroupInfoBasicById(long id) {
		return hostGroupInfoBasicMapper.getHostGroupInfoBasicById(id);
	}

	@Override
	public void updateHostGroupInfoBasicById(HostGroupInfoBasic updatedHostGroupInfoBasic) {
		hostGroupInfoBasicMapper.updateHostGroupInfoBasicById(updatedHostGroupInfoBasic);
	}

	@Override
	public void deleteTemplateHostGroupBindByHostGroupId(long id) {
		templateHostGroupBindMapper.deleteTemplateHostGroupBindByHostGroupId(id);
	}

	@Override
	public void deleteHostGroupInfoBasicById(long id) {
		hostGroupInfoBasicMapper.deleteHostGroupInfoBasicById(id);
	}

	@Override
	public void deleteHostGroupHostBindByHostGroupId(long id) {
		hostGroupHostBindMapper.deleteHostGroupHostBindByHostGroupId(id);
	}

	@Override
	public HostInfo getHostInfoById(long id) {
		return hostInfoMapper.getHostInfoById(id);
	}

	@Override
	public Long getHostGroupHostBindTime(long hostGroupId, long hostId) {
		return hostGroupHostBindMapper.getHostGroupHostBindTime(hostGroupId, hostId);
	}

	@Override
	public void updateHostGroupHostBind(long hostGroupId, long hostId, long bindTime) {
		hostGroupHostBindMapper.updateHostGroupHostBind(hostGroupId, hostId, bindTime);
	}

	@Override
	public void addHostGroupHostBind(long hostGroupId, long hostId, long bindTime) {
		hostGroupHostBindMapper.addHostGroupHostBind(hostGroupId, hostId, bindTime);
	}

	@Override
	public void deleteHostGroupHostBind(long hostGroupId, long hostId) {
		hostGroupHostBindMapper.deleteHostGroupHostBind(hostGroupId, hostId);
	}

	@Override
	public void addHostInfo(HostInfo hostInfo) {
		hostInfoMapper.addHostInfo(hostInfo);
	}

	@Override
	public List<HostInfo> getHostInfoByHostGroupId(long hostGroupId) {
		return hostInfoMapper.getHostInfoByHostGroupId(hostGroupId);
	}

	@Override
	public List<TemplateInfoBasic> getTemplateInfoBasicByHostGroupId(long hostGroupId) {
		return templateInfoBasicMapper.getTemplateInfoBasicByHostGroupId(hostGroupId);
	}


	

	
}
