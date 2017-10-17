package org.k8scmp.appmgmt.dao;

import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;

import java.util.List;

public interface VersionDao{

	long insertVersion(Version version);

    void deleteAllVersion(String serviceId);

    Integer getMaxVersion(String serviceId);
    
    VersionBase getVersion(String serviceId,int version);

    List<VersionBase> getAllVersionByServiceId(String serviceId);

	void deleteVersionById(String id);

	void updateVersion(Version version);
}
