package org.k8scmp.appmgmt.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.PodSpec;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.k8scmp.engine.model.CustomObjectMapper;
import org.k8scmp.engine.model.CustomYamlObjectMapper;
import org.k8scmp.model.VersionType;
import org.k8scmp.util.StringUtils;

/**
 */
public class Version extends VersionBase {
    private String serviceId;  // separate column
    private int version = 0; //version id for the deploy, separate column
    private List<ContainerDraft> containerDrafts; // describe container configs for each container in this pod
    private List<VolumeDraft> volumeDrafts; // describe volume configurations
    private LogDraft logDraft;
    private List<LabelSelector> labelSelectors;
    private String podSpecStr;
    private VersionType versionType;
    private boolean deprecate = false;
    private List<String> hostList;
    
    public List<ContainerDraft> getContainerDrafts() {
        return containerDrafts;
    }

    public List<LabelSelector> getLabelSelectors() {
        return labelSelectors;
    }

    public void setLabelSelectors(List<LabelSelector> labelSelectors) {
        this.labelSelectors = labelSelectors;
    }

    public void setContainerDrafts(List<ContainerDraft> containerDrafts) {
        this.containerDrafts = containerDrafts;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<VolumeDraft> getVolumeDrafts() {
        return volumeDrafts;
    }

    public Version setVolumeDrafts(List<VolumeDraft> volumeDrafts) {
        this.volumeDrafts = volumeDrafts;
        return this;
    }

    public String getPodSpecStr() {
        return podSpecStr;
    }

    public void setPodSpecStr(String podSpecStr) {
        this.podSpecStr = podSpecStr;
    }

    public VersionType getVersionType() {
        return versionType;
    }

    public void setVersionType(VersionType versionType) {
        this.versionType = versionType;
    }

    public boolean isDeprecate() {
        return deprecate;
    }

    public Version setDeprecate(boolean deprecate) {
        this.deprecate = deprecate;
        return this;
    }
    
    public List<String> getHostList() {
        return hostList;
    }

    public void setHostList(List<String> hostList) {
        this.hostList = hostList;
    }

    public String checkLegality() {
        if (!StringUtils.isBlank(podSpecStr)) {
            PodSpec podSpec = toPodSpec();
            if (podSpec == null) {
                return "something wrong with pod spec definition";
            } else {
                String checkAdditionalProperties = podSpec.toString();
                if (checkAdditionalProperties != null && checkAdditionalProperties.contains("additionalProperties")) {
                    Pattern additionalPropertiesPattern = Pattern.compile("(additionalProperties=\\{)([^\\}]+)\\}");
                    Matcher matcher = additionalPropertiesPattern.matcher(checkAdditionalProperties);
                    if (matcher.find()) {
                        String parameter = matcher.group(2);
                        return "\"" + parameter + "\" is wrong PodSpec yaml/json definition";
                    }
                }
            }
        }
        if (containerDrafts != null) {
            for (ContainerDraft containerDraft : containerDrafts) {
                if (!StringUtils.isBlank(containerDraft.checkLegality())) {
                    return containerDraft.checkLegality();
                } else if (containerDraft.getVolumeMountDrafts() != null) {
                    for (VolumeMountDraft volumeMountDraft : containerDraft.getVolumeMountDrafts()) {
                        if (!checkVolume(volumeMountDraft.getName())) {
                            return "volume name(" + volumeMountDraft.getName() + ") error, check deployment volume drafts configuration";
                        }
                    }
                }
            }
        }
        return "";
    }

    public PodSpec toPodSpec() {
        try {
            if (versionType == VersionType.CUSTOM) {
                return null;
            } else if (versionType == VersionType.YAML) {
                ObjectMapper objectMapper = new CustomYamlObjectMapper();
                return objectMapper.readValue(podSpecStr, PodSpec.class);
            } else if (versionType == VersionType.JSON) {
                ObjectMapper objectMapper = new CustomObjectMapper();
                return objectMapper.readValue(podSpecStr, PodSpec.class);
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private boolean checkVolume(String volumeName) {
        if (volumeDrafts == null || StringUtils.isBlank(volumeName)) {
            return false;
        }
        for (VolumeDraft volumeDraft : volumeDrafts) {
            if (volumeName.equals(volumeDraft.getName())) {
                return true;
            }
        }
        return false;
    }

	public LogDraft getLogDraft() {
		return logDraft;
	}

	public void setLogDraft(LogDraft logDraft) {
		this.logDraft = logDraft;
	}
}