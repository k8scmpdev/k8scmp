package org.k8scmp.engine.k8s.util;

import java.util.List;

import org.k8scmp.globalmgmt.domain.ClusterInfo;

public class LabelInfo {
	private String labelName;
	private ClusterInfo clusterInfo;
	private String namespace;
	private int hostCount;
	private List<String> nodeName;
	
	public LabelInfo(){
		
	}
	
	public LabelInfo(String labelName, ClusterInfo clusterInfo, String namespace, int hostCount, List<String> nodeName){
		this.labelName = labelName;
		this.clusterInfo = clusterInfo;
		this.namespace = namespace;
		this.hostCount = hostCount;
		this.nodeName = nodeName;
	}
	
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public ClusterInfo getClusterInfo() {
		return clusterInfo;
	}
	public void setClusterInfo(ClusterInfo clusterInfo) {
		this.clusterInfo = clusterInfo;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public int getHostCount() {
		return hostCount;
	}
	public void setHostCount(int hostCount) {
		this.hostCount = hostCount;
	}
	public List<String> getNodeName() {
		return nodeName;
	}
	public void setNodeName(List<String> nodeName) {
		this.nodeName = nodeName;
	}
	
	
}
