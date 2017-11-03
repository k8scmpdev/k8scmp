package org.k8scmp.monitormgmt.domain.alarm;


/**
 * Created by baokangwang on 2016/3/31.
 */
public class DeploymentInfo {

    private String id;
//    private String clusterName;
    private String deploymentName;
//    private HostEnv hostEnv;

    public DeploymentInfo() {
    }

//    public DeploymentInfo(Deployment deployment) {
//        this.id = deployment.getId();
//        this.clusterName = deployment.getClusterName();
//        this.deploymentName = deployment.getName();
//        this.hostEnv = deployment.getHostEnv();
//    }

    public DeploymentInfo(String id, String deploymentName) {
        this.id = id;
//        this.clusterName = clusterName;
        this.deploymentName = deploymentName;
//        this.hostEnv = hostEnv;
    }

    public String getId() {
        return id;
   }

    public void setId(String id) {
        this.id = id;
    }

//    public String getClusterName() {
//        return clusterName;
//    }

//    public void setClusterName(String clusterName) {
//        this.clusterName = clusterName;
//    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

//    public HostEnv getHostEnv() {
//        return hostEnv;
//    }

//    public void setHostEnv(HostEnv hostEnv) {
//        this.hostEnv = hostEnv;
//    }
}

