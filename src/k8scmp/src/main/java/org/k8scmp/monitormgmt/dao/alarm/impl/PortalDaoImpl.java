package org.k8scmp.monitormgmt.dao.alarm.impl;

import java.sql.Timestamp;
import java.util.List;

import org.k8scmp.common.GlobalConstant;
import org.k8scmp.login.domain.User;
import org.k8scmp.mapper.monitor.AlarmEventInfoMapper;
import org.k8scmp.mapper.monitor.portal.PortalActionMapper;
import org.k8scmp.mapper.monitor.portal.PortalGroupHostMapper;
import org.k8scmp.mapper.monitor.portal.PortalGroupMapper;
import org.k8scmp.mapper.monitor.portal.PortalGroupTemplateMapper;
import org.k8scmp.mapper.monitor.portal.PortalHostMapper;
import org.k8scmp.mapper.monitor.portal.PortalMockcfgMapper;
import org.k8scmp.mapper.monitor.portal.PortalStrategyMapper;
import org.k8scmp.mapper.monitor.portal.PortalTemplateMapper;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.dao.alarm.PortalDao;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateType;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Action;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Group;
import org.k8scmp.monitormgmt.domain.alarm.falcon.GroupHost;
import org.k8scmp.monitormgmt.domain.alarm.falcon.GroupTemplate;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Mockcfg;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Strategy;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Template;
import org.k8scmp.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("portalDao")
public class PortalDaoImpl implements PortalDao {
	
	@Autowired
    PortalGroupTemplateMapper portalGroupTemplateMapper;
	@Autowired
    PortalStrategyMapper portalStrategyMapper;
	@Autowired
    PortalTemplateMapper portalTemplateMapper;
	@Autowired
	AlarmDao alarmDao;
	@Autowired
	PortalActionMapper portalActionMapper;
	@Autowired
    AlarmEventInfoMapper alarmEventInfoMapper;
//	@Autowired
//    AlarmEventService alarmEventService;
	@Autowired
	PortalGroupHostMapper portalGroupHostMapper;
	@Autowired
	PortalGroupMapper portalGroupMapper;
	@Autowired
	PortalMockcfgMapper portalMockcfgMapper;
	@Autowired
	PortalHostMapper portalHostMapper;
	
	@Override
	public void insertTemplateByTemplateInfo(TemplateInfo templateInfo) {

        long actionId = createTemplateRelatedByTemplateInfo(templateInfo);

        // create template
        Template template = new Template();
        template.setId(templateInfo.getId());
        template.setTpl_name(templateInfo.getTemplateName());
        template.setAction_id(actionId);
        template.setCreate_user(templateInfo.getCreatorName());
        template.setCreate_at(new Timestamp(templateInfo.getCreateTime()));
        portalTemplateMapper.insertTemplateById(template);
		
	}
	
	private long createTemplateRelatedByTemplateInfo(TemplateInfo templateInfo) {

        if (templateInfo.getTemplateType().equals(TemplateType.host.name())) {
            // create group-bind
            for (HostGroupInfoBasic hostGroupInfoBasic : templateInfo.getHostGroupList()) {
                createGroupTemplateBind(hostGroupInfoBasic.getId(), templateInfo.getId(), templateInfo.getCreatorName());
            }
            // create strategy
            for (StrategyInfo strategyInfo : templateInfo.getStrategyList()) {
                createStrategyForHost(strategyInfo, templateInfo.getId());
            }
        } else if (templateInfo.getTemplateType().equals(TemplateType.deploy.name())) {
           /* // get all instances
            List<Instance> instances;
            try {
                instances = instanceService.getInstances(templateInfo.getDeploymentInfo().getId());
            } catch (Exception e) {
                logger.warn("get instances for deployment " + templateInfo.getDeploymentInfo().getDeploymentName() + " error: " + e.getMessage());
                instances = new ArrayList<>(1);
            }
            // create host group for deployment
            long groupId = createHostGroupForDeploy(instances, templateInfo);
            // create group-bind
            createGroupTemplateBind(groupId, templateInfo.getId(), templateInfo.getCreatorName());
            // create strategy
            List<String> containerIdList = getContainerIdListFromInstanceList(instances);
            for (StrategyInfo strategyInfo : templateInfo.getStrategyList()) {
                createStrategyForDeploy(strategyInfo, templateInfo.getId(), containerIdList);
            }*/
        }
        
        // create action
        return createAction(templateInfo.getUserList(), templateInfo.getCallback());
	}
	
	private long createAction(List<User> userList, CallBackInfo callback) {

        Action action = new Action();
        StringBuilder uicBuilder = new StringBuilder();
        for (User userInfo : userList) {
            User user = alarmDao.getUserById(userInfo.getId());
            if (user == null) {
                continue;
            }
            String userName = user.getUsername();
            if (StringUtils.isBlank(userName)) {
                continue;
            }
            uicBuilder.append(userName).append(",");
        }
        String uic = uicBuilder.toString();
        if (userList.size() == 0) {
            action.setUic("");
        } else {
            action.setUic(uic.substring(0, uic.length() - 1));
        }
        action.setUrl(fixNullString(callback.getUrl()));
        if (callback.isAfterCallbackMail() || callback.isAfterCallbackSms() ||
                callback.isBeforeCallbackMail() || callback.isBeforeCallbackSms()) {
            action.setCallback(1);
        } else {
            action.setCallback(0);
        }
        action.setBefore_callback_sms(booleanToInt(callback.isBeforeCallbackSms()));
        action.setBefore_callback_mail(booleanToInt(callback.isBeforeCallbackMail()));
        action.setAfter_callback_sms(booleanToInt(callback.isAfterCallbackSms()));
        action.setAfter_callback_mail(booleanToInt(callback.isAfterCallbackMail()));
        portalActionMapper.insertAction(action);
        return action.getId();
    }
	
	 private void createGroupTemplateBind(long hostGroupId, long templateId, String creatorName) {
        GroupTemplate groupTemplate = new GroupTemplate();
        groupTemplate.setGrp_id(hostGroupId);
        groupTemplate.setTpl_id(templateId);
        groupTemplate.setBind_user(creatorName);
        insertGroupTemplateBind(groupTemplate);
	 }
	 
	 private void createStrategyForHost(StrategyInfo strategyInfo, long templateId) {

	        Strategy strategy = new Strategy();
	        // strategy.setId(strategyInfo.getId());
	        strategy.setMetric(convertMetricForHost(strategyInfo.getMetric()));
	        strategy.setTags(fixNullString(strategyInfo.getTag()));
	        strategy.setMax_step(strategyInfo.getMaxStep());
	        strategy.setPriority(0);
	        strategy.setFunc(createFunc(strategyInfo.getAggregateType(), strategyInfo.getPointNum()));
	        strategy.setOp(strategyInfo.getOperator());
	        strategy.setRight_value(String.valueOf(strategyInfo.getRightValue()));
	        strategy.setNote(strategyInfo.getNote());
	        strategy.setTpl_id(templateId);
	        portalStrategyMapper.insertStrategy(strategy);
	 	}
	 
	 private static String convertMetricForHost(String metric) {
	        switch (metric) {
	            case "cpu_percent":
	                return "cpu.busy";
	            case "memory_percent":
	                return "mem.memused.percent";
	            case "disk_percent":
	                return "df.bytes.used.percent";
	            case "disk_read":
	                return "disk.io.read_bytes";
	            case "disk_write":
	                return "disk.io.write_bytes";
	            case "network_in":
	                return "net.if.in.bytes";
	            case "network_out":
	                return "net.if.out.bytes";
	            case "agent_alive":
	                return "agent.alive";
	            default:
	                return "";
	        }
	    }
	 
	 private static int booleanToInt(boolean var) {
	        return var ? 1 : 0;
	 }

     private static String fixNullString(String var) {
        return (var == null) ? "" : var;
     }
	 
     private static String createFunc(String aggregateType, int pointNum) {
        return aggregateType + "(#" + String.valueOf(pointNum) + ")";
     }
    
	 public void insertGroupTemplateBind(GroupTemplate groupTemplate) {

        if (portalGroupTemplateMapper.checkGroupTemplateBind(groupTemplate) != null) {
            return;
        }
        portalGroupTemplateMapper.insertGroupTemplateBind(groupTemplate);
     }

	@Override
	public void updateTemplateByTemplateInfo(TemplateInfo templateInfo) {

        Template template = portalTemplateMapper.getTemplateById(templateInfo.getId());
        if (template == null) {
            insertTemplateByTemplateInfo(templateInfo);
            return;
        }

        // delete related
        deleteTemplateRelatedByTemplateInfo(templateInfo.getId(), templateInfo.getTemplateType());

        // create related
        long actionId = createTemplateRelatedByTemplateInfo(templateInfo);

        // update template
        template.setTpl_name(templateInfo.getTemplateName());
        template.setAction_id(actionId);
        portalTemplateMapper.updateTemplateById(template);
    }

	 private void deleteTemplateRelatedByTemplateInfo(long templateId, String templateType) {

	        Template template = portalTemplateMapper.getTemplateById(templateId);
	        if (template == null) {
	            return;
	        }

	        // delete action & strategy & group-bind
	        portalActionMapper.deleteActionById(template.getAction_id());
	        List<String> alarmEventIds = alarmEventInfoMapper.listAlarmEventInfoIdByTemplateId(template.getId());
	        String alarmString = "";
	        if (alarmEventIds != null) {
	            for (String alarmEventId : alarmEventIds) {
	                alarmString = alarmString + alarmEventId + ",,";
	            }
	        }
//	        if (!StringUtils.isBlank(alarmString)) {
//	            alarmString = alarmString.substring(0, alarmString.length() - 2);
//	            alarmEventService.ignoreAlarmsInside(alarmString);
//	        }
	        portalStrategyMapper.deleteStrategyByTemplateId(template.getId());
	        if (templateType.equals(TemplateType.deploy.name())) {
	            portalGroupHostMapper.deleteByTemplate(template.getId());
	            portalGroupMapper.deleteByTemplate(template.getId());
	        }
	        portalGroupTemplateMapper.deleteByTemplate(template.getId());
	    }

	@Override
	public void deleteTemplateByIdAndType(long templateId, String templateType) {
		// delete related
        deleteTemplateRelatedByTemplateInfo(templateId, templateType);

        portalTemplateMapper.deleteTemplateById(templateId);
	}

	@Override
	public void insertHostGroupByHostGroupBasicInfo(HostGroupInfoBasic hostGroupInfoBasic) {
		Group group = new Group();
        group.setId(hostGroupInfoBasic.getId());
        group.setGrp_name(hostGroupInfoBasic.getHostGroupName());
        group.setCreate_user(hostGroupInfoBasic.getCreatorName());
        group.setCreate_at(new Timestamp(hostGroupInfoBasic.getCreateTime()));
        group.setCome_from(1);
        portalGroupMapper.insertHostGroupById(group);

        // update nodata config
        updateNodataObj(getHostGroupList());
	}

	private void updateNodataObj(String groups) {

        Mockcfg mockcfg = portalMockcfgMapper.getMockcfgByName(GlobalConstant.NODATA_CONFIG_NAME);
        if (mockcfg == null) {
            mockcfg = new Mockcfg();
            mockcfg.setName(GlobalConstant.NODATA_CONFIG_NAME);
            mockcfg.setObj(groups);
            mockcfg.setObj_type("group");
            mockcfg.setMetric("agent.alive");
            mockcfg.setTags("");
            mockcfg.setDstype("GAUGE");
            mockcfg.setStep(10);
            mockcfg.setMock(-1);
            mockcfg.setCreator("admin");
            mockcfg.setT_create(new Timestamp(System.currentTimeMillis()));
            mockcfg.setT_modify(mockcfg.getT_create());
            portalMockcfgMapper.insertMockcfg(mockcfg);
            return;
        }
        portalMockcfgMapper.updateObjByName(GlobalConstant.NODATA_CONFIG_NAME, groups);
    
	}
	
	public String getHostGroupList() {
        List<HostGroupInfoBasic> hostGroups = alarmDao.listHostGroupInfoBasic();
        if (hostGroups == null || hostGroups.size() == 0) {
            return "";
        }
        StringBuilder retBuilder = new StringBuilder();
        for (HostGroupInfoBasic hostGroupInfoBasic : hostGroups) {
            retBuilder.append(hostGroupInfoBasic.getHostGroupName()).append("\n");
        }
        String ret = retBuilder.toString();
        return retBuilder.toString().substring(0, ret.length() - 1);
    }

	@Override
	public void updateHostGroupByHostGroupBasicInfo(HostGroupInfoBasic hostGroupInfoBasic) {
		long id = hostGroupInfoBasic.getId();
        String grp_name = hostGroupInfoBasic.getHostGroupName();
        portalGroupMapper.updateHostGroup(id, grp_name);

        // update nodata config
        updateNodataObj(getHostGroupList());
	}

	@Override
	public Integer getHostIdByHostname(String hostname) {
		return portalHostMapper.getHostIdByHostname(hostname);
	}

	@Override
	public void insertGroupHostBind(long grp_id, long host_id) {

        GroupHost groupHost = new GroupHost(grp_id, host_id);
        if (portalGroupHostMapper.checkGroupHostBind(groupHost) != null) {
            return;
        }
        portalGroupHostMapper.insertGroupHostBind(groupHost);
    }

	@Override
	public void deleteGroupHostBind(long grp_id, long host_id) {
		 portalGroupHostMapper.deleteGroupHostBind(grp_id, host_id);
	} 
	 
	 
 }
