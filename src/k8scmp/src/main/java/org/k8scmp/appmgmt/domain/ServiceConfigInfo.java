package org.k8scmp.appmgmt.domain;

import java.util.List;

public class ServiceConfigInfo extends ServiceInfo{
	private int defaultReplicas;
	private List<String> externalIPs;
	 
	public int getDefaultReplicas() {
		return defaultReplicas;
	}
	public void setDefaultReplicas(int defaultReplicas) {
		this.defaultReplicas = defaultReplicas;
	}
	public List<String> getExternalIPs() {
		return externalIPs;
	}
	public void setExternalIPs(List<String> externalIPs) {
		this.externalIPs = externalIPs;
	}
}
