package org.k8scmp.appmgmt.dao;

import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;

import java.util.List;

public interface ServiceDao{

    long createService(ServiceConfigInfo service);

    void updateService(ServiceInfo serviceInfo);
    
    void updateDescription(ServiceInfo serviceInfo);

    void deleteService(String id);

    ServiceInfo getService(String id);

	List<ServiceInfo> getServices(ServiceInfo serviceInfo);

	List<ServiceInfo> getServicesByAppId(String appId);

	String getServiceStatu(String id);
	
	void updateServiceStatu(String state,String id);

	List<ServiceInfo> getNoRunningServicesByStartSeq(String appId, int startSeq);


}
