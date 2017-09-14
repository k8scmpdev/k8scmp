package org.k8scmp.engine;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.Env;
import org.k8scmp.appmgmt.domain.Policy;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.engine.exception.DriverException;
import org.k8scmp.login.domain.User;

/**
 */
public interface RuntimeDriver {

    void updateList(Cluster cluster);

    RuntimeDriver init(Cluster cluster);

    boolean isDriverLatest(Cluster cluster);

    // Operation
    void startDeploy(ServiceConfigInfo serviceConfigInfo, Version version, User user, List<Env> allExtraEnvs)
            throws DriverException,  IOException;

    void stopDeploy(ServiceConfigInfo serviceConfigInfo, User user) throws  IOException;

    void rollbackDeploy(ServiceConfigInfo serviceConfigInfo, int versionId, List<Env> allExtraEnvs, User user, Policy policy)
            throws IOException;

    void startUpdate(ServiceConfigInfo serviceConfigInfo, int version, List<Env> allExtraEnvs, User user, Policy policy)
            throws IOException;

    void abortDeployOperation(ServiceConfigInfo serviceConfigInfo, User user)
            throws  IOException;

    void scaleUpDeployment(ServiceConfigInfo serviceConfigInfo, int version, int replicas, List<Env> allExtraEnvs, User user)
            throws IOException;

    void scaleDownDeployment(ServiceConfigInfo serviceConfigInfo, int version, int replicas, List<Env> allExtraEnvs, User user)
            throws  IOException;

    void checkBasicEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event)
            throws  IOException, ParseException;

    void checkAbortEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event)
            throws  IOException;

    void checkStopEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event)
            throws  IOException;

    void expiredEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event) throws  IOException;

    List<Version> getCurrnetVersionsByDeployment(ServiceConfigInfo serviceConfigInfo) throws Exception;

    long getTotalReplicasByDeployment(ServiceConfigInfo serviceConfigInfo) throws Exception ;
    
    void deletePodByDeployIdAndInsName(ServiceConfigInfo serviceConfigInfo, String insName)
            throws  IOException;
}
