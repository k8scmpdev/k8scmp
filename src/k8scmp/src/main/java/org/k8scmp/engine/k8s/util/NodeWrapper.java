package org.k8scmp.engine.k8s.util;

import io.fabric8.kubernetes.api.model.*;

import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.Container;
import org.k8scmp.appmgmt.domain.Instance;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.exception.ApiException;
import org.k8scmp.exception.K8sDriverException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.mapper.appmgmt.ServiceMapper;
import org.k8scmp.monitormgmt.domain.monitor.ContainerInfo;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;
import org.k8scmp.monitormgmt.domain.monitor.PodInfo;
import org.k8scmp.util.CommonUtil;
import org.k8scmp.util.DateUtil;
import org.k8scmp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by feiliu206363 on 2015/12/15.
 */
@Component("nodeWrapper")
public class NodeWrapper {
    private Logger logger = LoggerFactory.getLogger(NodeWrapper.class);
    private KubeUtils client;
    
    @Autowired
    GlobalBiz globalBiz;
    
    @Autowired
    ServiceDao serviceDao;
//    private static DeploymentBiz deploymentBiz;

//    @Autowired
//    public void setProjectBiz(DeploymentBiz deploymentBiz) {
//        NodeWrapper.deploymentBiz = deploymentBiz;
//    }

    public NodeWrapper init(String namespace) throws K8sDriverException {
    	//cluster 需要自定义
//        Cluster cluster = KubeServiceInfo.getClusterBasicById(clusterId);
    	Cluster cluster = new Cluster();
//    	GlobalInfo globalInfo = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
//    	cluster.setApi(globalInfo.getValue());
    	cluster.setApi("192.168.80.146:8080");
        this.init(cluster, namespace);

        return this;
    }

    public NodeWrapper init(Cluster cluster, String namespace) throws K8sDriverException {
        if (cluster == null) {
            throw new K8sDriverException("no such cluster info");
        }

        // TODO: when we have different cluster type, should add more op here
        client = Fabric8KubeUtils.buildKubeUtils(cluster, namespace);
        return this;
    }

    public List<NodeInfo> getNodeListByClusterId() {
        NodeList nodeList = getNodeList();
        List<NodeInfo> nodeInfo = new LinkedList<>();
        if (nodeList != null && nodeList.getItems() != null) {
            List<NodeInfoTask> infoTasks = new LinkedList<>();

            for (Node node : nodeList.getItems()) {
                infoTasks.add(new NodeInfoTask(node));
            }
            nodeInfo = ClientConfigure.executeCompletionService(infoTasks);
        }
        return nodeInfo;
    }

    private class NodeInfoTask implements Callable<NodeInfo> {
        Node node;

        NodeInfoTask(Node node) {
            this.node = node;
        }

        @Override
        public NodeInfo call() throws Exception {
            NodeInfo nodeInfo = generateNodeInfo(node);
            if (node != null && node.getMetadata() != null
                    && node.getMetadata().getAnnotations() != null
                    && node.getMetadata().getAnnotations().containsKey(GlobalConstant.DISK_STR)) {
                nodeInfo.setDiskInfo(node.getMetadata().getAnnotations().get(GlobalConstant.DISK_STR));
            }
            return nodeInfo;
        }
    }

    public List<NodeInfo> getNodeInfoListWithoutPods() throws ParseException {
        NodeList nodeList = getNodeList();
        List<NodeInfo> nodeInfoList = new LinkedList<>();
        if (nodeList != null && nodeList.getItems() != null) {
            for (Node node : nodeList.getItems()) {
                NodeInfo nodeInfo = new NodeInfo();
                if (node.getMetadata() != null) {
                    nodeInfo.setLabels(node.getMetadata().getLabels());
                    nodeInfo.setName(node.getMetadata().getName());
                    if (node.getMetadata().getAnnotations() != null) {
                        nodeInfo.setDiskInfo(node.getMetadata().getAnnotations().get(GlobalConstant.DISK_STR));
                    }
                    nodeInfo.setCreateTime(DateUtil.string2timestamp(node.getMetadata().getCreationTimestamp(), TimeZone.getTimeZone(GlobalConstant.UTC_TIME)));
                }
                if (node.getStatus() != null) {
                    if (node.getStatus().getAddresses() != null) {
                        for (NodeAddress nodeAddress : node.getStatus().getAddresses()) {
                            if ("internalip".equalsIgnoreCase(nodeAddress.getType())) {
                                nodeInfo.setIp(nodeAddress.getAddress());
                            }
                        }
                    }
                    if (node.getStatus().getNodeInfo() != null) {
                        NodeSystemInfo nodeSystemInfo = node.getStatus().getNodeInfo();
                        if (StringUtils.isNotBlank(nodeSystemInfo.getContainerRuntimeVersion())
                                && nodeSystemInfo.getContainerRuntimeVersion().startsWith("docker://")) {
                            nodeInfo.setDockerVersion(nodeSystemInfo.getContainerRuntimeVersion().replace("docker://", ""));
                        }
                        if (StringUtils.isNotBlank(nodeSystemInfo.getKubeletVersion())) {
                            nodeInfo.setKubeletVersion(nodeSystemInfo.getKubeletVersion());
                        }
                        if (StringUtils.isNotBlank(nodeSystemInfo.getKernelVersion())) {
                            nodeInfo.setKernelVersion(nodeSystemInfo.getKernelVersion());
                        }
                        if (StringUtils.isNotBlank(nodeSystemInfo.getOsImage())) {
                            nodeInfo.setOsVersion(nodeSystemInfo.getOsImage());
                        } else if (StringUtils.isNotBlank(nodeSystemInfo.getOperatingSystem())) {
                            nodeInfo.setOsVersion(nodeSystemInfo.getOperatingSystem());
                        }
                    }
                    Map<String, Quantity> capacity = node.getStatus().getCapacity();
                    if (capacity != null) {
                        capacity.put("memory", CommonUtil.getMemory(capacity.get("memory")));
                    }
                    nodeInfo.setCapacity(capacity);
                }
                if (NodeUtils.isReady(node)) {
                    nodeInfo.setStatus("Ready");
                } else {
                    nodeInfo.setStatus("NotReady");
                }
                nodeInfoList.add(nodeInfo);
            }
        }
        return nodeInfoList;
    }
    
   /* public List<Map<String,List<PodInfo>>> getPodListByClusterNamespaceLabels(Cluster cluster,List<String> namespaces,List<Map<String,String>> labels){
    	KubeUtils clientnew = null;
    	List<Map<String,List<PodInfo>>> mapList = new ArrayList<>();
    	Map<String,List<PodInfo>> podmap = new HashMap<>();
    	try {
	    	if(cluster == null){
	    		GlobalInfo cluster_host = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
	    		GlobalInfo cluster_name = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
	        	Cluster clusterNew = new Cluster();
	        	clusterNew.setApi(cluster_host.getValue());
	        	clusterNew.setId(cluster_host.getId()+"");
	        	clusterNew.setName(cluster_name.getValue());
				clientnew = Fabric8KubeUtils.buildKubeUtils(clusterNew, "default");
	    	}else{
	    		clientnew = Fabric8KubeUtils.buildKubeUtils(cluster, "default");
	    	}
	    	if(namespaces != null){
				for (String namespace : namespaces) {
					List<Namespace> items = clientnew.listAllNamespace().getItems();
					for (Namespace namespace2 : items) {
						if(namespace.equals(namespace2.getMetadata().getNamespace())){
							if(labels != null){
								List<GetPodTask> nodeTasks = new ArrayList<>(labels.size());
	            			     nodeTasks.add(new GetPodTask(clientnew, labels));
	            			     List<List<PodInfo>> podLists = ClientConfigure.executeCompletionService(nodeTasks);
	            			     for (List<PodInfo> podList : podLists) {
	            			         podmap.put(namespace, podList);
	            			         mapList.add(podmap);
	            			     }	
							}else{
								List<GetPodTask> nodeTasks = new ArrayList<>();
	            			     nodeTasks.add(new GetPodTask(clientnew, null));
	            			     List<List<PodInfo>> podLists = ClientConfigure.executeCompletionService(nodeTasks);
	            			     for (List<PodInfo> podList : podLists) {
	            			         podmap.put(namespace, podList);
	            			         mapList.add(podmap);
	            			     }
							}
						}
					}
				}
			}else{
				List<Namespace> items = clientnew.listAllNamespace().getItems();
				for (Namespace namespace2 : items) {
					if(labels != null){
						 List<GetPodTask> nodeTasks = new ArrayList<>(labels.size());
	       			     nodeTasks.add(new GetPodTask(clientnew, labels));
	       			     List<List<PodInfo>> podLists = ClientConfigure.executeCompletionService(nodeTasks);
	       			     for (List<PodInfo> podList : podLists) {
	       			         podmap.put(namespace2.getMetadata().getNamespace(), podList);
	       			         mapList.add(podmap);
	       			     }	
					}else{
						List<GetPodTask> nodeTasks = new ArrayList<>();
	       			     nodeTasks.add(new GetPodTask(clientnew, null));
	       			     List<List<PodInfo>> podLists = ClientConfigure.executeCompletionService(nodeTasks);
	       			     for (List<PodInfo> podList : podLists) {
	       			         podmap.put(namespace2.getMetadata().getNamespace(), podList);
	       			         mapList.add(podmap);
	       			     }
					}
				}
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
   private class GetPodTask  implements Callable<List<PodInfo>>{
	   
	   private KubeUtils client;
	   private List<Map<String,String>> labels;
	   
	   GetPodTask(KubeUtils client,List<Map<String,String>> labels){
		   this.client = client;
		   this.labels = labels;
	   }
	   
	  @Override
	  public List<PodInfo> call() throws Exception {
		List<PodInfo> podList = new ArrayList<>();
		if(labels != null){
			for (Map<String,String> label : labels) {
				PodList listPod = client.listPod(label);
				for(Pod pod:listPod.getItems()){
					PodInfo podInfo = new PodInfo();
					podInfo.setPodName(pod.getMetadata().getName());
					List<ContainerInfo> containerList = new ArrayList<>();
					List<ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
					for (ContainerStatus containerStatus : containerStatuses) {
						ContainerInfo containerInfo = new ContainerInfo();
						containerInfo.setHostname(containerStatus.getName());
						containerInfo.setContainerId(containerStatus.getContainerID());
						containerList.add(containerInfo);
					}
					podInfo.setContainers(containerList);	
					podList.add(podInfo);
				}
			}
		}else{
			PodList listPod = client.listPod();
			for(Pod pod:listPod.getItems()){
				PodInfo podInfo = new PodInfo();
				podInfo.setPodName(pod.getMetadata().getName());
				List<ContainerInfo> containerList = new ArrayList<>();
				List<ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
				for (ContainerStatus containerStatus : containerStatuses) {
					ContainerInfo containerInfo = new ContainerInfo();
					containerInfo.setHostname(containerStatus.getName());
					containerInfo.setContainerId(containerStatus.getContainerID());
					containerList.add(containerInfo);
				}
				podInfo.setContainers(containerList);	
				podList.add(podInfo);
			}
		}
		return podList;
	  }
   }
    
    *//**
     * 根据cluster，namespace，labels查询node
     * *//*
    public List<Map<String,List<NodeInfo>>> getNodeListByClusterNamespaceLabels(Cluster clusters,List<String> namespaces,List<Map<String,String>> labels){
    	//当cluster等于空时新建一个cluster
    	KubeUtils clientnew = null;
    	List<Map<String,List<NodeInfo>>> resultList = new ArrayList<>();
    	Map<String,List<NodeInfo>> nodemap = new HashMap<>();
    	try {
    	if(clusters == null){
			GlobalInfo cluster_host = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
    		GlobalInfo cluster_name = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
        	Cluster clusterNew = new Cluster();
        	clusterNew.setApi(cluster_host.getValue());
        	clusterNew.setId(cluster_host.getId()+"");
        	clusterNew.setName(cluster_name.getValue());
			clientnew = Fabric8KubeUtils.buildKubeUtils(clusterNew, "default");
    	}else{
    		clientnew = Fabric8KubeUtils.buildKubeUtils(clusters, "default");
        }
		if(namespaces != null){
    		for (String namespace : namespaces) {
				List<Namespace> items = clientnew.listAllNamespace().getItems();
				for (Namespace namespace2 : items) {
					if(namespace.equals(namespace2.getMetadata().getNamespace())){
						if(labels != null){
            				 List<GetNodeTask> nodeTasks = new ArrayList<>(labels.size());
            			     nodeTasks.add(new GetNodeTask(clientnew, labels));
            			     List<List<NodeInfo>> nodeLists = ClientConfigure.executeCompletionService(nodeTasks);
            			     for (List<NodeInfo> nodeList : nodeLists) {
            			         nodemap.put(namespace, nodeList);
            			         resultList.add(nodemap);
            			     }	
            			}else{
            				List<GetNodeTask> nodeTasks = new ArrayList<>(labels.size());
           			        nodeTasks.add(new GetNodeTask(clientnew, null));
           			        List<List<NodeInfo>> nodeLists = ClientConfigure.executeCompletionService(nodeTasks);
           			        for (List<NodeInfo> nodeList : nodeLists) {
           			           nodemap.put(namespace, nodeList);
           			           resultList.add(nodemap);
           			       }	
						}
					}
				}
			}
    	}else{
    		NodeList listNode = clientnew.listNode();
    		NamespaceList listAllNamespace = clientnew.listAllNamespace();
			List<Namespace> items = clientnew.listAllNamespace().getItems();
			for (Namespace namespace : items) {
				if(labels != null){
    				 List<GetNodeTask> nodeTasks = new ArrayList<>(labels.size());
    			     nodeTasks.add(new GetNodeTask(clientnew, labels));
    			     List<List<NodeInfo>> nodeLists = ClientConfigure.executeCompletionService(nodeTasks);
    			     for (List<NodeInfo> nodeList : nodeLists) {
    			         nodemap.put(namespace.getMetadata().getNamespace(), nodeList);
    			         resultList.add(nodemap);
    			     }	
    			}else{
    				 List<GetNodeTask> nodeTasks = new ArrayList<>(labels.size());
    			     nodeTasks.add(new GetNodeTask(clientnew, null));
    			     List<List<NodeInfo>> nodeLists = ClientConfigure.executeCompletionService(nodeTasks);
    			     for (List<NodeInfo> nodeList : nodeLists) {
    			         nodemap.put(namespace.getMetadata().getNamespace(), nodeList);
    			         resultList.add(nodemap);
    			     }	
				}
			}
    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return resultList;
    }
    
    private class GetNodeTask implements Callable<List<NodeInfo>>{
    	private KubeUtils<?> client;
    	private List<Map<String,String>> labels;
    	GetNodeTask(KubeUtils<?> client,List<Map<String,String>> labels){
    		this.client = client;
    		this.labels = labels;
    	}
		@Override
		public List<NodeInfo> call() throws Exception {
			List<NodeInfo> nodeList = new ArrayList<>();
			if(labels != null){
				for (Map<String,String> label : labels) {
					NodeList listNode = client.listNode(label);
					for(Node node:listNode.getItems()){
						NodeInfo nodeInfo = new NodeInfo();
						nodeInfo = generateNodeInfo(node);
						nodeList.add(nodeInfo);
					}
				}
			}else{
				NodeList listNode = client.listNode();
				for(Node node:listNode.getItems()){
					NodeInfo nodeInfo = new NodeInfo();
					nodeInfo = generateNodeInfo(node);
					nodeList.add(nodeInfo);
				}
			}
			return nodeList;
		}
    }*/
    
    public NodeInfo getNodeInfo(String name) {
        NodeList nodeList = getNodeList();
        NodeInfo nodeInfo = null;
        if (nodeList != null && nodeList.getItems() != null) {
            for (Node node : nodeList.getItems()) {
//            	node.getMetadata().getNamespace()
                if (node.getMetadata() != null && name.equals(node.getMetadata().getName())) {
                    try {
                        nodeInfo = generateNodeInfo(node);
                    } catch (ParseException e) {
                        throw ApiException.wrapUnknownException(e);
                    }
                    break;
                }
            }
        }
        return nodeInfo;
    }

//    public List<Instance> getInstanceWithNodeLabels(Map<String, String> labels) throws ParseException {
//        NodeList nodeList = getNodeListByLabels(labels);
//        if (nodeList != null && nodeList.getItems() != null) {
//            List<String> nodeNameList = new ArrayList<>(nodeList.getItems().size());
//            for (Node node : nodeList.getItems()) {
//                nodeNameList.add(node.getMetadata().getName());
//            }
//            PodList podList = getAllPods();
//            if (podList != null && !podList.getItems().isEmpty()) {
//                List<Instance> instances = new ArrayList<>(podList.getItems().size());
//                for (Pod pod : podList.getItems()) {
//                    if (pod.getSpec() != null && nodeNameList.contains(pod.getSpec().getNodeName())) {
//                        try {
//                            Instance instance = transferPodToInstance(pod);
//                            if (instance != null && !instance.getStatus().equalsIgnoreCase("Completed")) {
//                                instances.add(instance);
//                            }
//                        } catch (Exception ignored) {
//                        }
//                    }
//                }
//                return instances;
//            }
//            return new ArrayList<>(1);
//        } else {
//            return null;
//        }
//    }

//    public List<Instance> getInstance(String nodeName) throws ParseException {
//        if (StringUtils.isBlank(nodeName)) {
//            return null;
//        }
//        PodList podList = getAllPods();
//        if (podList != null && !podList.getItems().isEmpty()) {
//            List<Instance> instances = new ArrayList<>(podList.getItems().size());
//            for (Pod pod : podList.getItems()) {
//                if (pod.getSpec() != null && nodeName.equals(pod.getSpec().getNodeName())) {
//                    try {
//                        Instance instance = transferPodToInstance(pod);
//                        if (instance != null && !instance.getStatus().equalsIgnoreCase("Completed")) {
//                            instances.add(instance);
//                        }
//                    } catch (Exception ignored) {
//                    }
//                }
//            }
//            return instances;
//        }
//        return new ArrayList<>(1);
//    }

    public List<Instance> getInstance() throws ParseException {
        PodList podList = getAllPods();
        if (podList != null && !podList.getItems().isEmpty()) {
            List<Instance> instances = new ArrayList<>(podList.getItems().size());
            for (Pod pod : podList.getItems()) {
                try {
                    Instance instance = transferPodToInstance(pod);
                    if (instance != null && !instance.getStatus().equalsIgnoreCase("Completed")) {
                        instances.add(instance);
                    }
                } catch (Exception ignored) {
                	ignored.printStackTrace();
                }
            }
            return instances;
        }
        return new ArrayList<>(1);
    }

    private Instance transferPodToInstance(Pod pod) throws ParseException {
        if (pod == null) {
            return null;
        }

        Instance instance = new Instance();
        instance.setHostName(pod.getSpec().getNodeName());
        if (pod.getMetadata() != null) {
            instance.setInstanceName(pod.getMetadata().getName());
            instance.setNamespace(pod.getMetadata().getNamespace());
            if (pod.getMetadata().getLabels() != null) {
                if (pod.getMetadata().getLabels().containsKey(GlobalConstant.DEPLOY_ID_STR) &&
                        pod.getMetadata().getLabels().containsKey(GlobalConstant.VERSION_STR)) {
                    String serviceId = pod.getMetadata().getLabels().get(GlobalConstant.DEPLOY_ID_STR);
//                    int versionId = Integer.valueOf(pod.getMetadata().getLabels().get(GlobalConstant.VERSION_STR));
                    instance.setServiceId(serviceId);
                    ServiceInfo service = serviceDao.getService(serviceId);
                    instance.setServiceCode(service.getServiceCode());
//                    instance.setVersion(versionId);
                }
            }
        }

        if (pod.getStatus() != null) {
            instance.setStartTime(pod.getStatus().getStartTime());
            instance.setPodIp(pod.getStatus().getPodIP());
            instance.setHostIp(pod.getStatus().getHostIP());
            if (pod.getStatus().getContainerStatuses() != null) {
                for (ContainerStatus containerStatus : pod.getStatus().getContainerStatuses()) {
                    if (StringUtils.isBlank(containerStatus.getContainerID())) {
                        continue;
                    }
                    String containerId = containerStatus.getContainerID().split("docker://")[1];
                    instance.addContainer(new org.k8scmp.appmgmt.domain.Container(containerId,
                            containerStatus.getName(), containerStatus.getImage()));
                }
            }
        }
        instance.setStatus(PodUtils.getPodStatus(pod));

        return instance;
    }


//    public Map<String, String> getClusterLabels() {
//        Map<String, String> labels = new HashMap<>();
//        NodeList nodeList = getNodeList();
//        if (nodeList != null && nodeList.getItems() != null) {
//            for (Node node : nodeList.getItems()) {
//                if (node.getMetadata() != null && node.getMetadata().getLabels() != null) {
//                    labels.putAll(node.getMetadata().getLabels());
//                }
//            }
//        }
//        return labels;
//    }

    public List<NodeInfo> getNodeListByLabel(Map<String, String> labels) {
        NodeList nodeList = getNodeListByLabels(labels);
        if (nodeList != null && nodeList.getItems() != null) {
            List<NodeInfo> nodeInfos = new ArrayList<>(nodeList.getItems().size());
            for (Node node : nodeList.getItems()) {
                NodeInfo nodeInfo = null;
                try {
                    nodeInfo = generateNodeInfo(node);
                } catch (ParseException e) {
                    logger.error("parse node info error: " + e.getMessage());
                }
                nodeInfos.add(nodeInfo);
            }
            return nodeInfos;
        }
        return null;
    }

//    public List<NamespaceInfo> getAllNamespaces() {
//        try {
//            NamespaceList namespaceList = client.listAllNamespace();
//            List<NamespaceInfo> namespaceInfos = new LinkedList<>();
//            if (namespaceList != null && namespaceList.getItems() != null) {
//                for (Namespace namespace : namespaceList.getItems()) {
//                    if (namespace.getMetadata() != null) {
//                        namespaceInfos.add(new NamespaceInfo(namespace.getMetadata().getName(),
//                                DateUtil.string2timestamp(namespace.getMetadata().getCreationTimestamp(), TimeZone.getTimeZone(GlobalConstant.UTC_TIME))));
//                    }
//                }
//            }
//            return namespaceInfos;
//        } catch (K8sDriverException | ParseException e) {
//            logger.warn("get all namespaces error in {} , message is " + e.getMessage(), client.info());
//            return null;
//        }
//    }

//    public boolean setNamespaces(List<String> namespaces) {
//        if (namespaces != null) {
//            for (String name : namespaces) {
//                Namespace namespace = new Namespace();
//                ObjectMeta objectMeta = new ObjectMeta();
//                objectMeta.setName(name);
//                namespace.setMetadata(objectMeta);
//                try {
//                    client.createNamespace(namespace);
//                } catch (K8sDriverException e) {
//                    logger.warn("put namespace error, message is " + e.getMessage());
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

//    public boolean deleteSecret(String secretName) {
//        if (StringUtils.isBlank(secretName)) {
//            return false;
//        }
//        try {
//            client.deleteSecret(GlobalConstant.SECRET_NAME_PREFIX + secretName);
//        } catch (K8sDriverException e) {
//            logger.warn("delete secret error, message is " + e.getMessage());
//            return false;
//        }
//        return true;
//    }

    public PodList getPods(Map<String, String> labels) {
        try {
            if (labels == null) {
                return null;
            }
            return client.listAllPod(labels);
        } catch (Exception e) {
            logger.warn("get deployment pod list by labels error, message is " + e.getMessage());
            return null;
        }
    }

    public PodList getAllPods() {
        try {
            return client.listAllPod();
        } catch (Exception e) {
            logger.warn("get deployment pod list by labels error, message is " + e.getMessage());
            return null;
        }
    }

//    public LogWatch fetchContainerLogs(String podName, String containerName) throws JobLogException {
//        try {
//            return (LogWatch) client.tailfLog(podName, containerName, 10);
//        } catch (K8sDriverException e) {
//            logger.warn("get container log error, message is " + e.getMessage());
//            throw new JobLogException(e.getMessage());
//        }
//    }

//    public boolean setNodeLabels(String nodeName, Map<String, String> labels) throws Exception {
//        try {
//            Node node = client.labelNode(nodeName, labels);
//            return node != null;
//        } catch (K8sDriverException e) {
//            logger.warn("set node label error, message is " + e.getMessage());
//            throw new Exception("set node labels error, message is " + e.getMessage());
//        }
//    }

//    public boolean deleteNodeLabels(String nodeName, List<String> labels) throws Exception {
//        try {
//            Node node = client.deleteNodeLabel(nodeName, labels);
//            return node != null;
//        } catch (K8sDriverException e) {
//            logger.warn("delete node labels error, message is " + e.getMessage());
//            throw new Exception("delete node labels error, message is " + e.getMessage());
//        }
//    }

//    public boolean addNodeDisk(String nodeName, String diskPath) throws Exception {
//        Node node = null;
//        try {
//            Map<String, String> annotation = new HashMap<>();
//            annotation.put(GlobalConstant.DISK_STR, diskPath);
//            node = client.annotateNode(nodeName, annotation);
//        } catch (K8sDriverException e) {
//            logger.warn("add node labels error, message is " + e.getMessage());
//            throw new Exception("add node disk error, message is " + e.getMessage());
//        }
//        return node != null && node.getMetadata() != null
//                && node.getMetadata().getAnnotations() != null
//                && node.getMetadata().getAnnotations().containsKey(GlobalConstant.DISK_STR);
//    }

//    public boolean deleteNodeDisk(String nodeName) throws Exception {
//        Node node = null;
//        try {
//            List<String> diskAnnotation = new LinkedList<>();
//            diskAnnotation.add(GlobalConstant.DISK_STR);
//            node = client.deleteNodeAnnotation(nodeName, diskAnnotation);
//        } catch (K8sDriverException e) {
//            logger.warn("delete node disk error, message is " + e.getMessage());
//            throw new Exception("delete node disk error, message is " + e.getMessage());
//        }
//        return node != null && node.getMetadata() != null
//                && node.getMetadata().getAnnotations() != null
//                && !node.getMetadata().getAnnotations().containsKey(GlobalConstant.DISK_STR);
//    }

//    public int getNodeCount() {
//        NodeList nodeList = getNodeList();
//        if (nodeList != null && nodeList.getItems() != null) {
//            return nodeList.getItems().size();
//        }
//        return 0;
//    }

//    public int getPodCount() {
//        PodList podList = getAllPods();
//        if (podList != null && podList.getItems() != null) {
//            return getRunningPodNumbers(podList.getItems());
//        }
//        return 0;
//    }

    private NodeList getNodeList() {
        try {
            return client.listNode();
        } catch (K8sDriverException e) {
            logger.warn("get node list error, message is " + e.getMessage());
            return null;
        }
    }

    private NodeList getNodeListByLabels(Map<String, String> labels) {
        try {
            if (labels == null) {
                return null;
            }
            return client.listNode(labels);
        } catch (K8sDriverException e) {
            logger.warn("get node list by labels error, message is " + e.getMessage());
            return null;
        }
    }

    private List<Pod> getPodListByNode(String nodeName) {
        try {
            PodList podList = client.listAllPod();
            List<Pod> pods = new LinkedList<>();
            if (podList != null && podList.getItems() != null) {
                for (Pod pod : podList.getItems()) {
                    if (pod.getSpec() != null && nodeName.equals(pod.getSpec().getNodeName())) {
                        pods.add(pod);
                    }
                }
            }
            return pods;
        } catch (Exception e) {
            logger.warn("get pod list error, message is " + e.getMessage());
            return null;
        }
    }

    private int getRunningPodNumbers(List<Pod> pods) {
        if (pods != null) {
            return PodUtils.getPodReadyNumber(pods);
        } else {
            return 0;
        }
    }

    private NodeInfo generateNodeInfo(Node node) throws ParseException {
        NodeInfo nodeInfo = new NodeInfo();
        if (node.getMetadata() != null) {
            nodeInfo.setLabels(node.getMetadata().getLabels());
            nodeInfo.setName(node.getMetadata().getName());
            nodeInfo.setRunningPods(getRunningPodNumbers(getPodListByNode(nodeInfo.getName())));
            if (node.getMetadata().getAnnotations() != null) {
                nodeInfo.setDiskInfo(node.getMetadata().getAnnotations().get(GlobalConstant.DISK_STR));
            }
            nodeInfo.setCreateTime(DateUtil.string2timestamp(node.getMetadata().getCreationTimestamp(), TimeZone.getTimeZone(GlobalConstant.UTC_TIME)));
        }
        if (node.getStatus() != null) {
            if (node.getStatus().getAddresses() != null) {
                for (NodeAddress nodeAddress : node.getStatus().getAddresses()) {
                    if ("internalip".equalsIgnoreCase(nodeAddress.getType())) {
                        nodeInfo.setIp(nodeAddress.getAddress());
                    }
                }
            }
            if (node.getStatus().getNodeInfo() != null) {
                NodeSystemInfo nodeSystemInfo = node.getStatus().getNodeInfo();
                if (StringUtils.isNotBlank(nodeSystemInfo.getContainerRuntimeVersion())
                        && nodeSystemInfo.getContainerRuntimeVersion().startsWith("docker://")) {
                    nodeInfo.setDockerVersion(nodeSystemInfo.getContainerRuntimeVersion().replace("docker://", ""));
                }
                if (StringUtils.isNotBlank(nodeSystemInfo.getKubeletVersion())) {
                    nodeInfo.setKubeletVersion(nodeSystemInfo.getKubeletVersion());
                }
                if (StringUtils.isNotBlank(nodeSystemInfo.getKernelVersion())) {
                    nodeInfo.setKernelVersion(nodeSystemInfo.getKernelVersion());
                }
                if (StringUtils.isNotBlank(nodeSystemInfo.getOsImage())) {
                    nodeInfo.setOsVersion(nodeSystemInfo.getOsImage());
                } else if (StringUtils.isNotBlank(nodeSystemInfo.getOperatingSystem())) {
                    nodeInfo.setOsVersion(nodeSystemInfo.getOperatingSystem());
                }
            }
            Map<String, Quantity> capacity = node.getStatus().getCapacity();
            if (capacity != null) {
                capacity.put("memory", CommonUtil.getMemory(capacity.get("memory")));
            }
            nodeInfo.setCapacity(capacity);
        }
        if (NodeUtils.isReady(node)) {
            nodeInfo.setStatus("Ready");
        } else {
            nodeInfo.setStatus("NotReady");
        }
        return nodeInfo;
    }
}