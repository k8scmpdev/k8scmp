package org.k8scmp.appmgmt.service;

import java.util.List;

import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.ServiceInfo;

/**
 */
public interface ServiceService {
    String createService(ServiceDetail serviceDetail);

    void deleteService(String id);

    void modifyService(ServiceInfo serviceInfo);

    List<ServiceConfigInfo> listServices(ServiceInfo serviceInfo);

	List<ServiceConfigInfo> getServicesByAppId(String appId);
}
