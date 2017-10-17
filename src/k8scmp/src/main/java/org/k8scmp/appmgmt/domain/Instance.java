package org.k8scmp.appmgmt.domain;

import java.util.LinkedList;
import java.util.List;

/**
 */
public class Instance {
    private String serviceId;
    private String serviceCode;
    private String namespace;
    private int version;
	private String instanceName;
    private String startTime;
    private String hostName;
    private String podIp;
    private String hostIp;
    private List<Container> containers;
    private String status;

    public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getPodIp() {
		return podIp;
	}
	public void setPodIp(String podIp) {
		this.podIp = podIp;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public List<Container> getContainers() {
		return containers;
	}
	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}
	
	public void addContainer(Container container) {
        if (containers == null) {
            containers = new LinkedList<>();
        }
        if (container != null) {
            containers.add(container);
        }
    }
	   
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
