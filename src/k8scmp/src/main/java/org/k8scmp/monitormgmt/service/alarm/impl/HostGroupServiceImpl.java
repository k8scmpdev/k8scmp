package org.k8scmp.monitormgmt.service.alarm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.engine.k8s.util.NodeWrapper;
import org.k8scmp.engine.k8s.util.NodeWrapperNew;
import org.k8scmp.exception.ApiException;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.dao.alarm.PortalDao;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.HostInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;
import org.k8scmp.monitormgmt.service.alarm.HostGroupService;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.operation.OperationType;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by baokangwang on 2016/4/13.
 */
@Service
public class HostGroupServiceImpl implements HostGroupService {

    private static Logger logger = LoggerFactory.getLogger(HostGroupServiceImpl.class);

    private final ResourceType resourceType = ResourceType.ALARM;

    @Autowired
    AlarmDao alarmBiz;

    @Autowired
    PortalDao portalBiz;
    
    @Autowired
    OperationLog operationLog;
    
    @Autowired
    NodeWrapperNew nodeWrapperNew;
    
    @Override
    public List<HostGroupInfo> listHostGroupInfo() {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        List<HostGroupInfoBasic> hostGroupInfoBasics = alarmBiz.listHostGroupInfoBasic();
        if (hostGroupInfoBasics == null) {
            return null;
        }
        List<HostGroupInfoTask> hostGroupInfoTasks = new LinkedList<>();

        for (HostGroupInfoBasic hostGroupInfoBasic : hostGroupInfoBasics) {
            hostGroupInfoTasks.add(new HostGroupInfoTask(hostGroupInfoBasic));
        }
        List<HostGroupInfo> hostGroupInfos = ClientConfigure.executeCompletionService(hostGroupInfoTasks);
        Collections.sort(hostGroupInfos, new HostGroupInfo.HostGroupInfoComparator());
        return hostGroupInfos;
    }
    
    @Override
    public HttpResponseTemp<?> searchHostGroupInfo(String hostGroupName) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        List<HostGroupInfoBasic> hostGroupInfoBasics = alarmBiz.listHostGroupInfoBasicByName(hostGroupName);
        if (hostGroupInfoBasics == null) {
            return null;
        }
        List<HostGroupInfoTask> hostGroupInfoTasks = new LinkedList<>();

        for (HostGroupInfoBasic hostGroupInfoBasic : hostGroupInfoBasics) {
            hostGroupInfoTasks.add(new HostGroupInfoTask(hostGroupInfoBasic));
        }
        List<HostGroupInfo> hostGroupInfos = ClientConfigure.executeCompletionService(hostGroupInfoTasks);
        Collections.sort(hostGroupInfos, new HostGroupInfo.HostGroupInfoComparator());
        return ResultStat.OK.wrap(hostGroupInfos);
    }
    
    
    @Override
    public HttpResponseTemp<?> createHostGroup(HostGroupInfoBasic hostGroupInfoBasic) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        if (hostGroupInfoBasic == null) {
            throw ApiException.wrapMessage(ResultStat.HOSTGROUP_NOT_LEGAL, "host group info is null");
        }
        if (hostGroupInfoBasic.checkLegality() != null) {
            throw ApiException.wrapMessage(ResultStat.HOSTGROUP_NOT_LEGAL, hostGroupInfoBasic.checkLegality());
        }
        if (alarmBiz.getHostGroupInfoBasicByName(hostGroupInfoBasic.getHostGroupName()) != null) {
            throw ApiException.wrapResultStat(ResultStat.HOSTGROUP_EXISTED);
        }
//        String id= UUIDUtil.generateUUID();
//        hostGroupInfoBasic.setId(id);
        hostGroupInfoBasic.setCreatorId(AuthUtil.getUserId());
        hostGroupInfoBasic.setCreatorName(AuthUtil.getCurrentLoginName());
        hostGroupInfoBasic.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
        hostGroupInfoBasic.setUpdateTime(hostGroupInfoBasic.getCreateTime());

        int hostback = alarmBiz.addHostGroupInfoBasic(hostGroupInfoBasic);

        // insert host group info into portal database
        int portalback = portalBiz.insertHostGroupByHostGroupBasicInfo(hostGroupInfoBasic);
        
        String state = null;
        String info = null;
        if(hostback > 0 && portalback > 0 ){
        	state = "ok";
        	info = "创建用户组成功!";
        }else{
        	state = "faile";
        	info = "创建用户组失败!";
        }
        
        operationLog.insertRecord(new OperationRecord(
				"1",//uuid 
				ResourceType.ALARM,
				OperationType.SET, 
				AuthUtil.getCurrentLoginName(),
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));

        return ResultStat.OK.wrap(hostGroupInfoBasic);
    }

    @Override
    public HttpResponseTemp<?> modifyHostGroup(HostGroupInfoBasic hostGroupInfoBasic) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        if (hostGroupInfoBasic == null) {
            throw ApiException.wrapMessage(ResultStat.HOSTGROUP_NOT_LEGAL, "host group info is null");
        }
        if (hostGroupInfoBasic.checkLegality() != null) {
            throw ApiException.wrapMessage(ResultStat.HOSTGROUP_NOT_LEGAL, hostGroupInfoBasic.checkLegality());
        }
        HostGroupInfoBasic updatedHostGroupInfoBasic = alarmBiz.getHostGroupInfoBasicById(hostGroupInfoBasic.getId());
        if (updatedHostGroupInfoBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.HOSTGROUP_NOT_EXISTED);
        }

        updatedHostGroupInfoBasic.setHostGroupName(hostGroupInfoBasic.getHostGroupName());
        updatedHostGroupInfoBasic.setUpdateTime(DateUtil.dateFormat(new Date()));

        alarmBiz.updateHostGroupInfoBasicById(updatedHostGroupInfoBasic);

        // update host group info in portal database
        portalBiz.updateHostGroupByHostGroupBasicInfo(updatedHostGroupInfoBasic);

        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> deleteHostGroup(int id) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        HostGroupInfoBasic hostGroupInfoBasic = alarmBiz.getHostGroupInfoBasicById(id);
        if (hostGroupInfoBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.HOSTGROUP_NOT_EXISTED);
        }

        alarmBiz.deleteHostGroupHostBindByHostGroupId(id);
        alarmBiz.deleteTemplateHostGroupBindByHostGroupId(id);
        alarmBiz.deleteHostGroupInfoBasicById(id);

        // delete host group info in portal database
        portalBiz.deleteHostGroupById(id);

        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> bindHostList(int id, List<HostInfo> hostInfoList) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        if (hostInfoList == null) {
            throw ApiException.wrapMessage(ResultStat.HOST_NOT_LEGAL, "host info list is null");
        }
        for (HostInfo hostInfo : hostInfoList) {
            if (hostInfo.checkLegality() != null) {
                throw ApiException.wrapMessage(ResultStat.HOST_NOT_LEGAL, hostInfo.checkLegality());
            }
        }
        HostGroupInfoBasic hostGroupInfoBasic = alarmBiz.getHostGroupInfoBasicById(id);
        if (hostGroupInfoBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.HOSTGROUP_NOT_EXISTED);
        }
        for (HostInfo hostInfo : hostInfoList) {
            if (portalBiz.getHostIdByHostname(hostInfo.getHostname()) == null) {
                throw ApiException.wrapMessage(ResultStat.AGENT_NOT_READY, "agent not ready on node " + hostInfo.getHostname());
            }
        }

        for (HostInfo hostInfo : hostInfoList) {

            int hostId = portalBiz.getHostIdByHostname(hostInfo.getHostname());
            hostInfo.setId(hostId);
            createHostIfNotExist(hostInfo);
            if (alarmBiz.getHostGroupHostBindTime(id, hostId) != null) {
                alarmBiz.updateHostGroupHostBind(id, hostId, DateUtil.dateFormat(new Date()));
            } else {
                alarmBiz.addHostGroupHostBind(id, hostId, DateUtil.dateFormat(new Date()));
                portalBiz.insertGroupHostBind(id, hostId);
            }
        }

        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> unbindHost(int id, int hostId) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        if (alarmBiz.getHostGroupInfoBasicById(id) == null) {
            throw ApiException.wrapResultStat(ResultStat.HOSTGROUP_NOT_EXISTED);
        }
        if (alarmBiz.getHostInfoById(hostId) == null) {
            throw ApiException.wrapResultStat(ResultStat.HOST_NOT_EXISTED);
        }

        alarmBiz.deleteHostGroupHostBind(id, hostId);
        portalBiz.deleteGroupHostBind(id, hostId);
        return ResultStat.OK.wrap(null);
    }

    @Override
    public void createHostIfNotExist(HostInfo hostInfo) {
        HostInfo retrievedHostInfo = alarmBiz.getHostInfoById(hostInfo.getId());
        if (retrievedHostInfo != null) {
            return;
        }

        hostInfo.setCreateTime(DateUtil.dateFormat(new Date()));
        alarmBiz.addHostInfo(hostInfo);
    }

    private class HostGroupInfoTask implements Callable<HostGroupInfo> {
        HostGroupInfoBasic hostGroupInfoBasic;

        public HostGroupInfoTask(HostGroupInfoBasic hostGroupInfoBasic) {
            this.hostGroupInfoBasic = hostGroupInfoBasic;
        }

        @Override
        public HostGroupInfo call() throws Exception {
            HostGroupInfo hostGroupInfo = new HostGroupInfo(hostGroupInfoBasic);

            int hostGroupId = hostGroupInfoBasic.getId();
            List<HostInfo> hostInfoList = alarmBiz.getHostInfoByHostGroupId(hostGroupId);
            hostGroupInfo.setHostList(hostInfoList);
            List<TemplateInfoBasic> templateInfoBasicList = alarmBiz.getTemplateInfoBasicByHostGroupId(hostGroupId);
            hostGroupInfo.setTemplateList(templateInfoBasicList);

            return hostGroupInfo;
        }
    }
    
    @Override
    public List<HostInfo> getHostList(){
    	try {
			NodeWrapper nodeWrapper = new NodeWrapper().init("default");
			List<HostInfo> hostInfoList = new ArrayList<>();
			List<NodeInfo> nodeInfoList = nodeWrapper.getNodeInfoListWithoutPods();
			for (NodeInfo nodeInfo : nodeInfoList) {
				HostInfo hostInfo = new HostInfo();
				hostInfo.setHostname(nodeInfo.getName());
				hostInfo.setIp(nodeInfo.getIp());
				hostInfoList.add(hostInfo);
			}
			return hostInfoList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    @Override
    public List<HostInfo> getHostBindList(int hostGroupId){
    	return alarmBiz.getHostInfoByHostGroupId(hostGroupId);
    }
    
}
