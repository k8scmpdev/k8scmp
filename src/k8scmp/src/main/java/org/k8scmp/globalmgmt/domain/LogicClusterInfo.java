package org.k8scmp.globalmgmt.domain;

import java.util.List;

import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;

/**
 * Created by jason on 2017/10/17.
 */
public class LogicClusterInfo {

    private String name;
    private String description;
    private int hostNum;
    private List<NodeInfo> nodeList;

    public LogicClusterInfo() {
    }
    
    public LogicClusterInfo(String name, int hostNum) {
        this.setName(name);
        this.setHostNum(hostNum);
    }
    
    public LogicClusterInfo(String name, int hostNum,String description) {
        this.setName(name);
        this.setHostNum(hostNum);
        this.setDescription(description);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHostNum() {
		return hostNum;
	}

	public void setHostNum(int hostNum) {
		this.hostNum = hostNum;
	}

	public List<NodeInfo> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<NodeInfo> nodeList) {
		this.nodeList = nodeList;
	}

}
