package org.k8scmp.monitormgmt.dao.alarm.impl;

import java.util.List;

import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.login.domain.User;
import org.k8scmp.mapper.alarm.AlarmEventInfoMapper;
import org.k8scmp.mapper.alarm.CallbackInfoMapper;
import org.k8scmp.mapper.alarm.HostGroupHostBindMapper;
import org.k8scmp.mapper.alarm.HostGroupInfoBasicMapper;
import org.k8scmp.mapper.alarm.HostInfoMapper;
import org.k8scmp.mapper.alarm.StrategyInfoMapper;
import org.k8scmp.mapper.alarm.TemplateHostGroupBindMapper;
import org.k8scmp.mapper.alarm.TemplateInfoBasicMapper;
import org.k8scmp.mapper.alarm.TemplateUserBindMapper;
import org.k8scmp.mapper.login.UserMapper;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.domain.alarm.AlarmEventInfoDraft;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;
import org.k8scmp.monitormgmt.domain.alarm.DeploymentInfo;
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
	@Autowired
	ServiceDao serviceDao;
	
	@Override
	public TemplateInfoBasic getTemplateInfoBasicByName(String templateName) {
		return templateInfoBasicMapper.getTemplateInfoBasicByName(templateName);
	}

	@Override
	public void addTemplateInfoBasic(TemplateInfoBasic templateInfoBasic) {
		templateInfoBasicMapper.addTemplateInfoBasic(templateInfoBasic);
	}

	@Override
	public void addTemplateHostGroupBind(int templateId, int hostGroupId, String bindTime) {
		templateHostGroupBindMapper.addTemplateHostGroupBind(templateId, hostGroupId, bindTime);
	}

	@Override
	public void setTemplateDeployIdByTemplateId(int templateId, String deploymentName) {
		templateInfoBasicMapper.setTemplateDeployIdByTemplateId(templateId, deploymentName);
	}

	@Override
	public void addStrategyInfo(StrategyInfo strategyInfo) {
		strategyInfoMapper.addStrategyInfo(strategyInfo);
	}

	@Override
	public void addTemplateUserGroupBind(int templateId, int userId, String bindTime) {
		templateUserBindMapper.addTemplateUserGroupBind(templateId, userId, bindTime);
	}

	@Override
	public void addCallBackInfo(CallBackInfo callbackInfo) {
		callbackInfoMapper.addCallBackInfo(callbackInfo);
	}

	@Override
	public void setTemplateCallbackIdByTemplateId(int templateId, int callbackId) {
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
	public TemplateInfoBasic getTemplateInfoBasicById(int id) {
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
	public List<HostGroupInfoBasic> listHostGroupInfoBasicByTemplateId(int templateId) {
		return hostGroupInfoBasicMapper.listHostGroupInfoBasicByTemplateId(templateId);
	}

	@Override
	public List<StrategyInfo> listStrategyInfoByTemplateId(int templateId) {
		return strategyInfoMapper.listStrategyInfoByTemplateId(templateId);
	}

	@Override
	public List<Long> listUserIdByTemplateId(int templateId) {
		return templateUserBindMapper.listUserIdByTemplateId(templateId);
	}

	@Override
	public CallBackInfo getCallbackInfoByTemplateId(int templateId) {
		return callbackInfoMapper.getCallbackInfoByTemplateId(templateId);
	}

	@Override
	public void deleteTemplateHostGroupBindByTemplateId(int templateId) {
		templateHostGroupBindMapper.deleteTemplateHostGroupBindByTemplateId(templateId);
	}

	@Override
	public void deleteStrategyInfoByTemplateId(int templateId) {
		strategyInfoMapper.deleteStrategyInfoByTemplateId(templateId);
	}

	@Override
	public void deleteTemplateUserBindByTemplateId(int templateId) {
		templateUserBindMapper.deleteTemplateUserBindByTemplateId(templateId);
	}

	@Override
	public void deleteCallbackInfoByTemplateId(int templateId) {
		callbackInfoMapper.deleteCallbackInfoByTemplateId(templateId);
	}

	@Override
	public void deleteTemplateInfoBasicById(int id) {
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
	public int addHostGroupInfoBasic(HostGroupInfoBasic hostGroupInfoBasic) {
		return hostGroupInfoBasicMapper.addHostGroupInfoBasic(hostGroupInfoBasic);
	}

	@Override
	public HostGroupInfoBasic getHostGroupInfoBasicById(int id) {
		return hostGroupInfoBasicMapper.getHostGroupInfoBasicById(id);
	}

	@Override
	public void updateHostGroupInfoBasicById(HostGroupInfoBasic updatedHostGroupInfoBasic) {
		hostGroupInfoBasicMapper.updateHostGroupInfoBasicById(updatedHostGroupInfoBasic);
	}

	@Override
	public void deleteTemplateHostGroupBindByHostGroupId(int id) {
		templateHostGroupBindMapper.deleteTemplateHostGroupBindByHostGroupId(id);
	}

	@Override
	public void deleteHostGroupInfoBasicById(int id) {
		hostGroupInfoBasicMapper.deleteHostGroupInfoBasicById(id);
	}

	@Override
	public void deleteHostGroupHostBindByHostGroupId(int id) {
		hostGroupHostBindMapper.deleteHostGroupHostBindByHostGroupId(id);
	}

	@Override
	public HostInfo getHostInfoById(int id) {
		return hostInfoMapper.getHostInfoById(id);
	}

	@Override
	public Long getHostGroupHostBindTime(int hostGroupId, int hostId) {
		return hostGroupHostBindMapper.getHostGroupHostBindTime(hostGroupId, hostId);
	}

	@Override
	public void updateHostGroupHostBind(int hostGroupId, int hostId, String bindTime) {
		hostGroupHostBindMapper.updateHostGroupHostBind(hostGroupId, hostId, bindTime);
	}

	@Override
	public void addHostGroupHostBind(int hostGroupId, int hostId, String bindTime) {
		hostGroupHostBindMapper.addHostGroupHostBind(hostGroupId, hostId, bindTime);
	}

	@Override
	public void deleteHostGroupHostBind(int hostGroupId, int hostId) {
		hostGroupHostBindMapper.deleteHostGroupHostBind(hostGroupId, hostId);
	}

	@Override
	public void addHostInfo(HostInfo hostInfo) {
		hostInfoMapper.addHostInfo(hostInfo);
	}
	
	@Override
	public List<HostInfo> getHostInfoByHostGroupId(int hostGroupId) {
		return hostInfoMapper.getHostInfoByHostGroupId(hostGroupId);
	}

	@Override
	public List<TemplateInfoBasic> getTemplateInfoBasicByHostGroupId(int hostGroupId) {
		return templateInfoBasicMapper.getTemplateInfoBasicByHostGroupId(hostGroupId);
	}

	@Override
	public List<HostGroupInfoBasic> listHostGroupInfoBasicByName(String hostGroupName) {
		return hostGroupInfoBasicMapper.listHostGroupInfoBasicByName(hostGroupName);
	}

	@Override
	public List<TemplateInfoBasic> getTemplateInfoByName(String templateName) {
		return templateInfoBasicMapper.getTemplateInfoByName(templateName);
	}

//	@Override
//	public DeploymentInfo getDeploymentByTemplateId(int id) {
//		return templateInfoBasicMapper.getDeploymentByTemplateId(id);
//	}
	 @Override
    public ServiceInfo getDeploymentByTemplateId(int templateId) {
        String serviceId = templateInfoBasicMapper.getServiceIdByTemplateId(templateId);
        if (serviceId == null) {
            return null;
        }
        return serviceDao.getService(serviceId);
    }
	
	
	@Override
	public HostInfo getHostInfoByHostname(String hostname) {
		return hostInfoMapper.getHostInfoByHostname(hostname);
	}


	

	
}
