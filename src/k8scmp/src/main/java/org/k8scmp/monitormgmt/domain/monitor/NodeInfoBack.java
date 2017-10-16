package org.k8scmp.monitormgmt.domain.monitor;

public class NodeInfoBack {
//	private String phyCluster;
	private String logicCluster;
	private String hostName;
	private String state;
	private String CPUPercent;
	private String memoryPercent;
	private String diskPercent;
	private String netin;
	private String netout;
	
	public NodeInfoBack(){
		
	}
	public NodeInfoBack(String logicCluster, String hostName, String state, String cPUPercent, String memoryPercent,
			String diskPercent, String netin, String netout) {
		this.logicCluster = logicCluster;
		this.hostName = hostName;
		this.state = state;
		this.CPUPercent = cPUPercent;
		this.memoryPercent = memoryPercent;
		this.diskPercent = diskPercent;
		this.netin = netin;
		this.netout = netout;
	}
	
	public String getLogicCluster() {
		return logicCluster;
	}
	public void setLogicCluster(String logicCluster) {
		this.logicCluster = logicCluster;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCPUPercent() {
		return CPUPercent;
	}
	public void setCPUPercent(String cPUPercent) {
		CPUPercent = cPUPercent;
	}
	public String getMemoryPercent() {
		return memoryPercent;
	}
	public void setMemoryPercent(String memoryPercent) {
		this.memoryPercent = memoryPercent;
	}
	public String getDiskPercent() {
		return diskPercent;
	}
	public void setDiskPercent(String diskPercent) {
		this.diskPercent = diskPercent;
	}
	public String getNetin() {
		return netin;
	}
	public void setNetin(String netin) {
		this.netin = netin;
	}
	public String getNetout() {
		return netout;
	}
	public void setNetout(String netout) {
		this.netout = netout;
	}
	
}
