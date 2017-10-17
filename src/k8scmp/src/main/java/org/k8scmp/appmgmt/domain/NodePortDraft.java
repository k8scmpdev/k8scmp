package org.k8scmp.appmgmt.domain;

import org.k8scmp.model.LoadBalancerProtocol;

public class NodePortDraft {
	private int nodePort;
	private int targetPort;
	private String context;
	private LoadBalancerProtocol protocol = LoadBalancerProtocol.TCP;
	private String description;
	
	public NodePortDraft() {
	}
	
	public NodePortDraft(int nodePort, int targetPort) {
	    this.setNodePort(nodePort);
	    this.setTargetPort(targetPort);
	}

	public int getNodePort() {
		return nodePort;
	}

	public void setNodePort(int nodePort) {
		this.nodePort = nodePort;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LoadBalancerProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(LoadBalancerProtocol protocol) {
		this.protocol = protocol;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}
	   
}
