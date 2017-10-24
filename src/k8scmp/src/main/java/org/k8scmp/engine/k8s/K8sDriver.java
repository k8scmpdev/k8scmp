package org.k8scmp.engine.k8s;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Service;
import org.apache.commons.lang.StringUtils;

import org.json.JSONException;
import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.dao.ServiceEventDao;
import org.k8scmp.appmgmt.dao.VersionDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.DeploymentSnapshot;
import org.k8scmp.appmgmt.domain.Env;
import org.k8scmp.appmgmt.domain.NodePortDraft;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;
import org.k8scmp.appmgmt.service.ServiceStatusManager;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.engine.RuntimeDriver;
import org.k8scmp.engine.exception.DriverException;
import org.k8scmp.engine.k8s.handler.DeployResourceHandler;
import org.k8scmp.engine.k8s.handler.impl.DeploymentDeployHandler;
import org.k8scmp.engine.k8s.util.Fabric8KubeUtils;
import org.k8scmp.engine.k8s.util.KubeUtils;
import org.k8scmp.engine.k8s.util.PodUtils;
import org.k8scmp.engine.k8s.util.SecretUtils;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.exception.DeploymentTerminatedException;
import org.k8scmp.exception.K8sDriverException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.login.domain.User;
import org.k8scmp.model.DeployEventStatus;
import org.k8scmp.model.DeployOperation;
import org.k8scmp.model.ServiceStatus;
import org.k8scmp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 */
@Component
@Scope("prototype")
public class K8sDriver implements RuntimeDriver {
    private Logger logger = LoggerFactory.getLogger(K8sDriver.class);
    private Cluster cluster;

    @Autowired
    private ServiceStatusManager serviceStatusManager;
    @Autowired
	private ServiceEventDao serviceEventDao;
    @Autowired
    private ServiceDao serviceDao;
    @Autowired
    private VersionDao versionDao;
//    @Autowired
//    private LoadBalancerBiz loadBalancerBiz;
    @Autowired
    private GlobalBiz globalBiz;

    @Override
    public RuntimeDriver init(Cluster cluster) {
        this.cluster = cluster;
        return this;
    }

    @Override
    public boolean isDriverLatest(Cluster cluster) {
        boolean equal = cluster.equalWith(this.cluster);
        this.cluster = cluster;
        return equal;
    }

    @Override
    public void updateList(Cluster cluster) {
        this.cluster = cluster;
    }

    private static List<DeploymentSnapshot> buildSingleDeploymentSnapshot(long version, int replicas) {
        List<DeploymentSnapshot> snapshots = new LinkedList<>();
        snapshots.add(new DeploymentSnapshot(version, replicas));
        return snapshots;
    }

    @Override
    public void startDeploy(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, Version version, User user, List<Env> allExtraEnvs)
            throws DriverException, IOException, DeploymentEventException {
        KubeUtils client;
        DeployResourceHandler deployResourceHandler;
        try {
            client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
            deployResourceHandler = getDeployResourceHandler(appInfo, serviceConfigInfo, client);
        } catch (K8sDriverException e) {
            throw new DriverException(e.getMessage());
        }
        String eventId = serviceStatusManager.registerEvent(serviceConfigInfo.getId(),
                DeployOperation.START,
                user,
                null,
                null,
                buildSingleDeploymentSnapshot(version.getVersion(), serviceConfigInfo.getDefaultReplicas()));
        serviceStatusManager.freshEvent(eventId, null);
        DeployEvent event = serviceEventDao.getEvent(eventId);
        try {
            //loadBalancer
//            if (serviceConfigInfo.getNetworkMode() != NetworkMode.HOST  && serviceConfigInfo.getUsedLoadBalancer() == 0) {
//                List<LoadBalancer> lbs = loadBalancerBiz.getInnerAndExternalLoadBalancerByDeployId(serviceConfigInfo.getId());
//               checkLoadBalancer(client, serviceConfigInfo,appInfo);
//            }
            // create secret before the create of rc
            // judge the registry is belong to domeos or not
            checkSecret(client, version, appInfo, serviceConfigInfo);

            deployResourceHandler.create(version, allExtraEnvs);
        } catch (Exception e) {
            failedDeployment(serviceConfigInfo.getId(),event,e);
            logger.error("Failed to start service!", e);
        }
    }

    @Override
    public void createLoadBalancer(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo) throws K8sDriverException, DriverException {
    	 KubeUtils client;
         try {
             client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
             Service service = new K8sServiceBuilder(serviceConfigInfo,appInfo).build();
         	if(service != null){
         		Service oldService = client.serviceInfo(service.getMetadata().getName());
				if (oldService != null) {
					client.deleteService(service.getMetadata().getName());
				} 
         		client.createService(service);
         		logger.info("Service:" + service.getMetadata().getName() + " created successfully");
         	}else{
         		logger.error("Failed to create service!");
         	}
         } catch (K8sDriverException e) {
             throw new DriverException(e.getMessage());
         }
    	
    }
    
    @Override
    public void stopLoadBalancer(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo) throws DriverException {
    	 KubeUtils client;
         try {
             client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
             if(serviceConfigInfo.isExternal()){
            	 client.deleteService(GlobalConstant.RC_NAME_PREFIX + serviceConfigInfo.getServiceCode());
             }
         } catch (K8sDriverException e) {
             throw new DriverException(e.getMessage());
         }
    	
    }
    
//    @Override
//    public void abortDeployOperation(ServiceConfigInfo serviceConfigInfo, User user)
//            throws IOException{
//        KubeUtils kubeUtils;
//        DeployResourceHandler deployResourceHandler;
//        try {
//            kubeUtils = Fabric8KubeUtils.buildKubeUtils(cluster, serviceConfigInfo.getNamespace());
//            deployResourceHandler = getDeployResourceHandler(serviceConfigInfo, kubeUtils);
//        } catch (K8sDriverException e) {
//            throw new Exception(e);
//        }
//        long abortDeployEventId = serviceStatusManager.registerAbortEvent(serviceConfigInfo.getId(), user);
//        PodList podList = getPodListByDeployment(kubeUtils, serviceConfigInfo);
//        serviceStatusManager.freshEvent(abortDeployEventId, queryCurrentSnapshotWithPodRunning(podList));
//        DeployEvent abortEvent = deployEventBiz.getEvent(abortDeployEventId);
//        if (abortEvent.getEventStatus().equals(DeployEventStatus.PROCESSING)) {
//            switch (abortEvent.getOperation()) {
//                case ABORT_START:
//                    try {
//                        //loadBalancer
//                        try {
//                            LoadBalancer lb = loadBalancerBiz.getInnerLoadBalancerByDeployId(serviceConfigInfo.getId());
//                            if (lb != null) {
//                                kubeUtils.deleteService(GlobalConstant.RC_NAME_PREFIX + serviceConfigInfo.getName());
//                            }
//                        } catch (Exception e) {
//                            throw new DeploymentEventException(e.getMessage());
//                        }
//                        deployResourceHandler.delete();
//                        podList = getPodListByDeployment(kubeUtils, serviceConfigInfo);
//                        serviceStatusManager.succeedEvent(abortDeployEventId, queryCurrentSnapshotWithPodRunning(podList));
//                    } catch (Exception e) {
//                        podList = getPodListByDeployment(kubeUtils, serviceConfigInfo);
//                        serviceStatusManager.failedEvent(abortDeployEventId, queryCurrentSnapshotWithPodRunning(podList),
//                                "abort " + abortEvent.getOperation() + " failed");
//                    }
//                    break;
//                case ABORT_UPDATE:
//                case ABORT_ROLLBACK:
//                    try {
//                        Boolean updateDone = deployResourceHandler.abortUpdateOrRollBack();
//                        if (updateDone) {
//                            podList = getPodListByDeployment(kubeUtils, serviceConfigInfo);
//                            serviceStatusManager.succeedEvent(abortDeployEventId, queryCurrentSnapshotWithPodRunning(podList));
//                        }
//                    } catch (Exception e) {
//                        podList = getPodListByDeployment(kubeUtils, serviceConfigInfo);
//                        serviceStatusManager.failedEvent(abortDeployEventId, queryCurrentSnapshotWithPodRunning(podList),
//                                "abort " + abortEvent.getOperation() + " failed");
//                    }
//                    break;
//                case ABORT_SCALE_UP:
//                case ABORT_SCALE_DOWN:
//                    try {
//                        deployResourceHandler.abortScales();
//                    } catch (Exception e) {
//                        podList = getPodListByDeployment(kubeUtils, serviceConfigInfo);
//                        serviceStatusManager.failedEvent(abortDeployEventId, queryCurrentSnapshotWithPodRunning(podList),
//                                "abort " + abortEvent.getOperation() + " failed");
//                    }
//                    break;
//                default:
//                    throw new DeploymentEventException("There is no deploy event operation named " + abortEvent.getOperation());
//            }
//        }
//    }
//
    @Override
    public void stopDeploy(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, User user)
            throws DeploymentEventException, IOException {
        KubeUtils kubeUtils;
        DeployResourceHandler deployResourceHandler;
        try {
            kubeUtils = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
            deployResourceHandler = getDeployResourceHandler(appInfo,serviceConfigInfo, kubeUtils);
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        PodList podList = getPodListByDeployment(kubeUtils, serviceConfigInfo.getId());
        List<DeploymentSnapshot> currentSnapshot = queryCurrentSnapshotWithPodRunning(podList);
        String eventId = serviceStatusManager.registerEvent(serviceConfigInfo.getId(),
                DeployOperation.STOP,
                user,
                currentSnapshot,
                currentSnapshot,
                null);
        serviceStatusManager.freshEvent(eventId, currentSnapshot);
        
        //loadBalancer
        try {
        	if(serviceConfigInfo.isExternal())
        		kubeUtils.deleteService(GlobalConstant.RC_NAME_PREFIX + serviceConfigInfo.getServiceCode());
        	
            deployResourceHandler.delete();
        } catch (Exception e) {
            throw new DeploymentEventException(e.getMessage());
        }

    }

    @Override
    public void rollbackDeploy(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, int ver, List<Env> allExtraEnvs, User user)
            throws IOException, DeploymentTerminatedException, DeploymentEventException {
        KubeUtils kubeUtils;
        DeployResourceHandler deployResourceHandler;
        try {
        	kubeUtils = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
            deployResourceHandler = getDeployResourceHandler(appInfo,serviceConfigInfo, kubeUtils);
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        VersionBase versionBase = versionDao.getVersion(serviceConfigInfo.getId(), ver);
        Version version = versionBase.toModel(Version.class);
        // check status
        PodList podList = getPodListByDeployment(kubeUtils, serviceConfigInfo.getId());
        List<DeploymentSnapshot> currentRunningSnapshot = queryCurrentSnapshotWithPodRunning(podList);
        int totalReplicas = getTotalReplicas(currentRunningSnapshot);
        if (serviceConfigInfo.getDefaultReplicas() != -1) {
            totalReplicas = serviceConfigInfo.getDefaultReplicas();
        }
        
        String eventId = serviceStatusManager.registerEvent(serviceConfigInfo.getId(),
                DeployOperation.ROLLBACK,
                user,
                currentRunningSnapshot,
                currentRunningSnapshot,
                buildSingleDeploymentSnapshot(ver, totalReplicas));
        serviceStatusManager.freshEvent(eventId, currentRunningSnapshot);
        
        // create secret before the create of rc
        // judge the registry is belong to domeos or not
        checkSecret(kubeUtils, version, appInfo, serviceConfigInfo);
        DeployEvent event = serviceEventDao.getEvent(eventId);
        try {
        	checkLoadBalancer(kubeUtils, serviceConfigInfo,appInfo);
           
            deployResourceHandler.rollback(version, allExtraEnvs);
        } catch (K8sDriverException | DriverException e) {
        	failedDeployment(serviceConfigInfo.getId(),event,e);
        }
    }

    @Override
    public void startUpdate(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, int ver, List<Env> allExtraEnvs, User user)
            throws IOException, DeploymentTerminatedException, DeploymentEventException {
        // ** create KubernetesClient
    	 KubeUtils kubeUtils;
         DeployResourceHandler deployResourceHandler;
         try {
         	kubeUtils = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
             deployResourceHandler = getDeployResourceHandler(appInfo,serviceConfigInfo, kubeUtils);
         } catch (K8sDriverException e) {
             throw new DeploymentEventException(e);
         }
         VersionBase versionBase = versionDao.getVersion(serviceConfigInfo.getId(), ver);
         Version version = versionBase.toModel(Version.class);
         // check status
         PodList podList = getPodListByDeployment(kubeUtils, serviceConfigInfo.getId());
         List<DeploymentSnapshot> currentRunningSnapshot = queryCurrentSnapshotWithPodRunning(podList);
         int totalReplicas = getTotalReplicas(currentRunningSnapshot);
         if (serviceConfigInfo.getDefaultReplicas() != -1) {
             totalReplicas = serviceConfigInfo.getDefaultReplicas();
         }
         
         String eventId = serviceStatusManager.registerEvent(serviceConfigInfo.getId(),
                 DeployOperation.UPDATE,
                 user,
                 currentRunningSnapshot,
                 currentRunningSnapshot,
                 buildSingleDeploymentSnapshot(ver, totalReplicas));
         serviceStatusManager.freshEvent(eventId, currentRunningSnapshot);
         
         // create secret before the create of rc
         // judge the registry is belong to domeos or not
         checkSecret(kubeUtils, version, appInfo, serviceConfigInfo);
         DeployEvent event = serviceEventDao.getEvent(eventId);
         try {
         	 checkLoadBalancer(kubeUtils, serviceConfigInfo,appInfo);
            
             deployResourceHandler.update(version, allExtraEnvs);
         } catch (K8sDriverException | DriverException e) {
         	failedDeployment(serviceConfigInfo.getId(),event,e);
         }
         
    }

    @Override
    public void scaleUpDeployment(AppInfo appInfo,ServiceConfigInfo serviceConfigInfo, int ver, int replicas, List<Env> allExtraEnvs, User user)
            throws DeploymentEventException, IOException, DeploymentTerminatedException {
    	KubeUtils client;
        DeployResourceHandler deployResourceHandler;
        try {
        	client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
            deployResourceHandler = getDeployResourceHandler(appInfo,serviceConfigInfo, client);
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        VersionBase versionBase = versionDao.getVersion(serviceConfigInfo.getId(), ver);
        Version version = versionBase.toModel(Version.class);
        List<DeploymentSnapshot> currentRunningSnapshot = null;
        try {
            // ** find rc
        	PodList podList = getPodListByDeployment(client, serviceConfigInfo.getId());
            currentRunningSnapshot = queryCurrentSnapshotWithPodRunning(podList);
            List<DeploymentSnapshot> dstSnapshot = buildDeploymentSnapshotWith(currentRunningSnapshot, ver, replicas);
            String eventId = serviceStatusManager.registerEvent(serviceConfigInfo.getId(),
                    DeployOperation.SCALE_UP,
                    user,
                    currentRunningSnapshot,
                    currentRunningSnapshot,
                    dstSnapshot);
            serviceStatusManager.freshEvent(eventId, currentRunningSnapshot);
            
            checkSecret(client, version, appInfo, serviceConfigInfo);
            //loadBalancer
            checkLoadBalancer(client, serviceConfigInfo,appInfo);
            deployResourceHandler.scaleUp(version, replicas);
            //deployResourceHandler.removeOtherDeploy(ver);
        } catch (IOException | K8sDriverException | DriverException e) {
            serviceStatusManager.failedEventForDeployment(serviceConfigInfo.getId(), currentRunningSnapshot, e.getMessage());
            throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
        }
    }

    @Override
    public void scaleDownDeployment(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, int ver, int replicas, List<Env> allExtraEnvs, User user)
            throws DeploymentEventException, IOException, DeploymentTerminatedException {
    	KubeUtils client;
        DeployResourceHandler deployResourceHandler;
        try {
        	client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
            deployResourceHandler = getDeployResourceHandler(appInfo,serviceConfigInfo, client);
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        VersionBase versionBase = versionDao.getVersion(serviceConfigInfo.getId(), ver);
        Version version = versionBase.toModel(Version.class);
        List<DeploymentSnapshot> currentRunningSnapshot = null;
        try {
            // ** find rc
        	PodList podList = getPodListByDeployment(client, serviceConfigInfo.getId());
            currentRunningSnapshot = queryCurrentSnapshotWithPodRunning(podList);
            List<DeploymentSnapshot> dstSnapshot = buildDeploymentSnapshotWith(currentRunningSnapshot, ver, replicas);
            String eventId = serviceStatusManager.registerEvent(serviceConfigInfo.getId(),
                    DeployOperation.SCALE_DOWN,
                    user,
                    currentRunningSnapshot,
                    currentRunningSnapshot,
                    dstSnapshot);
            serviceStatusManager.freshEvent(eventId, currentRunningSnapshot);
            
            checkSecret(client, version, appInfo, serviceConfigInfo);
            //loadBalancer
            checkLoadBalancer(client, serviceConfigInfo,appInfo);
            deployResourceHandler.scaleDown(version, replicas);
            //deployResourceHandler.removeOtherDeploy(ver);
        } catch (IOException | K8sDriverException | DriverException e) {
            serviceStatusManager.failedEventForDeployment(serviceConfigInfo.getId(), currentRunningSnapshot, e.getMessage());
            throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
        }
    }

    private DeployResourceHandler getDeployResourceHandler(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, KubeUtils kubeUtils) throws K8sDriverException {
//        String deployClass = serviceConfigInfo.getDeploymentType().getDeployClassName();
//        if (deployClass == null) {
//            throw new K8sDriverException("A serviceConfigInfo must have serviceConfigInfo type");
//        }
//        Server server = globalBiz.getServer();
//        if (server == null) {
//            throw new K8sDriverException("Global configuration of Server not set!");
//        }
        DeployResourceHandler deployResourceHandler = new DeploymentDeployHandler(appInfo, serviceConfigInfo, kubeUtils);
        if (deployResourceHandler == null) {
            throw new K8sDriverException("Cannot create deploy handler with serviceConfigInfo :" + serviceConfigInfo);
        }
        return deployResourceHandler;
    }

    private Map<String, String> buildDeploySelector(String serviceId) {
        Map<String, String> selector = new HashMap<>();
        selector.put(GlobalConstant.DEPLOY_ID_STR, String.valueOf(serviceId));
        return selector;
    }

    private PodList getPodListByDeployment(KubeUtils kubeUtils, String serviceId)
            throws DeploymentEventException {
        try {
            return kubeUtils.listPod(buildDeploySelector(serviceId));
        } catch (K8sDriverException e) {
            throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
        }
    }

    private List<DeploymentSnapshot> queryCurrentSnapshot(PodList podList) {
        if (podList == null || podList.getItems() == null || podList.getItems().size() == 0) {
            return null;
        }
        Map<Long, Long> snapshots = new HashMap<>();
        for (Pod pod : podList.getItems()) {
            if (pod == null || pod.getMetadata() == null || pod.getMetadata().getLabels() == null) {
                continue;
            }
            String longData = pod.getMetadata().getLabels().get(GlobalConstant.VERSION_STR);
            if (StringUtils.isBlank(longData)) {
                continue;
            }
            Long version = Long.parseLong(longData);
            if (!snapshots.containsKey(version)) {
                snapshots.put(version, 1L);
            } else {
                snapshots.put(version, snapshots.get(version) + 1);
            }
        }
        List<DeploymentSnapshot> snapshotList = new LinkedList<>();
        for (Map.Entry<Long, Long> entry : snapshots.entrySet()) {
            snapshotList.add(new DeploymentSnapshot(entry.getKey(), entry.getValue()));
        }
        return snapshotList;
    }

    private List<DeploymentSnapshot> queryCurrentSnapshotWithPodRunning(PodList podList) {
        if (podList == null || podList.getItems() == null || podList.getItems().size() == 0) {
            return null;
        }
        Map<Long, Long> snapshots = new HashMap<>();
        for (Pod pod : podList.getItems()) {
            if (pod == null || pod.getMetadata() == null || pod.getMetadata().getLabels() == null) {
                continue;
            }
            String longData = pod.getMetadata().getLabels().get(GlobalConstant.VERSION_STR);
            if (StringUtils.isBlank(longData)) {
                continue;
            }
            if (!PodUtils.isPodReady(pod)) {
                continue;
            }
            Long version = Long.parseLong(longData);

            if (!snapshots.containsKey(version)) {
                snapshots.put(version, 1L);
            } else {
                snapshots.put(version, snapshots.get(version) + 1);
            }
        }
        List<DeploymentSnapshot> snapshotList = new LinkedList<>();
        for (Map.Entry<Long, Long> entry : snapshots.entrySet()) {
            snapshotList.add(new DeploymentSnapshot(entry.getKey(), entry.getValue()));
        }
        return snapshotList;
    }

    private int getTotalReplicas(List<DeploymentSnapshot> snapshots) {
        int replicas = 0;
        if (snapshots == null || snapshots.size() == 0) {
            return replicas;
        }
        for (DeploymentSnapshot snapshot : snapshots) {
            replicas += snapshot.getReplicas();
        }
        return replicas;
    }

    private boolean checkAnyInstanceFailed(String namespace,String serviceId, long version)
            throws Exception {
    	ServiceInfo serviceInfo = serviceDao.getService(serviceId);
    	ServiceConfigInfo serviceConfigInfo  = serviceInfo.toModel(ServiceConfigInfo.class);
        KubeUtils client = Fabric8KubeUtils.buildKubeUtils(cluster, namespace);
        Map<String, String> rcSelector = buildDeploySelectorWithSpecifyVersion(serviceConfigInfo, version);
        PodList podList = client.listPod(rcSelector);
        return PodUtils.isAnyFailed(podList);
    }

    @Override
    public void checkBasicEvent(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, DeployEvent event)
            throws DeploymentEventException, IOException, ParseException, DeploymentTerminatedException {
        KubeUtils client;
        try {
            client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        try {
            PodList podList = getPodListByDeployment(client, serviceConfigInfo.getId());
            List<DeploymentSnapshot> currentSnapshot = queryCurrentSnapshot(podList);
            List<DeploymentSnapshot> currentRunningSnapshot = queryCurrentSnapshotWithPodRunning(podList);
            List<DeploymentSnapshot> desiredSnapshot = event.getTargetSnapshot();
            if (currentSnapshot == null && PodUtils.isExpireForEventNotReallyHappen(DateUtil.string2timestamp(event.getStartTime()))) {
//                deleteUpdaterJob(client, serviceConfigInfo.getId());
                serviceStatusManager.failedEvent(event.getId(), null, "no deployment found for event(id="
                        + event.getId() + ")");
                return;
            }
            if (desiredSnapshot == null) {
//                deleteUpdaterJob(client, serviceConfigInfo.getId());
                serviceStatusManager.failedEvent(event.getId(), currentRunningSnapshot, "null desired snapshot");
                return;
            }

            for (DeploymentSnapshot deploymentSnapshot : desiredSnapshot) {
                if (checkAnyInstanceFailed(appInfo.getNamespace(), event.getServiceId(), deploymentSnapshot.getVersion())) {
//                    deleteUpdaterJob(client, serviceConfigInfo.getId());
                    serviceStatusManager.failedEvent(event.getId(), currentRunningSnapshot, "one of pod is start failed");
                    return;
                }
            }
            if (isSnapshotEquals(currentSnapshot, event.getTargetSnapshot()) && isSnapshotEquals(currentRunningSnapshot, event.getTargetSnapshot())) {
//                deleteUpdaterJob(client, serviceConfigInfo.getId());
                serviceStatusManager.succeedEvent(event.getId(), currentSnapshot);
            } else {
                serviceStatusManager.freshEvent(event.getId(), currentRunningSnapshot);
            }
        } catch (K8sDriverException e) {
            throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
        } catch (Exception e) {
        	throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
		}
    }

//    @Override
//    public void checkAbortEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event)
//            throws DeploymentEventException, IOException, DeploymentTerminatedException {
//        KubeUtils client;
//        DeployResourceHandler deployResourceHandler;
//        try {
//            client = Fabric8KubeUtils.buildKubeUtils(cluster, serviceConfigInfo.getNamespace());
//            deployResourceHandler = getDeployResourceHandler(serviceConfigInfo, client);
//        } catch (K8sDriverException e) {
//            throw new DeploymentEventException(e);
//        }
//        PodList podList = getPodListByDeployment(client, serviceConfigInfo);
//        List<DeploymentSnapshot> currentSnapshot = queryCurrentSnapshot(podList);
//        List<DeploymentSnapshot> currentRunningSnapshot = queryCurrentSnapshotWithPodRunning(podList);
//        switch (event.getOperation()) {
//            case ABORT_START:
//                if (currentSnapshot == null || currentSnapshot.isEmpty()) {
//                    serviceStatusManager.succeedEvent(event.getId(), currentSnapshot);
//                } else {
//                    try {
//                        deployResourceHandler.delete();
//                    } catch (K8sDriverException e) {
//                        throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
//                    }
//                    serviceStatusManager.freshEvent(event.getId(), currentRunningSnapshot);
//                }
//                break;
//            case ABORT_UPDATE:
//            case ABORT_ROLLBACK:
//                try {
//                    deployResourceHandler.abortUpdateOrRollBack();
//                    serviceStatusManager.succeedEvent(event.getId(), currentRunningSnapshot);
//                } catch (K8sDriverException e) {
//                    serviceStatusManager.failedEvent(event.getId(), currentRunningSnapshot, "Abort failed of rolling operation.");
//                }
//                break;
//            case ABORT_SCALE_UP:
//            case ABORT_SCALE_DOWN:
//                try {
//                    deployResourceHandler.abortScales();
//                    serviceStatusManager.succeedEvent(event.getId(), currentRunningSnapshot);
//                } catch (Exception e) {
//                    serviceStatusManager.failedEvent(event.getId(), currentRunningSnapshot,
//                            "Adjust serviceConfigInfo replicas failed when abort scale operation.");
//                }
//                break;
//            default:
//                throw new DeploymentEventException("Deploy event operation '" + event.getOperation() + "' can not be check as abort event");
//        }
//    }
//
    @Override
    public void checkStopEvent(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, DeployEvent event)
            throws DeploymentEventException, IOException, DeploymentTerminatedException {
        KubeUtils client;
        DeployResourceHandler deployResourceHandler;
        try {
            client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
            deployResourceHandler = getDeployResourceHandler(appInfo,serviceConfigInfo, client);
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        PodList podList = getPodListByDeployment(client, serviceConfigInfo.getId());
        List<DeploymentSnapshot> currentSnapshot = queryCurrentSnapshot(podList);
        if (currentSnapshot == null || currentSnapshot.isEmpty()) {
            serviceStatusManager.succeedEvent(event.getId(), currentSnapshot);
        } else {
            try {
                deployResourceHandler.delete();
            } catch (K8sDriverException e) {
                throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
            }
            serviceStatusManager.freshEvent(event.getId(), currentSnapshot);
        }
    }

//    @Override
//    public void expiredEvent(ServiceConfigInfo serviceConfigInfo, DeployEvent event) throws DeploymentEventException, IOException, DeploymentTerminatedException {
//        KubeUtils client;
//        try {
//            client = Fabric8KubeUtils.buildKubeUtils(cluster, serviceConfigInfo.getNamespace());
//        } catch (K8sDriverException e) {
//            throw new DeploymentEventException(e);
//        }
//        PodList podList = getPodListByDeployment(client, serviceConfigInfo);
//        serviceStatusManager.failedEvent(event.getId(), queryCurrentSnapshotWithPodRunning(podList), "Operation expired. " + event.getMessage());
//    }
//
    @Override
    public List<VersionBase> getCurrnetVersionsByService(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo) throws DeploymentEventException {
        if (serviceConfigInfo == null) {
            return null;
        }
        KubeUtils client = null;
        DeployResourceHandler deployResourceHandler;
        try {
        	client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
            deployResourceHandler = getDeployResourceHandler(appInfo,serviceConfigInfo, client);
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        // get current versions
        PodList podList = getPodListByDeployment(client, serviceConfigInfo.getId());
        List<DeploymentSnapshot> deploymentSnapshots = queryCurrentSnapshot(podList);
        if (deploymentSnapshots != null && deploymentSnapshots.isEmpty()) {
            try {
                deploymentSnapshots = deployResourceHandler.queryDesiredSnapshot();
            } catch (K8sDriverException e) {
                throw new DeploymentEventException(e);
            }
        }
        List<VersionBase> versions = null;
        if (deploymentSnapshots != null) {
            versions = new ArrayList<>(deploymentSnapshots.size());
            for (DeploymentSnapshot deploymentSnapshot : deploymentSnapshots) {
                VersionBase verBase = versionDao.getVersion(serviceConfigInfo.getId(), (int) deploymentSnapshot.getVersion());
                versions.add(verBase);
            }
        }
        return versions;
    }

    @Override
    public long getTotalReplicasByDeployment(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo) throws DeploymentEventException {
        if (serviceConfigInfo == null) {
            return 0;
        }
        KubeUtils client = null;
        try {
            client = Fabric8KubeUtils.buildKubeUtils(cluster, appInfo.getNamespace());
        } catch (K8sDriverException e) {
            throw new DeploymentEventException(e);
        }
        // get current versions
        PodList podList = getPodListByDeployment(client, serviceConfigInfo.getId());
        List<DeploymentSnapshot> deploymentSnapshots = queryCurrentSnapshot(podList);
        return getTotalReplicas(deploymentSnapshots);
    }
    
    private Map<String, String> buildDeploySelectorWithSpecifyVersion(ServiceConfigInfo serviceConfigInfo, long versionV) {
        Map<String, String> selector = buildDeploySelector(serviceConfigInfo.getId());
        selector.put(GlobalConstant.VERSION_STR, String.valueOf(versionV));
        return selector;
    }

    private void failedDeployment(String serviceId,DeployEvent event,Exception e) {
        serviceDao.updateServiceStatu(serviceId,ServiceStatus.ERROR.name());
        event.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
        event.setState(DeployEventStatus.FAILED);
        event.setCurrentSnapshot(new ArrayList<DeploymentSnapshot>());
        event.setMessage(e.getMessage());
        serviceEventDao.updateEvent(event);
    }

    // this function will add or replace version in oldSnapshot
    private List<DeploymentSnapshot> buildDeploymentSnapshotWith(
            List<DeploymentSnapshot> oldSnapshot, long version, long replicas) {
        List<DeploymentSnapshot> result = new LinkedList<>();
        if (oldSnapshot == null) {
            return null;
        }
        boolean isFind = false;
        for (DeploymentSnapshot oneSnapshot : oldSnapshot) {
            if (oneSnapshot.getVersion() == version) {
                result.add(new DeploymentSnapshot(version, replicas));
                isFind = true;
            } else {
                result.add(new DeploymentSnapshot(oneSnapshot));
            }
        }
        if (!isFind) {
            result.add(new DeploymentSnapshot(version, replicas));
        }
        return result;
    }

    private boolean isSnapshotEquals(List<DeploymentSnapshot> one, List<DeploymentSnapshot> another) {
        if (one == null || another == null) {
            return false;
        }
        Map<Long, Long> versionCount = new HashMap<>();
        for (DeploymentSnapshot deploymentSnapshot : one) {
            if (deploymentSnapshot.getReplicas() > 0) {
                // ignore zero replicas
                versionCount.put(deploymentSnapshot.getVersion(), deploymentSnapshot.getReplicas());
            }
        }
        for (DeploymentSnapshot deploymentSnapshot : another) {
            if (deploymentSnapshot.getReplicas() <= 0) {
                // ignore zero replicas
                continue;
            }
            if (!versionCount.containsKey(deploymentSnapshot.getVersion())) {
                return false;
            }
            if (versionCount.get(deploymentSnapshot.getVersion()) != deploymentSnapshot.getReplicas()) {
                return false;
            }
            versionCount.remove(deploymentSnapshot.getVersion());
        }
        return versionCount.isEmpty();
    }

    private void checkSecret(KubeUtils client, Version version, AppInfo appInfo, ServiceConfigInfo serviceConfigInfo) throws DeploymentEventException{
        if (version != null && SecretUtils.haveRegistry(version.getContainerDrafts())) {
            try {
                if (client.secretInfo(GlobalConstant.SECRET_NAME_PREFIX + appInfo.getNamespace()) == null) {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put(GlobalConstant.SECRET_DOCKERCFG_DATA_KEY, SecretUtils.getImageSecretData());
                    client.createSecret(GlobalConstant.SECRET_NAME_PREFIX + appInfo.getNamespace(),
                            GlobalConstant.SECRET_DOCKERCFG_TYPE, dataMap);
                }
            } catch (K8sDriverException | JSONException e) {
                throw new DeploymentEventException("kubernetes exception with message=" + e.getMessage());
            }
        }
    }

    private Map<String, String> buildJobSelector(int deployId) {
        Map<String, String> selector = new HashMap<>();
        selector.put(GlobalConstant.JOB_DEPLOY_ID_STR, String.valueOf(deployId));
        return selector;
    }

    private void deleteUpdaterJob(KubeUtils client, int deployId) {
        try {
            client.deleteJob(buildJobSelector(deployId));
        } catch (Exception ignored) {
        }
    }
    
    private void checkLoadBalancer(KubeUtils client,  ServiceConfigInfo serviceConfigInfo, AppInfo appInfo) throws K8sDriverException, DriverException {
        Service service = new K8sServiceBuilder(serviceConfigInfo,appInfo).build();
    	if(service != null){
    		Service oldService = client.serviceInfo(service.getMetadata().getName());
			if (oldService == null) {
                client.createService(service);
                logger.info("Service:" + service.getMetadata().getName() + " created successfully");
            } else {
                logger.info("Service:" + service.getMetadata().getName() + " exists, do not need to create");
            }
    	}else{
    		logger.error("Failed to create service!");
    	}
        
    }
//    
//    @Override
//    public void deletePodByDeployIdAndInsName(ServiceConfigInfo serviceConfigInfo, String insName)
//            throws DeploymentEventException, IOException {
//        KubeUtils kubeUtils;
//        DeployResourceHandler deployResourceHandler;
//        try {
//            kubeUtils = Fabric8KubeUtils.buildKubeUtils(cluster, serviceConfigInfo.getNamespace());
//            kubeUtils.deletePod(insName);
//        } catch (K8sDriverException e) {
//            throw new DeploymentEventException(e);
//        }
//    }
}
