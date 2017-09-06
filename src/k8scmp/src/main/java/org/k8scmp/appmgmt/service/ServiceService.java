package org.k8scmp.appmgmt.service;

import java.util.List;

import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;

/**
 * Created by KaiRen on 2016/9/22.
 */
public interface ServiceService {
    Long createService(ServiceConfigInfo serviceConfigInfo);

    void deleteService(String id);

    void modifyService(ServiceInfo serviceInfo);

    List<ServiceConfigInfo> listServices(ServiceInfo serviceInfo);

	List<ServiceConfigInfo> getServicesByAppId(String appId);
}
