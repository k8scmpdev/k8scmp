package org.k8scmp.appmgmt.domain;

import org.k8scmp.util.StringUtils;

public class VolumeMount {
    private String name; // name of volume, this must be set in deployment
    private boolean readOnly = false;
    private String mountPath;
    private String subPath;

    public String getName() {
        return name;
    }

    public VolumeMount setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public VolumeMount setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public String getMountPath() {
        return mountPath;
    }

    public VolumeMount setMountPath(String mountPath) {
        this.mountPath = mountPath;
        return this;
    }

    public String getSubPath() {
        return subPath;
    }

    public VolumeMount setSubPath(String subPath) {
        this.subPath = subPath;
        return this;
    }

    public String checkLegality() {
        if (StringUtils.isBlank(name)) {
            return "volume name in container must be set";
        }
        if (!StringUtils.checkVolumeNamePattern(name)) {
            return "name must match pattern ^[a-z0-9]([-a-z0-9]*[a-z0-9])?$";
        }
        if (StringUtils.isBlank(mountPath)) {
            return "mount path of name(" + name + ") must be set";
        }
        return null;
    }
}
