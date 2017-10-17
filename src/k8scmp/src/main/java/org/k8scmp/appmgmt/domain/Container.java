package org.k8scmp.appmgmt.domain;

/**
 */
public class Container {
    String containerId;
    String containerName;
    String imageName;

    public Container() {
    }

    public Container(String containerId, String containerName, String imageName) {
        this.containerId = containerId;
        this.containerName = containerName;
        this.imageName = imageName;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
