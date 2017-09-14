package org.k8scmp.monitormgmt.domain.monitor;

public class NodeInfoBack {
//	private String phyCluster;
	private String logicCluster;
	private String hostIP;
	private String state;
	private String CPUTotal;
	private String memoryTotal;
	private String diskTotal;
	private String ioinput;
	private String iooutput;
	
//	public String getPhyCluster() {
//		return phyCluster;
//	}
//	
//	public void setPhyCluster(String phyCluster) {
//		this.phyCluster = phyCluster;
//	}
	
	public String getLogicCluster() {
		return logicCluster;
	}
	
	public void setLogicCluster(String logicCluster) {
		this.logicCluster = logicCluster;
	}
	
	public String getHostIP() {
		return hostIP;
	}
	
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getCPUTotal() {
		return CPUTotal;
	}
	
	public void setCPUTotal(String cPUTotal) {
		CPUTotal = cPUTotal;
	}
	
	public String getMemoryTotal() {
		return memoryTotal;
	}
	
	public void setMemoryTotal(String memoryTotal) {
		this.memoryTotal = memoryTotal;
	}
	
	public String getDiskTotal() {
		return diskTotal;
	}
	
	public void setDiskTotal(String diskTotal) {
		this.diskTotal = diskTotal;
	}
	
	public String getIoinput() {
		return ioinput;
	}
	
	public void setIoinput(String ioinput) {
		this.ioinput = ioinput;
	}
	
	public String getIooutput() {
		return iooutput;
	}
	
	public void setIooutput(String iooutput) {
		this.iooutput = iooutput;
	}
	
	
}
