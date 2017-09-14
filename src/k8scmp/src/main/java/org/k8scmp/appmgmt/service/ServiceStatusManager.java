package org.k8scmp.appmgmt.service;

import org.k8scmp.model.ServiceStatus;

public interface ServiceStatusManager {

	void checkStateAvailable(ServiceStatus curState, ServiceStatus dstState);

}
