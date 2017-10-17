package org.k8scmp.appmgmt.service;

import java.util.List;

import org.k8scmp.appmgmt.domain.Version;



public interface VersionService {
    String createVersion(String serviceId, Version version);
    
    Version getVersion(String serviceId, int ver);

    List<Version> listVersion(String serviceId);

    void deprecateVersion(String serviceId, int ver) throws Exception;
    
    void enableVersion(String serviceId, int ver);

}
