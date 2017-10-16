package org.k8scmp.monitormgmt.domain.monitor;

public class InstenceInfoBack {
	private String instanceName;
	private String appName;
	private String serviceName;
	private String CPUUsed;
	private String memoryUsed;
	private String netInput;
	private String netOutput;
	
	public InstenceInfoBack(){
		
	}
	public InstenceInfoBack(String instanceName, String appName, String serviceName, String cPUUsed, String memoryUsed,
			String netInput, String netOutput) {
		this.instanceName = instanceName;
		this.appName = appName;
		this.serviceName = serviceName;
		this.CPUUsed = cPUUsed;
		this.memoryUsed = memoryUsed;
		this.netInput = netInput;
		this.netOutput = netOutput;
	}
	
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getCPUUsed() {
		return CPUUsed;
	}
	public void setCPUUsed(String cPUUsed) {
		CPUUsed = cPUUsed;
	}
	public String getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(String memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	public String getNetInput() {
		return netInput;
	}
	public void setNetInput(String netInput) {
		this.netInput = netInput;
	}
	public String getNetOutput() {
		return netOutput;
	}
	public void setNetOutput(String netOutput) {
		this.netOutput = netOutput;
	}
	
	
}
