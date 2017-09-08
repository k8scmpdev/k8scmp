package org.k8scmp.appmgmt.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.dao.VersionDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.service.ServiceService;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.exception.ApiException;
import org.k8scmp.model.ServiceStatus;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.operation.OperationType;
import org.k8scmp.util.DateUtil;
import org.k8scmp.util.StringUtils;
import org.k8scmp.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    private static Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

	@Override
	public String createService(
			ServiceDetail serviceDetail) {
		if(serviceDetail==null){
			 throw ApiException.wrapMessage(ResultStat.DEPLOYMENT_NOT_LEGAL, "service is null");
		}
		ServiceConfigInfo service = serviceDetail.getServiceConfigInfo();
		String serviceCode = service.getServiceCode();
		String appId = service.getAppId();
		
		AppInfo currentApp = appDao.getApp(appId);
		if(currentApp == null){
			throw ApiException.wrapMessage(ResultStat.DEPLOYMENT_NOT_LEGAL, "application is not exsited");
		}
		List<AppInfo> apps = appDao.getAppsByserviceCode(serviceCode);
		if (apps != null && apps.size() != 0) {
            for (AppInfo one : apps) {
                if (one.getClusterId() == currentApp.getClusterId() &&
                        one.getNamespace().equals(currentApp.getNamespace())) {
                    throw ApiException.wrapResultStat(ResultStat.DEPLOYMENT_EXIST);
                }
            }
        }
		String serviceId = UUIDUtil.generateUUID();
		service.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
		service.setCreateTime("");
		service.setId(serviceId);
		service.setState(ServiceStatus.STOP.name());
		
		serviceDao.createService(service);
		
		List<Version> versions = serviceDetail.getVersions();
		
		if(versions == null || versions.size()!=1){
			throw ApiException.wrapMessage(ResultStat.DEPLOYMENT_NOT_LEGAL, "service config information cannot be None or more than one");
		}
		
		Version version = versions.get(0);
		String errInfo = version.checkLegality();
        if (!StringUtils.isBlank(errInfo)) {
            throw new ApiException(ResultStat.DEPLOYMENT_NOT_LEGAL,errInfo);
        }
        version.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
		version.setCreateTime("");
		version.setId(serviceId);
		version.setState("");
		
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
				"", 
				"", 
				"OK", 
				"", 
				DateUtil.dateFormatToMillis(new Date())
		));
		
		return serviceId;
		
		
	}


	@Override
	public void deleteService(String id) {
		String statu = serviceDao.getServiceStatu(id);
		if(ServiceStatus.STOP.name().equals(statu)){
			throw ApiException.wrapMessage(ResultStat.CANNOT_DELETE_DEPLOYMENT, "service statu is "+statu+" now");
		}
		serviceDao.deleteService(id);
		versionDao.deleteAllVersion(id);
		
		operationLog.insertRecord(new OperationRecord(
				id, 
				ResourceType.SERVICE,
				OperationType.DELETE, 
				"", 
				"", 
				"OK", 
				"", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}


	@Override
	public void modifyService(ServiceInfo serviceInfo) {
		serviceInfo.setLastModifierId("");
		serviceInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		serviceDao.updateService(serviceInfo);
		operationLog.insertRecord(new OperationRecord(
				serviceInfo.getId(), 
				ResourceType.SERVICE,
				OperationType.MODIFY, 
				"", 
				"", 
				"OK", 
				"", 
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
	
}
