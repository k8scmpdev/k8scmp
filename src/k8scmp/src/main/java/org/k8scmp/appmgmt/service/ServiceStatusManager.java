package org.k8scmp.appmgmt.service;

import java.io.IOException;
import java.util.List;

import org.k8scmp.appmgmt.domain.DeploymentSnapshot;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.exception.DeploymentTerminatedException;
import org.k8scmp.login.domain.User;
import org.k8scmp.model.DeployOperation;
import org.k8scmp.model.ServiceStatus;

public interface ServiceStatusManager {

	void checkStateAvailable(ServiceStatus curState, ServiceStatus dstState);

	String registerEvent(String serviceId, DeployOperation operation, User user, List<DeploymentSnapshot> srcSnapshot,
			List<DeploymentSnapshot> currentSnapshot, List<DeploymentSnapshot> dstSnapshot)
			throws DeploymentEventException, IOException;

	String registerAbortEvent(String serviceId, User user) throws DeploymentEventException, IOException;

	void freshEvent(String id, List<DeploymentSnapshot> currentSnapshot) throws IOException, DeploymentEventException;

	void succeedEvent(String id, List<DeploymentSnapshot> currentSnapshot)
			throws IOException, DeploymentEventException, DeploymentTerminatedException;

	void failedEvent(String id, List<DeploymentSnapshot> currentSnapshot, String message)
			throws IOException, DeploymentEventException, DeploymentTerminatedException;

	void failedEventForDeployment(String deploymentId, List<DeploymentSnapshot> currentSnapshot, String message)
			throws IOException, DeploymentEventException, DeploymentTerminatedException;

}
