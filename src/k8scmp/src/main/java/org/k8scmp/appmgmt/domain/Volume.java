package org.k8scmp.appmgmt.domain;

import org.k8scmp.util.StringUtils;

public class Volume {
    private String name;
    private VolumeType volumeType;
    private String hostPath;
    private String emptyDir;
    private VolumePVC volumePVC;
    private VolumeConfigMap volumeConfigMap;

    public String getName() {
        return name;
    }

    public Volume setName(String name) {
        this.name = name;
        return this;
    }

    public VolumeType getVolumeType() {
        return volumeType;
    }

    public Volume setVolumeType(VolumeType volumeType) {
        this.volumeType = volumeType;
        return this;
    }

    public String getHostPath() {
        return hostPath;
    }

    public Volume setHostPath(String hostPath) {
        this.hostPath = hostPath;
        return this;
    }

    public String getEmptyDir() {
        return emptyDir;
    }

    public Volume setEmptyDir(String emptyDir) {
        this.emptyDir = emptyDir;
        return this;
    }

    public VolumePVC getVolumePVC() {
        return volumePVC;
    }

    public Volume setVolumePVC(VolumePVC volumePVC) {
        this.volumePVC = volumePVC;
        return this;
    }

    public VolumeConfigMap getVolumeConfigMap() {
        return volumeConfigMap;
    }

    public Volume setVolumeConfigMap(VolumeConfigMap volumeConfigMap) {
        this.volumeConfigMap = volumeConfigMap;
        return this;
    }
    
    public String checkLegality() {
        if (StringUtils.isBlank(getName())) {
            return "name must be set";
        }
        if (!StringUtils.checkVolumeNamePattern(getName())) {
            return "name must match pattern ^[a-z0-9]([-a-z0-9]*[a-z0-9])?$";
        }
        return null;
    }
}
