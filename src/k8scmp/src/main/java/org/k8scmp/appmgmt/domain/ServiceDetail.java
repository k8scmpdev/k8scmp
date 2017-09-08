package org.k8scmp.appmgmt.domain;

import java.util.List;

public class ServiceDetail{
	private ServiceConfigInfo serviceConfigInfo;
	private List<Version> versions;

	public ServiceConfigInfo getServiceConfigInfo() {
		return serviceConfigInfo;
	}

	public void setServiceConfigInfo(ServiceConfigInfo serviceConfigInfo) {
		this.serviceConfigInfo = serviceConfigInfo;
	}
	
	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}
}
