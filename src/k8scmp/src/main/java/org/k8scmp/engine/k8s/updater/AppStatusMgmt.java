package org.k8scmp.engine.k8s.updater;

import java.util.ArrayList;
import java.util.List;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.model.AppStatus;
import org.k8scmp.model.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppStatusMgmt {
	@Autowired
    ServiceDao serviceDao;
    
    @Autowired
    AppDao appDao;
    
	private static Logger logger = LoggerFactory.getLogger(AppStatusMgmt.class);
	
	private String appId;
	
	public AppStatusMgmt init(String appId) {
		this.appId = appId;
		return this;
	}
	
	public String updateAppState() throws Exception {
		List<ServiceInfo> serviceInfos = serviceDao.getServicesByAppId(appId);
		if (serviceInfos == null || serviceInfos.size() == 0) {
			return null;
		}

		List<String> serviceStatus = new ArrayList<>();
		for (ServiceInfo serviceInfo : serviceInfos) {
			serviceStatus.add(serviceInfo.getState());
		}

		if (serviceStatus.contains(ServiceStatus.ERROR.name())) {
			appDao.updateAppState(appId, AppStatus.ERROR.name());
			return AppStatus.ERROR.name();
		} else if (serviceStatus.contains(ServiceStatus.RUNNING.name())
				|| serviceStatus.contains(ServiceStatus.UPDATE_ABORTED.name())
				|| serviceStatus.contains(ServiceStatus.BACKROLL_ABORTED.name())) {
			appDao.updateAppState(appId, AppStatus.RUNNING.name());
			return AppStatus.RUNNING.name();
		} else if (serviceStatus.contains(ServiceStatus.UPSCALING.name())
				|| serviceStatus.contains(ServiceStatus.DOWNSCALING.name())
				|| serviceStatus.contains(ServiceStatus.UPDATING.name())
				|| serviceStatus.contains(ServiceStatus.BACKROLLING.name())
				|| serviceStatus.contains(ServiceStatus.DEPLOYING.name())
				|| serviceStatus.contains(ServiceStatus.STOPPING.name())
				|| serviceStatus.contains(ServiceStatus.ABORTING.name())) {
			appDao.updateAppState(appId, AppStatus.OPERATING.name());
			return AppStatus.OPERATING.name();
		} else {
			appDao.updateAppState(appId, AppStatus.STOP.name());
			return AppStatus.STOP.name();
		}
	}
}
