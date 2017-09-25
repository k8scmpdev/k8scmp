package org.k8scmp.engine.k8s.handler;

import java.util.List;

import org.k8scmp.appmgmt.domain.Env;
import org.k8scmp.appmgmt.domain.Policy;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.exception.K8sDriverException;


/**
 */
public interface DeployResourceHandler<T> {

    T build(Version version, List<Env> extraEnvs);

    T create(Version version, List<Env> extraEnvs) throws K8sDriverException;

    void delete() throws K8sDriverException;

    T scaleUp(Version version, int replicas) throws K8sDriverException;

    T scaleDown(Version version, int replicas) throws K8sDriverException;

    T update(Version version,List<Env> extraEnvs, Policy policy, long eventId, int targetVersion) throws K8sDriverException;
   
    T rollback(Version version,List<Env> extraEnvs, Policy policy, long eventId, int targetVersion) throws K8sDriverException;

    Boolean abortUpdateOrRollBack() throws K8sDriverException;

    void abortScales() throws K8sDriverException;

    T abort();

    void removeOtherDeploy(int versionId) throws K8sDriverException;

    List queryDesiredSnapshot() throws K8sDriverException;

}
