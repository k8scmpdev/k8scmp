package org.k8scmp.appmgmt.domain;

public enum VolumeType {
    HOSTPATH("hostPath"),
    EMPTYDIR("emptyDir"),
    PERSISTENTVOLUMECLAIM("persistentVolumeClaim"),
    CONFIGMAP("configMap");

    private String type;

    VolumeType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public VolumeType setType(String type) {
        this.type = type;
        return this;
    }
}
