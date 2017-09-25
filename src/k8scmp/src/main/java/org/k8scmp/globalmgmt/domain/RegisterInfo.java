package org.k8scmp.globalmgmt.domain;

import org.k8scmp.util.CommonUtil;
import org.k8scmp.util.StringUtils;

/**
 * Created by jason on 2017/9/11.
 */
public class RegisterInfo {

    private String name;
    private String url;
    private String description;

    public RegisterInfo() {
    }
    
    public RegisterInfo(String name, String url) {
        this.setName(name);
        this.setUrl(url);
    }
    
    public RegisterInfo(String name, String url,String description) {
        this.setName(name);
        this.setUrl(url);
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
    public String registryDomain() {
        return CommonUtil.domainUrl(url);
    }

    public String fullRegistry() {
        return CommonUtil.fullUrl(url);
    }

	public String checkLegality() {
        if (StringUtils.isBlank(url)) {
            return  "url and host port cannot be null at the same time";
        }
        if (url.startsWith("http://")) {
            url = "http://" + CommonUtil.domainUrl(url);
        }
        url = CommonUtil.fullUrl(url);
        return null;
    }

}
