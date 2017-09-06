package org.k8scmp.appmgmt.dao.impl;

import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.mapper.appmgmt.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("serviceDao")
public class ServiceDaoImpl implements ServiceDao {

    @Autowired
    ServiceMapper mapper;

    @Override
    public long createService(ServiceConfigInfo service) {

        return mapper.createService(service,service.toString());
    }

    @Override
    public void updateService(ServiceInfo serviceInfo) {
        mapper.updateService(serviceInfo);
    }

	@Override
	public void deleteService(String id) {
		 mapper.deleteService(id);
	}

	@Override
	public ServiceInfo getService(String id) {
		return mapper.getService(id);
	}

	@Override
	public List<ServiceInfo> getServices(ServiceInfo serviceInfo) {
		return mapper.getServices(serviceInfo);
	}
	
	@Override
	public List<ServiceInfo> getServicesByAppId(String appId) {
		return mapper.getServicesByAppId(appId);
	}
	
}