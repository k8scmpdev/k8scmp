package org.k8scmp.engine.k8s.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
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

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Quantity;

@Component("nodeWrapperNew")
public class NodeWrapperNew {
	private Logger logger = LoggerFactory.getLogger(NodeWrapper.class);
//	private KubeUtils client;
	 
	@Autowired
    GlobalBiz globalBiz;
	
	/*public List<Map<String,List<PodInfo>>> getPodListByClusterNamespaceLabels(Cluster cluster,List<String> namespaces,List<Map<String,String>> labels){
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
				client = Fabric8KubeUtils.buildKubeUtils(clusterNew, "default");
	    	}else{
	    		client = Fabric8KubeUtils.buildKubeUtils(cluster, "default");
	    	}
	    	if(namespaces != null){
				for (String namespace : namespaces) {
					List<Namespace> items = client.listAllNamespace().getItems();
					for (Namespace namespace2 : items) {
						if(namespace.equals(namespace2.getMetadata().getNamespace())){
							if(labels != null){
								List<GetPodTask> nodeTasks = new ArrayList<>(labels.size());
	            			     nodeTasks.add(new GetPodTask(client, labels));
	            			     List<List<PodInfo>> podLists = ClientConfigure.executeCompletionService(nodeTasks);
	            			     for (List<PodInfo> podList : podLists) {
	            			         podmap.put(namespace, podList);
	            			         mapList.add(podmap);
	            			     }	
							}else{
								List<GetPodTask> nodeTasks = new ArrayList<>();
	            			     nodeTasks.add(new GetPodTask(client, null));
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
				List<Namespace> items = client.listAllNamespace().getItems();
				for (Namespace namespace2 : items) {
					if(labels != null){
						 List<GetPodTask> nodeTasks = new ArrayList<>(labels.size());
	       			     nodeTasks.add(new GetPodTask(client, labels));
	       			     List<List<PodInfo>> podLists = ClientConfigure.executeCompletionService(nodeTasks);
	       			     for (List<PodInfo> podList : podLists) {
	       			         podmap.put(namespace2.getMetadata().getName(), podList);
	       			         mapList.add(podmap);
	       			     }	
					}else{
						List<GetPodTask> nodeTasks = new ArrayList<>();
	       			     nodeTasks.add(new GetPodTask(client, null));
	       			     List<List<PodInfo>> podLists = ClientConfigure.executeCompletionService(nodeTasks);
	       			     for (List<PodInfo> podList : podLists) {
	       			         podmap.put(namespace2.getMetadata().getName(), podList);
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
   }*/
    
    /**
     * 根据cluster，namespace，labels查询node
     * */
    public List<Map<String,List<NodeInfo>>> getNodeListByClusterNamespaceLabels(Cluster clusters,List<String> namespaces,List<Map<String,String>> labels){
    	//当cluster等于空时新建一个cluster
    	KubeUtils client = null;
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
			client= Fabric8KubeUtils.buildKubeUtils(clusterNew, "default");
    	}else{
    		client = Fabric8KubeUtils.buildKubeUtils(clusters, "default");
        }
		if(namespaces != null){
    		for (String namespace : namespaces) {
				List<Namespace> items = client.listAllNamespace().getItems();
				for (Namespace namespace2 : items) {
					if(namespace.equals(namespace2.getMetadata().getNamespace())){
						if(labels != null){
            				 List<GetNodeTask> nodeTasks = new ArrayList<>(labels.size());
            			     nodeTasks.add(new GetNodeTask(client, labels));
            			     List<List<NodeInfo>> nodeLists = ClientConfigure.executeCompletionService(nodeTasks);
            			     for (List<NodeInfo> nodeList : nodeLists) {
            			         nodemap.put(namespace, nodeList);
            			         resultList.add(nodemap);
            			     }	
            			}else{
            				List<GetNodeTask> nodeTasks = new ArrayList<>();
           			        nodeTasks.add(new GetNodeTask(client, null));
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
    		NamespaceList listAllNamespace = client.listAllNamespace();
			List<Namespace> items = client.listAllNamespace().getItems();
			for (Namespace namespace : items) {
				if(labels != null){
    				 List<GetNodeTask> nodeTasks = new ArrayList<>(labels.size());
    			     nodeTasks.add(new GetNodeTask(client, labels));
    			     List<List<NodeInfo>> nodeLists = ClientConfigure.executeCompletionService(nodeTasks);
    			     for (List<NodeInfo> nodeList : nodeLists) {
    			         nodemap.put(namespace.getMetadata().getName(), nodeList);
    			         resultList.add(nodemap);
    			     }	
    			}else{
    				 List<GetNodeTask> nodeTasks = new ArrayList<>();
    			     nodeTasks.add(new GetNodeTask(client, null));
    			     List<List<NodeInfo>> nodeLists = ClientConfigure.executeCompletionService(nodeTasks);
    			     for (List<NodeInfo> nodeList : nodeLists) {
    			         nodemap.put(namespace.getMetadata().getName(), nodeList);
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
    	private KubeUtils client;
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
						nodeInfo = generateNodeInfo(node,client);
						nodeList.add(nodeInfo);
					}
				}
			}else{
				NodeList listNode = client.listNode();
				for(Node node:listNode.getItems()){
					NodeInfo nodeInfo = new NodeInfo();
					nodeInfo = generateNodeInfo(node,client);
					nodeList.add(nodeInfo);
				}
			}
			return nodeList;
		}
    }
    
    private List<Pod> getPodListByNode(String nodeName,KubeUtils client) {
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
    
    private NodeInfo generateNodeInfo(Node node,KubeUtils client) throws ParseException {
        NodeInfo nodeInfo = new NodeInfo();
        if (node.getMetadata() != null) {
            nodeInfo.setLabels(node.getMetadata().getLabels());
            nodeInfo.setName(node.getMetadata().getName());
            nodeInfo.setRunningPods(getRunningPodNumbers(getPodListByNode(nodeInfo.getName(),client)));
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
