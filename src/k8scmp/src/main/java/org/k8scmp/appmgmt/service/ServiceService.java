package org.k8scmp.appmgmt.service;

import java.util.HashMap;
import java.util.List;

import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.Instance;
import org.k8scmp.appmgmt.domain.NodePortDraft;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.domain.VersionString;

/**
 */
public interface ServiceService {
    String createService(ServiceDetail serviceDetail);

    void deleteService(String id);

    void modifyService(ServiceInfo serviceInfo);

    List<ServiceConfigInfo> listServices(ServiceInfo serviceInfo);

	List<ServiceConfigInfo> getServicesByAppId(String appId);

	String startService(String serviceId, int version, int replicas)
			throws Exception;

	String createLoadBalancer(String serviceId, List<NodePortDraft> nodePorts) throws Exception;

	void stopService(String serviceId) throws Exception;

	void startUpdate(String serviceId, int version, int replicas) throws Exception;

	void startRollback(String serviceId, int version, int replicas) throws Exception;

	void scaleUpDeployment(String serviceId, int version, int replicas) throws Exception;

	void scaleDownDeployment(String serviceId, int version, int replicas) throws Exception;

	List<DeployEvent> listDeployEvent(String serviceId) throws Exception;

	VersionString getYamlStr(ServiceConfigInfo serviceConfigInfo);

	List<Instance> listPodsByServiceId(String serviceId) throws Exception;

	List<String> getServiceURLs(String serviceId) throws Exception;

	HashMap<String, String> getServiceState(String serviceId) throws Exception;

	HashMap<String, String> getServicesStateByAppId(String appId) throws Exception;
}
