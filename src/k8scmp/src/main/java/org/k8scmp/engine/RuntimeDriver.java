package org.k8scmp.engine;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.Env;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;
import org.k8scmp.engine.exception.DriverException;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.exception.DeploymentTerminatedException;
import org.k8scmp.exception.K8sDriverException;
import org.k8scmp.login.domain.User;

/**
 */
public interface RuntimeDriver {

    void updateList(Cluster cluster);

    RuntimeDriver init(Cluster cluster);

    boolean isDriverLatest(Cluster cluster);

    // Operation
    void startDeploy(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, Version version, User user, List<Env> allExtraEnvs)
            throws DriverException,IOException, DeploymentEventException;

	void createLoadBalancer(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo) throws K8sDriverException, DriverException;

    void stopDeploy(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, User user) throws  IOException, DeploymentEventException;

	void stopLoadBalancer(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo) throws DriverException;

	void rollbackDeploy(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, int ver, List<Env> allExtraEnvs,
			User user) throws IOException, DeploymentTerminatedException, DeploymentEventException;

	void startUpdate(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, int ver, List<Env> allExtraEnvs, User user)
			throws IOException, DeploymentTerminatedException, DeploymentEventException;

	void scaleDownDeployment(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, int versionId, int replicas, List<Env> allExtraEnvs,
			User user) throws DeploymentEventException, IOException, DeploymentTerminatedException;

	void scaleUpDeployment(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, int versionId, int replicas, List<Env> allExtraEnvs,
			User user) throws DeploymentEventException, IOException, DeploymentTerminatedException;

	List<VersionBase> getCurrnetVersionsByService(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo)
			throws DeploymentEventException;

	void checkBasicEvent(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, DeployEvent event)
			throws DeploymentEventException, IOException, ParseException,
			DeploymentTerminatedException;

	void checkStopEvent(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, DeployEvent event)
			throws DeploymentEventException, IOException, DeploymentTerminatedException;

	long getTotalReplicasByDeployment(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo)
			throws DeploymentEventException;

	void expiredEvent(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, DeployEvent event)
			throws DeploymentEventException, IOException, DeploymentTerminatedException;

//    void abortDeployOperation(ServiceConfigInfo serviceConfigInfo, User user)
//            throws  IOException;
//
//    void checkBasicEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event)
//            throws  IOException, ParseException;
//
//    void checkAbortEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event)
//            throws  IOException;
//
//    void checkStopEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event)
//            throws  IOException;
//
//    void expiredEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event) throws  IOException;
//
//    List<Version> getCurrnetVersionsByDeployment(ServiceConfigInfo serviceConfigInfo) throws Exception;
//
//    long getTotalReplicasByDeployment(ServiceConfigInfo serviceConfigInfo) throws Exception ;
//    
//    void deletePodByDeployIdAndInsName(ServiceConfigInfo serviceConfigInfo, String insName)
//            throws  IOException;
}
