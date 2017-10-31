package org.k8scmp.globalmgmt.domain;

/**
 * Created by feiliu206363 on 2016/1/20.
 */
public class ClusterInfo {

    private String name;
    private String apiserver;
    private String description;

    public ClusterInfo() {
    }
    
    public ClusterInfo(String name, String apiserver) {
        this.setName(name);
        this.setApiserver(apiserver);
    }
    
    public ClusterInfo(String name, String apiserver,String description) {
        this.setName(name);
        this.setApiserver(apiserver);
        this.setDescription(description);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApiserver() {
		return apiserver;
	}

	public void setApiserver(String apiserver) {
		this.apiserver = apiserver;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
