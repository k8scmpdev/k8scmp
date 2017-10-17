package org.k8scmp.appmgmt.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.dao.ServiceEventDao;
import org.k8scmp.appmgmt.dao.VersionDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.Container;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.Env;
import org.k8scmp.appmgmt.domain.Instance;
import org.k8scmp.appmgmt.domain.NodePortDraft;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;
import org.k8scmp.appmgmt.domain.VersionString;
import org.k8scmp.appmgmt.service.ServiceService;
import org.k8scmp.appmgmt.service.ServiceStatusManager;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.engine.ClusterRuntimeDriver;
import org.k8scmp.engine.RuntimeDriver;
import org.k8scmp.engine.exception.DriverException;
import org.k8scmp.engine.k8s.handler.DeployResourceHandler;
import org.k8scmp.engine.k8s.handler.impl.DeploymentDeployHandler;
import org.k8scmp.engine.k8s.util.NodeWrapper;
import org.k8scmp.engine.k8s.util.PodUtils;
import org.k8scmp.exception.ApiException;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.model.LoadBalancerProtocol;
import org.k8scmp.model.ServiceStatus;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.operation.OperationType;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.CommonUtil;
import org.k8scmp.util.DateUtil;
import org.k8scmp.util.StringUtils;
import org.k8scmp.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;

@Service
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    ServiceDao serviceDao;
    
    @Autowired
    AppDao appDao;
    
    @Autowired
    VersionDao versionDao;
    
    @Autowired
    OperationLog operationLog;
    
    @Autowired
    ServiceStatusManager serviceStatusManager;
    
    @Autowired
    GlobalBiz globalBiz;
    
    @Autowired
    ServiceEventDao serviceEventDao;
    
    private static Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

	@Override
	public String createService(
			ServiceDetail serviceDetail) {
		if(serviceDetail==null){
			 throw ApiException.wrapMessage(ResultStat.SERVICE_NOT_LEGAL, "service is null");
		}
		ServiceConfigInfo service = serviceDetail.getServiceConfigInfo();
		String serviceCode = service.getServiceCode();
		String appId = service.getAppId();
		
		AppInfo currentApp = appDao.getApp(appId);
		if(currentApp == null){
			throw ApiException.wrapMessage(ResultStat.SERVICE_NOT_LEGAL, "application is not exsited");
		}
		List<AppInfo> apps = appDao.getAppsByserviceCode(serviceCode);
		if (apps != null && apps.size() != 0) {
            for (AppInfo one : apps) {
                if (one.getClusterId() == currentApp.getClusterId() &&
                        one.getNamespace().equals(currentApp.getNamespace())) {
                    throw ApiException.wrapResultStat(ResultStat.SERVICE_EXIST);
                }
            }
        }
		String serviceId = UUIDUtil.generateUUID();
		service.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
		service.setCreatorId(AuthUtil.getCurrentLoginName());
		service.setId(serviceId);
		service.setState(ServiceStatus.STOP.name());
		
		serviceDao.createService(service);
		
		List<Version> versions = serviceDetail.getVersions();
		
		if(versions == null || versions.size()!=1){
			throw ApiException.wrapMessage(ResultStat.SERVICE_NOT_LEGAL, "service config information cannot be None or more than one");
		}
		
		Version version = versions.get(0);
		String errInfo = version.checkLegality();
        if (!StringUtils.isBlank(errInfo)) {
            throw new ApiException(ResultStat.SERVICE_NOT_LEGAL,errInfo);
        }
        version.setId(UUIDUtil.generateUUID());
        version.setCreatorId(service.getCreatorId());
        version.setCreateTime(service.getCreateTime());
		version.setServiceId(serviceId);
		version.setState("");
		version.setDeprecate(false);
		try{
			versionDao.insertVersion(version);
		}catch(Exception e){
			serviceDao.deleteService(serviceId);
			versionDao.deleteAllVersion(serviceId);
			throw e;
		}
		operationLog.insertRecord(new OperationRecord(
				serviceId, 
				ResourceType.SERVICE,
				OperationType.SET, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"新增服务信息", 
				DateUtil.dateFormatToMillis(new Date())
		));
		
		return serviceId;
		
		
	}


	@Override
	public void deleteService(String id) {
		String statu = serviceDao.getServiceStatu(id);
		if(!ServiceStatus.STOP.name().equals(statu)){
			throw ApiException.wrapMessage(ResultStat.CANNOT_DELETE_SERVICE, "service statu is "+statu+" now");
		}
		serviceDao.deleteService(id);
		versionDao.deleteAllVersion(id);
		
		operationLog.insertRecord(new OperationRecord(
				id, 
				ResourceType.SERVICE,
				OperationType.DELETE, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"删除服务信息",  
				DateUtil.dateFormatToMillis(new Date())
		));
	}


	@Override
	public void modifyService(ServiceInfo serviceInfo) {
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		serviceDao.updateDescription(serviceInfo);
		operationLog.insertRecord(new OperationRecord(
				serviceInfo.getId(), 
				ResourceType.SERVICE,
				OperationType.MODIFY, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"编辑服务信息", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}


	@Override
	public List<ServiceConfigInfo> listServices(ServiceInfo serviceInfo) {
		List<ServiceInfo> services = serviceDao.getServices(serviceInfo);
		if (services == null || services.size() == 0) {
            return new ArrayList<>(1);
        }
		
		List<GetServiceInfoTask> serviceInfoTasks = new LinkedList<>();
        for (ServiceInfo service : services) {
        	serviceInfoTasks.add(new GetServiceInfoTask(service));
        }
        
        List<ServiceConfigInfo> serviceConfigInfos = ClientConfigure.executeCompletionService(serviceInfoTasks);
        // sort by startSeq
        Collections.sort(serviceConfigInfos, new Comparator<ServiceConfigInfo>() {
            @Override
            public int compare(ServiceConfigInfo o1, ServiceConfigInfo o2) {
                return ((Integer)o2.getStartSeq()).compareTo((Integer)o1.getStartSeq());
            }
        });
        
		return serviceConfigInfos;
	}
	
	@Override
	public List<ServiceConfigInfo> getServicesByAppId(String appId) {
		List<ServiceInfo> services = serviceDao.getServicesByAppId(appId);
		if (services == null || services.size() == 0) {
            return new ArrayList<>(1);
        }
		
		List<GetServiceInfoTask> serviceInfoTasks = new LinkedList<>();
        for (ServiceInfo service : services) {
        	serviceInfoTasks.add(new GetServiceInfoTask(service));
        }
        
        List<ServiceConfigInfo> serviceConfigInfos = ClientConfigure.executeCompletionService(serviceInfoTasks);
        // sort by startSeq
        Collections.sort(serviceConfigInfos, new Comparator<ServiceConfigInfo>() {
            @Override
            public int compare(ServiceConfigInfo o1, ServiceConfigInfo o2) {
                return ((Integer)o2.getStartSeq()).compareTo((Integer)o1.getStartSeq());
            }
        });
        
		return serviceConfigInfos;
	}
	
	 private class GetServiceInfoTask implements Callable<ServiceConfigInfo> {
		 ServiceInfo service;

        private GetServiceInfoTask(ServiceInfo service) {
            this.service = service;
        }

        @Override
        public ServiceConfigInfo call() throws Exception {
        	if (service == null) {
                return null;
            }
        	ServiceConfigInfo serviceConfig = (ServiceConfigInfo) service.toModel(ServiceConfigInfo.class);
            return serviceConfig;
        }
    }

//	@Override
//	public List<ServiceConfigInfo> getService(String serviceId) {
//		 
//	}
	
	@Override
	public String startService(String serviceId, int version,int replicas) throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
		checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		VersionBase verBase = versionDao.getVersion(serviceId, version);
		if(verBase == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
		Version ver = verBase.toModel(Version.class);
		if (ver.isDeprecate()) {
            throw ApiException.wrapMessage(ResultStat.SERVICE_START_FAILED, "can't start deprecated version");
        }
		
		serviceStatusManager.checkStateAvailable(ServiceStatus.valueOf(serviceInfo.getState()), ServiceStatus.DEPLOYING);
		
		serviceInfo.setState(ServiceStatus.DEPLOYING.name());
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		if(replicas>0){
			service.setDefaultReplicas(replicas);
		}
		serviceInfo.setData(service.toString());
		serviceDao.updateService(serviceInfo);
		
		Cluster cluster = getCluster();
		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
		if (driver == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "cluster: " + cluster.toString());
        }
		
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
		try {
            List<Env> allExtraEnvs = buildExtraEnv(cluster);
            driver.startDeploy(appInfo, service, ver, AuthUtil.getUser(), allExtraEnvs);
            // add operation record
            operationLog.insertRecord(new OperationRecord(
            		serviceId, 
    				ResourceType.SERVICE,
    				OperationType.START, 
    				AuthUtil.getCurrentLoginName(), 
    				AuthUtil.getUserName(),
    				"OK", 
    				"部署服务", 
    				DateUtil.dateFormatToMillis(new Date())
    		));
        } catch (DriverException e) {
            serviceStatusManager.failedEventForDeployment(serviceId, null, e.getMessage());
            throw ApiException.wrapMessage(ResultStat.SERVICE_START_FAILED, e.getMessage());
        }
		return serviceId;
	}
	
	@Override
	public String createLoadBalancer(String serviceId,List<NodePortDraft> nodePorts) throws Exception{
		if(nodePorts == null || nodePorts.size()==0){
			return null;
		}
		
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		service.setExternal(true);
		service.setNodePorts(nodePorts);
		
		serviceInfo.setData(service.toString());
		
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		
		serviceDao.updateService(serviceInfo);
		
		Cluster cluster = getCluster();
		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
		if (driver == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "cluster: " + cluster.toString());
        }
		
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
		try {
            driver.createLoadBalancer(appInfo, service);
            // add operation record
            operationLog.insertRecord(new OperationRecord(
            		serviceId, 
    				ResourceType.SERVICE,
    				OperationType.STARTLOADBALANCER, 
    				AuthUtil.getCurrentLoginName(), 
    				AuthUtil.getUserName(),
    				"OK", 
    				"创建负载", 
    				DateUtil.dateFormatToMillis(new Date())
    		));
        } catch (DriverException e) {
            throw ApiException.wrapMessage(ResultStat.LOADBALANCER_START_FAILED, e.getMessage());
        }
		
		return serviceId;
	}
	
	@Override
	public void stopService(String serviceId) throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
		checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		
		serviceStatusManager.checkStateAvailable(ServiceStatus.valueOf(serviceInfo.getState()), ServiceStatus.STOP);
		
		serviceInfo.setState(ServiceStatus.STOPPING.name());
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		
		serviceDao.updateService(serviceInfo);
		
		Cluster cluster = getCluster();
		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
		if (driver == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "cluster: " + cluster.toString());
        }
		
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
		try {
            List<Env> allExtraEnvs = buildExtraEnv(cluster);
            driver.stopDeploy(appInfo, service, AuthUtil.getUser());
            // add operation record
            operationLog.insertRecord(new OperationRecord(
            		serviceId, 
    				ResourceType.SERVICE,
    				OperationType.STOP, 
    				AuthUtil.getCurrentLoginName(), 
    				AuthUtil.getUserName(),
    				"OK", 
    				"停止服务", 
    				DateUtil.dateFormatToMillis(new Date())
    		));
        } catch (DeploymentEventException e) {
            serviceStatusManager.failedEventForDeployment(serviceId, null, e.getMessage());
            throw ApiException.wrapMessage(ResultStat.SERVICE_STOP_FAILED, e.getMessage());
        }
	}
	
	
	@Override
	public void startUpdate(String serviceId,int version,int replicas) throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
		checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		VersionBase verBase = versionDao.getVersion(serviceId, version);
		if(verBase == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
		Version ver = verBase.toModel(Version.class);
		if (ver.isDeprecate()) {
            throw ApiException.wrapMessage(ResultStat.SERVICE_UPDATE_FAILED, "can't update deprecated version");
        }
		
		serviceStatusManager.checkStateAvailable(ServiceStatus.valueOf(serviceInfo.getState()), ServiceStatus.UPDATING);
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		
		Cluster cluster = getCluster();
		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
		if (driver == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "cluster: " + cluster.toString());
        }
		
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
		try {
            List<Env> allExtraEnvs = buildExtraEnv(cluster);
            if(replicas>0){
    			service.setDefaultReplicas(replicas);
    			serviceInfo.setData(service.toString());
    		}
            
            driver.startUpdate(appInfo, service, version, allExtraEnvs,AuthUtil.getUser());
            // add operation record
            
        } catch (DeploymentEventException e) {
            serviceStatusManager.failedEventForDeployment(serviceId, null, e.getMessage());
            throw ApiException.wrapMessage(ResultStat.SERVICE_UPDATE_FAILED, e.getMessage());
        }
		
		serviceInfo.setState(ServiceStatus.UPDATING.name());
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		
		serviceDao.updateService(serviceInfo);
		operationLog.insertRecord(new OperationRecord(
        		serviceId, 
				ResourceType.SERVICE,
				OperationType.UPDATE, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"升级版本", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}
	
	@Override
	public void startRollback(String serviceId,int version,int replicas) throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
		checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		VersionBase verBase = versionDao.getVersion(serviceId, version);
		if(verBase == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
		Version ver = verBase.toModel(Version.class);
		if (ver.isDeprecate()) {
            throw ApiException.wrapMessage(ResultStat.SERVICE_ROLLBACK_FAILED, "can't rollback deprecated version");
        }
		
		serviceStatusManager.checkStateAvailable(ServiceStatus.valueOf(serviceInfo.getState()), ServiceStatus.UPDATING);
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		
		Cluster cluster = getCluster();
		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
		if (driver == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "cluster: " + cluster.toString());
        }
		
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
		try {
            List<Env> allExtraEnvs = buildExtraEnv(cluster);
            if(replicas>0){
    			service.setDefaultReplicas(replicas);
    			serviceInfo.setData(service.toString());
    		}
            
            driver.rollbackDeploy(appInfo, service, version, allExtraEnvs,AuthUtil.getUser());
            // add operation record
            
        } catch (DeploymentEventException e) {
            serviceStatusManager.failedEventForDeployment(serviceId, null, e.getMessage());
            throw ApiException.wrapMessage(ResultStat.SERVICE_ROLLBACK_FAILED, e.getMessage());
        }
		
		serviceInfo.setState(ServiceStatus.BACKROLLING.name());
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		
		serviceDao.updateService(serviceInfo);
		operationLog.insertRecord(new OperationRecord(
        		serviceId, 
				ResourceType.SERVICE,
				OperationType.ROLLBACK, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"升级版本", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}
	
	@Override
    public void scaleUpDeployment(String serviceId,int version,int replicas) throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
//		checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		
		serviceStatusManager.checkStateAvailable(ServiceStatus.valueOf(serviceInfo.getState()), ServiceStatus.UPSCALING);
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		
		Cluster cluster = getCluster();
		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
		if (driver == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "cluster: " + cluster.toString());
        }
		
		service.setDefaultReplicas(replicas);
		serviceInfo.setData(service.toString());
		
		serviceInfo.setState(ServiceStatus.UPSCALING.name());
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		
		serviceDao.updateService(serviceInfo);
		
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
        List<Env> allExtraEnvs = buildExtraEnv(cluster);
            
        driver.scaleUpDeployment(appInfo, service, version, replicas, allExtraEnvs,AuthUtil.getUser());
        // add operation record
		operationLog.insertRecord(new OperationRecord(
        		serviceId, 
				ResourceType.SERVICE,
				OperationType.SCALEUP, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"实例扩容", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}
	
	@Override
    public void scaleDownDeployment(String serviceId,int version,int replicas) throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
//		checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		
		serviceStatusManager.checkStateAvailable(ServiceStatus.valueOf(serviceInfo.getState()), ServiceStatus.DOWNSCALING);
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		
		Cluster cluster = getCluster();
		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
		if (driver == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "cluster: " + cluster.toString());
        }
		
		service.setDefaultReplicas(replicas);
		serviceInfo.setData(service.toString());
		
		serviceInfo.setState(ServiceStatus.DOWNSCALING.name());
		serviceInfo.setLastModifierId(AuthUtil.getCurrentLoginName());
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		
		serviceDao.updateService(serviceInfo);
		
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
        List<Env> allExtraEnvs = buildExtraEnv(cluster);
            
        driver.scaleDownDeployment(appInfo, service, version, replicas, allExtraEnvs,AuthUtil.getUser());
        // add operation record
		operationLog.insertRecord(new OperationRecord(
        		serviceId, 
				ResourceType.SERVICE,
				OperationType.SCALEDOWN, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"实例缩容", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}
	
	@Override
    public List<DeployEvent> listDeployEvent(String serviceId) throws Exception {
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
//			checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		List<DeployEvent> events = serviceEventDao.getEventByServiceId(serviceId);
       
		// sort by startTime
        if (events.size() != 0) {
            Collections.sort(events, new Comparator<DeployEvent>() {
                @Override
                public int compare(DeployEvent o1, DeployEvent o2) {
                    return ((String) o2.getStartTime()).compareTo(o1.getStartTime());
                }
            });
        }
        
		return events;
    }
	
	@Override
    public VersionString getYamlStr(ServiceConfigInfo serviceConfigInfo) {
		if(serviceConfigInfo==null){
			return null;
		}
		
		AppInfo appInfo = appDao.getApp(serviceConfigInfo.getAppId());
		
        DeployResourceHandler deployResourceHandler = new DeploymentDeployHandler(null, null, null);
        
        return deployResourceHandler.getVersionString(appInfo,serviceConfigInfo);
    }
	
	@Override
	public List<Instance> listPodsByServiceId(String serviceId) throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
		checkStartSeq(serviceInfo.getAppId(),serviceInfo.getStartSeq());
		
		
//		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
		
		Cluster cluster = getCluster();
		
		Map<String, String> labels = new HashMap<>();
        labels.put(GlobalConstant.DEPLOY_ID_STR, serviceId);
        NodeWrapper nodeWrapper = new NodeWrapper().init(cluster, appInfo.getNamespace());
        PodList podList = nodeWrapper.getPods(labels);
        
        if (podList == null || podList.getItems() == null || podList.getItems().isEmpty()) {
            return new ArrayList<>(1);
        }
        List<Instance> instances = new ArrayList<>(podList.getItems().size());
        for (Pod pod : podList.getItems()) {
            Instance instance = new Instance();
            instance.setServiceId(serviceId);;
            instance.setServiceCode(serviceInfo.getServiceCode());;
            instance.setNamespace(appInfo.getNamespace());
            if (pod.getMetadata() != null) {
                instance.setInstanceName(pod.getMetadata().getName());
                if (pod.getMetadata().getLabels() != null && pod.getMetadata().getLabels().containsKey(GlobalConstant.VERSION_STR)) {
                    instance.setVersion(Integer.valueOf(pod.getMetadata().getLabels().get(GlobalConstant.VERSION_STR)));
                }
            }
            if (pod.getSpec() != null) {
                instance.setHostName(pod.getSpec().getNodeName());
            }
            if (pod.getStatus() != null) {
                instance.setStartTime(DateUtil.stringToMillis(pod.getStatus().getStartTime(), TimeZone.getTimeZone(GlobalConstant.UTC_TIME)));
                instance.setPodIp(pod.getStatus().getPodIP());
                instance.setHostIp(pod.getStatus().getHostIP());
                if (pod.getStatus().getContainerStatuses() != null) {
                    for (ContainerStatus containerStatus : pod.getStatus().getContainerStatuses()) {
                        if (StringUtils.isBlank(containerStatus.getContainerID())) {
                            continue;
                        }
                        String containerId = containerStatus.getContainerID().split("docker://")[1];
                        instance.addContainer(new Container(containerId, containerStatus.getName(), containerStatus.getImage()));
                    }
                }
            }
            instance.setStatus(PodUtils.getPodStatus(pod));
            instances.add(instance);
        }
        return instances;
	}
	
	
	private Cluster getCluster(){
		GlobalInfo cluster_host = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
		GlobalInfo cluster_name = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
    	Cluster cluster = new Cluster();
    	cluster.setApi(cluster_host.getValue());
    	cluster.setId(cluster_host.getId()+"");
    	cluster.setName(cluster_name.getValue());
    	
		return cluster;
	}
	
	 private List<Env> buildExtraEnv(Cluster cluster) {
	        List<Env> extraEnvs = new LinkedList<>();
//	        GlobalInfo info = globalMapper.getGlobalInfoByType(GlobalType.SERVER);
//	        if (info == null) {
//	            throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "domeos api is null!");
//	        }
//	        extraEnvs.add(new EnvDraft("DOMEOS_SERVER_ADDR", CommonUtil.fullUrl(info.getValue())));
	        extraEnvs.add(new Env("CLUSTER_NAME", cluster.getName()));
	        return extraEnvs;
	    }
	 
	/**
	 * 检查启动服务的依赖服务是否是运行态
	 * @param appId
	 * @param startSeq
	 */
	public void checkStartSeq(String appId,int startSeq){
		if(startSeq<1){
			return;
		}
		List<ServiceInfo> services = serviceDao.getNoRunningServicesByStartSeq(appId,startSeq);
		if(services!=null && services.size()>0){
			String msg="";
			for(ServiceInfo service:services){
				msg += service.getId()+"  ";
			}
			 throw ApiException.wrapMessage(ResultStat.SERVICE_START_FAILED, "hasn't start depend services:"+msg);
		}
	}
	
	@Override
	public List<String> getServiceURLs(String serviceId)throws Exception{
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
		if(serviceInfo == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service:" + serviceId);
		}
		
		ServiceConfigInfo service = serviceInfo.toModel(ServiceConfigInfo.class);
		
		if(!service.isExternal()){
			return null;
		}
		Cluster cluster = getCluster();
		AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
		
		return getServiceURLs(cluster,appInfo.getNamespace(),appInfo.getHostLabel(),service.getNodePorts());
	}
	
	private List<String> getServiceURLs(Cluster cluster, String namespace, String label, List<NodePortDraft> nodeports) throws Exception{
		if(nodeports==null || nodeports.size()==0){
        	return null;
        }
        NodeWrapper nodeWrapper = new NodeWrapper().init(cluster, namespace);
        
        Map<String, String> nodeSelector = new HashMap<>();
        if(!StringUtils.isEmpty(label)){
        	nodeSelector.put(label, GlobalConstant.LABEL_VALUE);
        }else{
        	return null;
        }
        
        List<NodeInfo> nodeList = nodeWrapper.getNodeListByLabel(nodeSelector);
        
        if(nodeList==null || nodeList.size()==0){
        	return null;
        }
        
        List<String> urls = new ArrayList<>(nodeports.size());
        String nodeIp = nodeList.get(0).getIp();
        for(NodePortDraft nodeport:nodeports){
        	String url = nodeIp+":"+nodeport.getNodePort();
        	if(nodeport.getProtocol().equals(LoadBalancerProtocol.HTTP)){
        		urls.add(CommonUtil.fullUrl(url));
        	}else{
        		urls.add(url);
        	}
        }
        
        return urls;
	}
}
