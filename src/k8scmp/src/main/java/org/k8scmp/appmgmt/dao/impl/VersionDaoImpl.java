package org.k8scmp.appmgmt.dao.impl;

import org.k8scmp.appmgmt.dao.VersionDao;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;
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
		Integer verNow = mapper.getMaxVersion(version.getServiceId());
        if (verNow == null) {
            verNow = 0;
        }
        verNow++;
        version.setVersion(verNow);
        version.setVersionName("version" + verNow);
		return mapper.insertVersion(version, version.toString());
	}

	@Override
	public void deleteAllVersion(String serviceId) {
		mapper.deleteAllVersion(serviceId);
	}
	
	@Override
	public void deleteVersionById(String id) {
		mapper.deleteVersionById(id);
	}

	@Override
	public void updateVersion(Version version) {
		mapper.updateVersion(version, version.toString());
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

	@Override
	public List<Version> getVersionNames(String serviceId) {
		return mapper.getVersionNames(serviceId);
	}

	@Override
	public void deleteVersionByServiceId(String serviceId, int version) {
		mapper.deleteVersionByServiceId(serviceId, version);
		
	}
}
