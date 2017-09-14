package org.k8scmp.monitormgmt.dao.alarm.impl;

import java.util.List;

import org.k8scmp.login.domain.User;
import org.k8scmp.mapper.login.UserMapper;
import org.k8scmp.mapper.monitor.AlarmEventInfoMapper;
import org.k8scmp.mapper.monitor.CallbackInfoMapper;
import org.k8scmp.mapper.monitor.HostGroupInfoBasicMapper;
import org.k8scmp.mapper.monitor.StrategyInfoMapper;
import org.k8scmp.mapper.monitor.TemplateHostGroupBindMapper;
import org.k8scmp.mapper.monitor.TemplateInfoBasicMapper;
import org.k8scmp.mapper.monitor.TemplateUserBindMapper;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.domain.alarm.AlarmEventInfoDraft;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
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
	public User getUserById(int id) {
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
	public List<Integer> listUserIdByTemplateId(int templateId) {
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

	

	
}
