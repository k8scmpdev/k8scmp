package org.k8scmp.appmgmt.domain;

public class NodePortDraft {
	private int nodePort;
	private int containerPort;
	private String protocol;
	private String description;
	
	public NodePortDraft() {
	}
	
	public NodePortDraft(int nodePort, int containerPort) {
	    this.setNodePort(nodePort);
	    this.setContainerPort(containerPort);
	}

	public int getNodePort() {
		return nodePort;
	}

	public void setNodePort(int nodePort) {
		this.nodePort = nodePort;
	}

	public int getContainerPort() {
		return containerPort;
	}

	public void setContainerPort(int containerPort) {
		this.containerPort = containerPort;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	   
}
