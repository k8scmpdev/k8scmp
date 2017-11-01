package org.k8scmp.engine.k8s.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.ClusterInfo;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.globalmgmt.service.GlobalService;
import org.k8scmp.globalmgmt.service.impl.GlobalServiceImpl;
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

	@Autowired
	GlobalBiz globalBiz;
	@Autowired
	GlobalService globalService;

	public String createLabels(KubeUtils<?> client, List<String> nodeNames, Map<String, String> label) {
		try {
			if (label != null && label.size() != 0) {
				for (Node node : client.listNode().getItems()) {
					Map<String, String> labels = node.getMetadata().getLabels();
					for (String key : labels.keySet()) {
						for (String k : label.keySet()) {
							if (k.equals(key)) {
								return "label is alredy exit";
							}
						}
					}
				}
				for (String nodeName : nodeNames) {
					client.labelNode(nodeName, label);
				}
				return "SUCCESS";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String removeLabels(KubeUtils<?> client, String nodeName, List<String> labels) {
		try {
			Node node = client.deleteNodeLabel(nodeName, labels);
			Map<String, String> nodeLabel = node.getMetadata().getLabels();
			for (String label : nodeLabel.keySet()) {
				for (String str : labels) {
					if (!str.equals(label)) {
						return "SUCCESS";
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 获取对应namespace下的所有labels
	 */
	public List<LabelInfo> getAllLabels() {
		KubeUtils<?> client;
		try {
			List<Cluster> clusters = globalService.getAllCluster();
			List<LabelInfo> labelInfos = new ArrayList<>();
			for (Cluster c : clusters) {
				String cluster_host = c.getApi();
				String cluster_name = c.getName();
				Cluster clusterNew = new Cluster();
				clusterNew.setApi(cluster_host);
				clusterNew.setName(cluster_name);
				client = Fabric8KubeUtils.buildKubeUtils(clusterNew, null);
				NodeList nodeList = client.listNode();
				Map<String, Set<String>> labelnodes = new HashMap<>();
				for (Node node : nodeList.getItems()) {
					Map<String, String> labels = node.getMetadata().getLabels();
					for (String labelkey : labels.keySet()) {
						if (labelnodes.get(labelkey) == null) {
							Set<String> set = new HashSet<>();
							set.add(node.getMetadata().getName());
							labelnodes.put(labelkey, set);
						} else {
							labelnodes.get(labelkey).add(node.getMetadata().getName());
						}
					}
				}
				for (String labelName : labelnodes.keySet()) {
					LabelInfo labelInfo = new LabelInfo();
					// labelInfo.setNamespace(namespace.getMetadata().getName());
					labelInfo.setLabelName(labelName);
					ClusterInfo clusterInfo = new ClusterInfo();
					clusterInfo.setApiserver(clusterNew.getApi());
					clusterInfo.setName(clusterNew.getName());
					labelInfo.setClusterInfo(clusterInfo);
					Set<String> labelset = labelnodes.get(labelName);
					List<String> nodeNames = new ArrayList<>();
					int count = 0;
					for (String nodeName : labelset) {
						nodeNames.add(nodeName);
						count++;
					}
					labelInfo.setNodeName(nodeNames);
					labelInfo.setHostCount(count);
					labelInfos.add(labelInfo);
				}
			}
			return labelInfos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据cluster，namespace，labels查询node下的pod
	 */
	public List<PodInfo> getPodListByClusterNamespaceLabels(Cluster cluster, List<String> namespaces,
			List<Map<String, String>> labels) {
		try {
			List<PodInfo> podInfoList = new ArrayList<>();
			Map<String, Set<PodInfo>> podInfoMap = new HashMap<>();
			Set<PodInfo> podInfoSet = new HashSet<>();
			if (cluster == null) {
				GlobalInfo cluster_host = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
				GlobalInfo cluster_name = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
				Cluster clusterNew = new Cluster();
				clusterNew.setApi(cluster_host.getValue());
				clusterNew.setId(cluster_host.getId() + "");
				clusterNew.setName(cluster_name.getValue());
				if (namespaces != null) {
					for (String namespace : namespaces) {
						podInfoSet.clear();
						List<GetPodTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetPodTask(labels, clusterNew, namespace));
						List<List<PodInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<PodInfo> podList : list) {
							for (PodInfo podInfo : podList) {
								podInfoSet.add(podInfo);
							}
						}
						podInfoMap.put(namespace, podInfoSet);
					}
				} else {
					// namespace == null
					KubeUtils<?> client = Fabric8KubeUtils.buildKubeUtils(clusterNew, null);
					NamespaceList namespaceList = client.listAllNamespace();
					for (Namespace namespace : namespaceList.getItems()) {
						podInfoSet.clear();
						List<GetPodTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetPodTask(labels, clusterNew, namespace.getMetadata().getName()));
						List<List<PodInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<PodInfo> podList : list) {
							for (PodInfo podInfo : podList) {
								podInfoSet.add(podInfo);
							}
						}
						podInfoMap.put(namespace.getMetadata().getName(), podInfoSet);
					}
				}
			} else {
				// cluster != null
				if (namespaces != null) {
					for (String namespace : namespaces) {
						podInfoSet.clear();
						List<GetPodTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetPodTask(labels, cluster, namespace));
						List<List<PodInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<PodInfo> podList : list) {
							for (PodInfo podInfo : podList) {
								podInfoSet.add(podInfo);
							}
						}
						podInfoMap.put(namespace, podInfoSet);
					}
				} else {
					// namespace == null
					KubeUtils<?> client = Fabric8KubeUtils.buildKubeUtils(cluster, null);
					NamespaceList namespaceList = client.listAllNamespace();
					for (Namespace namespace : namespaceList.getItems()) {
						podInfoSet.clear();
						List<GetPodTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetPodTask(labels, cluster, namespace.getMetadata().getName()));
						List<List<PodInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<PodInfo> podList : list) {
							for (PodInfo podInfo : podList) {
								podInfoSet.add(podInfo);
							}
						}
						podInfoMap.put(namespace.getMetadata().getName(), podInfoSet);
					}
				}
			}
			for (String namespace : podInfoMap.keySet()) {
				Set<PodInfo> podInfos = podInfoMap.get(namespace);
				for (PodInfo podInfo : podInfos) {
					podInfoList.add(podInfo);
				}
			}
			return podInfoList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private class GetPodTask implements Callable<List<PodInfo>> {

		private KubeUtils<?> client;
		private List<Map<String, String>> labels;

		GetPodTask(List<Map<String, String>> labels, Cluster cluster, String namespace) throws Exception {
			this.client = Fabric8KubeUtils.buildKubeUtils(cluster, namespace);
			this.labels = labels;
		}

		@Override
		public List<PodInfo> call() throws Exception {
			List<PodInfo> podList = new ArrayList<>();
			if (labels != null || labels.size() != 0) {
				for (Map<String, String> label : labels) {
					PodList listPod = client.listPod(label);
					for (Pod pod : listPod.getItems()) {
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
			} else {
				PodList listPod = client.listPod();
				for (Pod pod : listPod.getItems()) {
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

	/**
	 * 根据cluster，namespace，labels查询node
	 */
	public List<NodeInfo> getNodeListByClusterNamespaceLabels(Cluster cluster, List<String> namespaces,
			List<Map<String, String>> labels) {

		try {
			List<NodeInfo> nodeInfoList = new ArrayList<>();
			Map<String, Set<NodeInfo>> nodeInfoMap = new HashMap<>();
			Set<NodeInfo> nodeInfoSet = new HashSet<>();
			if (cluster == null) {
				GlobalInfo cluster_host = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
				GlobalInfo cluster_name = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
				Cluster clusterNew = new Cluster();
				clusterNew.setApi(cluster_host.getValue());
				clusterNew.setId(cluster_host.getId() + "");
				clusterNew.setName(cluster_name.getValue());
				if (namespaces != null) {
					for (String namespace : namespaces) {
						nodeInfoSet.clear();
						List<GetNodeTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetNodeTask(labels, clusterNew, namespace));
						List<List<NodeInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<NodeInfo> nodeList : list) {
							for (NodeInfo nodeInfo : nodeList) {
								nodeInfoSet.add(nodeInfo);
							}
						}
						nodeInfoMap.put(namespace, nodeInfoSet);
					}
				} else {
					// namespace == null
					KubeUtils<?> client = Fabric8KubeUtils.buildKubeUtils(clusterNew, null);
					NamespaceList namespaceList = client.listAllNamespace();
					for (Namespace namespace : namespaceList.getItems()) {
						nodeInfoSet.clear();
						List<GetNodeTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetNodeTask(labels, clusterNew, namespace.getMetadata().getName()));
						List<List<NodeInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<NodeInfo> nodeList : list) {
							for (NodeInfo nodeInfo : nodeList) {
								nodeInfoSet.add(nodeInfo);
							}
						}
						nodeInfoMap.put(namespace.getMetadata().getName(), nodeInfoSet);
					}
				}
			} else {
				// cluster != null
				if (namespaces != null) {
					for (String namespace : namespaces) {
						nodeInfoSet.clear();
						List<GetNodeTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetNodeTask(labels, cluster, namespace));
						List<List<NodeInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<NodeInfo> nodeList : list) {
							for (NodeInfo nodeInfo : nodeList) {
								nodeInfoSet.add(nodeInfo);
							}
						}
						nodeInfoMap.put(namespace, nodeInfoSet);
					}
				} else {
					// namespace == null
					KubeUtils<?> client = Fabric8KubeUtils.buildKubeUtils(cluster, null);
//					NamespaceList namespaceList = client.listAllNamespace();
//					for (Namespace namespace : namespaceList.getItems()) {
						nodeInfoSet.clear();
						List<GetNodeTask> infoTasks = new LinkedList<>();
						infoTasks.add(new GetNodeTask(labels, cluster, null));
						List<List<NodeInfo>> list = ClientConfigure.executeCompletionService(infoTasks);
						for (List<NodeInfo> nodeList : list) {
							for (NodeInfo nodeInfo : nodeList) {
								nodeInfo.setCluster(cluster);
								nodeInfoList.add(nodeInfo);
							}
						}
						nodeInfoMap.put(null, nodeInfoSet);
//					}
				}
			}
//			for (String namespace : nodeInfoMap.keySet()) {
//				Set<NodeInfo> nodeInfos = nodeInfoMap.get(namespace);
//				for (NodeInfo nodeInfo : nodeInfos) {
//					nodeInfo.setNamespace(namespace);
//					nodeInfo.setCluster(cluster);
//					nodeInfoList.add(nodeInfo);
//				}
//			}
			return nodeInfoList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private class GetNodeTask implements Callable<List<NodeInfo>> {
		KubeUtils<?> client;
		List<Map<String, String>> labels;

		GetNodeTask(List<Map<String, String>> labels, Cluster clusterNew, String namespace) throws Exception {
			this.client = Fabric8KubeUtils.buildKubeUtils(clusterNew, namespace);
			this.labels = labels;
		}

		@Override
		public List<NodeInfo> call() throws Exception {
			List<NodeInfo> nodeList = new ArrayList<>();
			if (labels != null && labels.size() != 0) {
				for (Map<String, String> label : labels) {
					NodeList listNode = client.listNode(label);
					for (Node node : listNode.getItems()) {
						NodeInfo nodeInfo = new NodeInfo();
						nodeInfo = generateNodeInfo(node, client);
						nodeList.add(nodeInfo);
					}
				}
			} else {
				NodeList listNode = client.listNode();
				for (Node node : listNode.getItems()) {
					NodeInfo nodeInfo = new NodeInfo();
					nodeInfo = generateNodeInfo(node, client);
					nodeList.add(nodeInfo);
				}
			}
			return nodeList;
		}
	}

	private List<Pod> getPodListByNode(String nodeName, KubeUtils<?> client) {
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

	private NodeInfo generateNodeInfo(Node node, KubeUtils<?> client) throws ParseException {
		NodeInfo nodeInfo = new NodeInfo();
		if (node.getMetadata() != null) {
			nodeInfo.setLabels(node.getMetadata().getLabels());
			nodeInfo.setName(node.getMetadata().getName());
			nodeInfo.setRunningPods(getRunningPodNumbers(getPodListByNode(nodeInfo.getName(), client)));
			if (node.getMetadata().getAnnotations() != null) {
				nodeInfo.setDiskInfo(node.getMetadata().getAnnotations().get(GlobalConstant.DISK_STR));
			}
			nodeInfo.setCreateTime(DateUtil.string2timestamp(node.getMetadata().getCreationTimestamp(),
					TimeZone.getTimeZone(GlobalConstant.UTC_TIME)));
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
