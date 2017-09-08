package org.k8scmp.appmgmt.dao.impl;

import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.dao.VersionDao;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;
import org.k8scmp.mapper.appmgmt.ServiceMapper;
import org.k8scmp.mapper.appmgmt.VersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("versionDao")
public class VersionDaoImpl implements VersionDao {

    @Autowired
    VersionMapper mapper;

	@Override
	public long insertVersion(Version version) {
		return mapper.insertVersion(version, version.toString());
	}

	@Override
	public void deleteAllVersion(String serviceId) {
		mapper.deleteAllVersion(serviceId);
	}

	@Override
	public void updateLabelSelector(String id, String data) {
		mapper.updateLabelSelector(id, data);
	}

	@Override
	public Integer getMaxVersion(String serviceId) {
		return mapper.getMaxVersion(serviceId);
	}

	@Override
	public VersionBase getVersion(String serviceId, int version) {
		return mapper.getVersion(serviceId, version);
	}

	@Override
	public List<VersionBase> getAllVersionByServiceId(String serviceId) {
		return mapper.getAllVersionByServiceId(serviceId);
	}

	
}
