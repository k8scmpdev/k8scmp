package org.k8scmp.appmgmt.domain;

import java.util.List;

import org.k8scmp.model.VersionType;



public class ServiceConfigInfo extends ServiceInfo{
	private int defaultReplicas;
	private List<String> externalIPs;
	private String yamlPodSpec;
	private VersionType versionType;
	private boolean isExternal = false;
	private List<NodePortDraft> nodePorts;
	 
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
	public String getYamlPodSpec() {
		return yamlPodSpec;
	}
	public void setYamlPodSpec(String yamlPodSpec) {
		this.yamlPodSpec = yamlPodSpec;
	}
	public VersionType getVersionType() {
		return versionType;
	}
	public void setVersionType(VersionType versionType) {
		this.versionType = versionType;
	}
	public List<NodePortDraft> getNodePorts() {
		return nodePorts;
	}
	public void setNodePorts(List<NodePortDraft> nodePorts) {
		this.nodePorts = nodePorts;
	}
	public boolean isExternal() {
		return isExternal;
	}
	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}
}
