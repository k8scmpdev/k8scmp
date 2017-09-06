package org.k8scmp.appmgmt.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.service.ServiceService;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.model.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    ServiceDao serviceDao;
    
    private static Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

	@Override
	public Long createService(
			ServiceConfigInfo serviceConfigInfo) {
		Long result = serviceDao.createService(serviceConfigInfo);
		return result;
	}


	@Override
	public void deleteService(String id) {
		serviceDao.deleteService(id);
	}


	@Override
	public void modifyService(ServiceInfo serviceInfo) {
		serviceDao.updateService(serviceInfo);
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
