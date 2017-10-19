package org.k8scmp.monitormgmt.service.monitor.impl;

import com.fasterxml.jackson.core.type.TypeReference;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.engine.k8s.util.NodeWrapper;
import org.k8scmp.engine.k8s.util.NodeWrapperNew;
import org.k8scmp.engine.model.CustomObjectMapper;
import org.k8scmp.exception.ApiException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.monitormgmt.dao.monitor.MonitorDao;
import org.k8scmp.monitormgmt.domain.monitor.ContainerInfo;
import org.k8scmp.monitormgmt.domain.monitor.InstenceInfoBack;
import org.k8scmp.monitormgmt.domain.monitor.MonitorDataRequest;
import org.k8scmp.monitormgmt.domain.monitor.MonitorResult;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfoBack;
import org.k8scmp.monitormgmt.domain.monitor.PodInfo;
import org.k8scmp.monitormgmt.domain.monitor.TargetInfo;
import org.k8scmp.monitormgmt.domain.monitor.TargetRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.EndpointCounter;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryResponse;
import org.k8scmp.monitormgmt.service.monitor.MonitorService;
import org.k8scmp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by jason on 2017/10/13.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private static Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    MonitorDao monitorBiz;

    @Autowired
    GlobalBiz globalBiz;

    @Autowired
    CustomObjectMapper mapper;
    
    @Autowired
    ServiceDao serviceDao;
    
    @Autowired
    AppDao appDao;
    
    public List<NodeInfoBack> getNodeMonitorData(String hostName){
    	//选取当前时间1秒内的值
    	Calendar current = Calendar.getInstance();
        long endTime = current.getTimeInMillis();
        current.set(Calendar.MINUTE, current.get(Calendar.MINUTE) - 1);
        long startTime = current.getTimeInMillis();
    	String dataSpec = "AVERAGE";
    	String type = "node";
    	List<NodeInfoBack> nodeInfoBackList = new ArrayList<>();
    	MonitorResult result = (MonitorResult)getMonitorData(type,startTime,endTime,dataSpec).getResult();
    	NodeWrapper nodeWrapper;
		try {
			nodeWrapper = new NodeWrapper().init("default");
			List<NodeInfo> nodeInfoList = nodeWrapper.getNodeInfoListWithoutPods();
	    	for (NodeInfo nodeInfo : nodeInfoList) {
	    		if(hostName != null && !hostName.equals("") && nodeInfo.getName().indexOf(hostName) >= 0){
	    			NodeInfoBack nodeInfoBack = new NodeInfoBack();
	    	
		    		nodeInfoBack.setHostName(nodeInfo.getName());
		    		nodeInfoBack.setLogicCluster(monitorBiz.getLogicClusterById());
		    		nodeInfoBack.setCPUPercent(formatDouble(result.getCounterResults().get("cpu.busy").get(0).get(nodeInfo.getName())));
		    		nodeInfoBack.setMemoryPercent(formatDouble(result.getCounterResults().get("mem.memused.percent").get(0).get(nodeInfo.getName())));
		    		nodeInfoBack.setDiskPercent(formatDouble(result.getCounterResults().get("df.bytes.used.percent/mount=/").get(0).get(nodeInfo.getName())));
//		    		nodeInfoBack.setNetin(formatDouble(result.getCounterResults().get("net.if.in.bytes").get(0).get(nodeInfo.getName())));
//		    		nodeInfoBack.setNetout(formatDouble(result.getCounterResults().get("net.if.out.bytes").get(0).get(nodeInfo.getName())));
		    		nodeInfoBack.setState(nodeInfo.getStatus());
		    		nodeInfoBackList.add(nodeInfoBack);
	    		}else if(hostName != null && !hostName.equals("") && nodeInfo.getName().indexOf(hostName) < 0){
	    			continue;
	    		}else{
	    			NodeInfoBack nodeInfoBack = new NodeInfoBack();
		    		nodeInfoBack.setHostName(nodeInfo.getName());
		    		nodeInfoBack.setLogicCluster(monitorBiz.getLogicClusterById());
		    		nodeInfoBack.setCPUPercent(formatDouble(result.getCounterResults().get("cpu.busy").get(0).get(nodeInfo.getName())));
		    		nodeInfoBack.setMemoryPercent(formatDouble(result.getCounterResults().get("mem.memused.percent").get(0).get(nodeInfo.getName())));
		    		nodeInfoBack.setDiskPercent(formatDouble(result.getCounterResults().get("df.bytes.used.percent/mount=/").get(0).get(nodeInfo.getName())));
//		    		nodeInfoBack.setNetin(formatDouble(result.getCounterResults().get("net.if.in.bytes").get(0).get(nodeInfo.getName())));
//		    		nodeInfoBack.setNetout(formatDouble(result.getCounterResults().get("net.if.out.bytes").get(0).get(nodeInfo.getName())));
		    		nodeInfoBack.setState(nodeInfo.getStatus());
		    		nodeInfoBackList.add(nodeInfoBack);
	    		}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return nodeInfoBackList;
    }
    
    public String formatDouble(Double d){
    	DecimalFormat df = new DecimalFormat("0.00"); 
    	String str = df.format(d);
    	return str;
    }
    
    
    @Override
    public List<InstenceInfoBack> getInstenceMonitorData(String serviceName){
    	Calendar current = Calendar.getInstance();
        long endTime = current.getTimeInMillis();
        current.set(Calendar.MINUTE, current.get(Calendar.MINUTE) - 1);
        long startTime = current.getTimeInMillis();
    	String dataSpec = "AVERAGE";
    	String type = "pod";
    	List<InstenceInfoBack> instenceList = new ArrayList<>();
    	MonitorResult result = (MonitorResult)getMonitorData(type,startTime,endTime,dataSpec).getResult();
    	NodeWrapper nodeWrapper;
    	try {
			nodeWrapper = new NodeWrapper().init("default");
			List<Pod> podList = nodeWrapper.getAllPods().getItems();
			//通过服务名搜索和获取所有的实例
	    	for (Pod pod : podList) {
	    		InstenceInfoBack instenceInfoBack = new InstenceInfoBack();
	    		String serviceId = pod.getMetadata().getLabels().get(GlobalConstant.DEPLOY_ID_STR);
	    		if(serviceId != null && !serviceId.equals("")){
		    		ServiceInfo service = serviceDao.getService(serviceId);
		    		if(serviceName != null && !serviceName.equals("") && pod.getMetadata().getName().indexOf(serviceName)>=0 ){
		    					instenceInfoBack.setServiceName(service.getServiceCode());
		    					AppInfo app = appDao.getApp(service.getAppId());
		    					instenceInfoBack.setAppName(app.getAppCode());
		    					instenceInfoBack.setInstanceName(pod.getMetadata().getName());
		    					instenceInfoBack.setCPUUsed(formatDouble(result.getCounterResults().get("container.cpu.usage.busy").get(0).get(pod.getMetadata().getName())));
		    					instenceInfoBack.setMemoryUsed(formatDouble(result.getCounterResults().get("container.mem.usage.percent").get(0).get(pod.getMetadata().getName())));
		    					instenceInfoBack.setNetInput(formatDouble(result.getCounterResults().get("container.net.if.in.bytes").get(0).get(pod.getMetadata().getName())));
		    					instenceInfoBack.setNetOutput(formatDouble(result.getCounterResults().get("container.net.if.out.bytes").get(0).get(pod.getMetadata().getName())));
					}else if(serviceName != null && !serviceName.equals("") && pod.getMetadata().getName().indexOf(serviceName)<0){
						continue;
					}else{
						serviceId = pod.getMetadata().getLabels().get(GlobalConstant.DEPLOY_ID_STR);
						service = serviceDao.getService(serviceId);
						AppInfo app = appDao.getApp(service.getAppId());
						instenceInfoBack.setAppName(app.getAppCode());
						instenceInfoBack.setServiceName(service.getServiceCode());
						instenceInfoBack.setInstanceName(pod.getMetadata().getName());
						instenceInfoBack.setCPUUsed(formatDouble(result.getCounterResults().get("container.cpu.usage.busy").get(0).get(pod.getMetadata().getName())));
						instenceInfoBack.setMemoryUsed(formatDouble(result.getCounterResults().get("container.mem.usage.percent").get(0).get(pod.getMetadata().getName())));
						instenceInfoBack.setNetInput(formatDouble(result.getCounterResults().get("container.net.if.in.bytes").get(0).get(pod.getMetadata().getName())));
						instenceInfoBack.setNetOutput(formatDouble(result.getCounterResults().get("container.net.if.out.bytes").get(0).get(pod.getMetadata().getName())));
						
					}
	    		}
	    		instenceList.add(instenceInfoBack);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return instenceList;
    }
    
    @Override
    public HttpResponseTemp<?> getMonitorData(String type, long startTime, long endTime, String dataSpec) {

       // AuthUtil.verify(CurrentThreadInfo.getUserId(), cid, ResourceType.CLUSTER, OperationType.GET);
        
    	//根据type实时查询node\pod\container
    	List<TargetInfo> targetInfos = getTargetInfos(type);
    	
       //TargetRequest targetRequest = fetchTargetRequest(targetId);
       // if (targetRequest == null) {
       //    throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_REQUEST_NOT_LEGAL, "target request info not exists");
       // }
        MonitorDataRequest monitorDataRequest = new MonitorDataRequest(
                startTime,
                endTime,
                dataSpec,
                type,
                targetInfos);

        if (!StringUtils.isBlank(monitorDataRequest.checkLegality())) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_REQUEST_NOT_LEGAL, monitorDataRequest.checkLegality());
        }

        // preparation
        GlobalInfo queryInfo = globalBiz.getGlobalInfoByType(GlobalType.MONITOR_QUERY);
        if (queryInfo == null) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_QUERY_ERROR, "query is null");
        }
        String queryUrl = "http://" + queryInfo.getValue() + "/graph/history";

        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setTargetType(monitorDataRequest.getTargetType());
        monitorResult.setDataSpec(monitorDataRequest.getDataSpec());

        // fetch data from query api
        List<GraphHistoryResponse> graphHistoryResponses = new ArrayList<>();
        // create graphHistoryRequest
        GraphHistoryRequest graphHistoryRequest = getGraphHistoryRequest(monitorDataRequest);
        List<EndpointCounter> endpointCounterList = graphHistoryRequest.getEndpoint_counters();
        //Concurrent requests, sending 500 counters at a time
        int max_size = 500;
        int times = endpointCounterList.size() % max_size == 0 ? endpointCounterList.size() / max_size : endpointCounterList.size() / max_size + 1;
        List<postJsonTask> tasks = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            GraphHistoryRequest tmp = new GraphHistoryRequest();
            tmp.setStart(graphHistoryRequest.getStart());
            tmp.setEnd(graphHistoryRequest.getEnd());
            tmp.setCf(graphHistoryRequest.getCf());
            tmp.setEndpoint_counters(endpointCounterList.subList(i * max_size, Math.min((i + 1) * max_size, endpointCounterList.size())));
            tasks.add(new postJsonTask(queryUrl, tmp));
        }
        List<List<GraphHistoryResponse>> taskResult = ClientConfigure.executeCompletionService(tasks);
        for (List<GraphHistoryResponse> result : taskResult) {
            if (result != null && result.size() > 0) {
                graphHistoryResponses.addAll(result);
            }
        }

        // re-arrage GraphHistoryResponses
        Map<String, List<GraphHistoryResponse>> graphHistoryResponseMap = arrangeGraphHistoryResponseList(graphHistoryResponses,
                monitorDataRequest.getTargetType());

        // create MonitorResult
        createMonitorResult(monitorResult, graphHistoryResponseMap, monitorDataRequest);

        return ResultStat.OK.wrap(monitorResult);
    }
   
    private List<TargetInfo> getTargetInfos(String type) {
    	
		//获取node/pod/container
    	try {
			List<TargetInfo> targetInfoList = new ArrayList<>();
			
//			Cluster clusters = new Cluster();
//			clusters.setApi("192.168.80.137:8080");
//			List<String> namespaces = new ArrayList<>();
//			namespaces.add("default");
//			namespaces.add("kube-system");
//			List<Map<String,String>> labels = new ArrayList<>();
//			Map<String,String> label = new HashMap<>();			
			
//			List<Map<String, List<NodeInfo>>> nodewapp = new NodeWrapperNew().getNodeListByClusterNamespaceLabels(clusters, null, null);
			
			NodeWrapper nodeWrapper = new NodeWrapper().init("default");
			if(type.equals("node")){
				List<NodeInfo> nodeInfoList = nodeWrapper.getNodeListByClusterId();
				for (NodeInfo nodeInfo : nodeInfoList) {
					TargetInfo targetInfo = new TargetInfo();
					targetInfo.setNode(nodeInfo.getName());
					targetInfoList.add(targetInfo);
				}
			}else if(type.equals("pod") || type.equals("container")){
				
				PodList podList = nodeWrapper.getAllPods();
				for (Pod pod : podList.getItems()) {
					TargetInfo targetInfo = new TargetInfo();
					PodInfo podInfo = new PodInfo();
					podInfo.setPodName(pod.getMetadata().getName());
					List<ContainerInfo> containers = new ArrayList<>();
					List<ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
					for (ContainerStatus containerStatus : containerStatuses) {
						ContainerInfo containerInfo = new ContainerInfo();
						containerInfo.setHostname(pod.getSpec().getNodeName());
						containerInfo.setContainerId(containerStatus.getContainerID().substring(9));
						containers.add(containerInfo);
					}
					podInfo.setContainers(containers);
					targetInfo.setPod(podInfo);
					targetInfoList.add(targetInfo);
				}
			}
			return targetInfoList;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	
		return null;
	}

    // create graphHistoryRequest
    private GraphHistoryRequest getGraphHistoryRequest(MonitorDataRequest monitorDataRequest) {

        GraphHistoryRequest graphHistoryRequest = new GraphHistoryRequest();
        graphHistoryRequest.setStart(monitorDataRequest.getStartTime() / 1000);
        graphHistoryRequest.setEnd(monitorDataRequest.getEndTime() / 1000);
        graphHistoryRequest.setCf(monitorDataRequest.getDataSpec());

        List<String> counters = retrieveCountersByTargetInfoList(monitorDataRequest.getTargetType(), monitorDataRequest.getTargetInfos());

        switch (monitorDataRequest.getTargetType()) {
            case "node":
                for (String counter : counters) {
                    for (TargetInfo targetInfo : monitorDataRequest.getTargetInfos()) {
                        graphHistoryRequest.getEndpoint_counters().add(new EndpointCounter(targetInfo.getNode(), counter));
                    }
                }
                break;
            case "pod":
                for (String counter : counters) {
                    for (TargetInfo targetInfo : monitorDataRequest.getTargetInfos()) {
                        for (ContainerInfo containerInfo : targetInfo.getPod().getContainers()) {
                            if (counter.contains(containerInfo.getContainerId())) {
                                graphHistoryRequest.getEndpoint_counters().add(new EndpointCounter(containerInfo.getHostname(), counter));
                            }
                        }
                    }
                }
                break;
            case "container":
                for (String counter : counters) {
                    for (TargetInfo targetInfo : monitorDataRequest.getTargetInfos()) {
                        if (counter.contains(targetInfo.getContainer().getContainerId())) {
                            graphHistoryRequest.getEndpoint_counters().add(new EndpointCounter(targetInfo.getContainer().getHostname(), counter));
                        }
                    }
                }
        }

        return graphHistoryRequest;
    }

    // retrieve sorted counters
    private List<String> retrieveCountersByTargetInfoList(String targetType, List<TargetInfo> targetInfos) {

        if (targetInfos == null || targetInfos.size() == 0) {
            return null;
        }

        Set<String> endpoints = new HashSet<>();
//        List<String> endpoints = new ArrayList<>();
        Set<String> containers = new HashSet<>();

        // collect endpoints and containers
        switch (targetType) {
            case "node":
                for (TargetInfo targetInfo : targetInfos) {
                    endpoints.add(targetInfo.getNode());
                }
                break;
            case "pod":
                for (TargetInfo targetInfo : targetInfos) {
                    for (ContainerInfo containerInfo : targetInfo.getPod().getContainers()) {
                        endpoints.add(containerInfo.getHostname());
                        containers.add(containerInfo.getContainerId());
                    }
                }
                break;
            case "container":
                for (TargetInfo targetInfo : targetInfos) {
                    endpoints.add(targetInfo.getContainer().getHostname());
                    containers.add(targetInfo.getContainer().getContainerId());
                }
        }

        // fetch counters from database
        switch (targetType) {
            case "node":
//            	List<String> countsback = new ArrayList<>();
//            	List<String> counts = new ArrayList<>();
//            	for (String endpoint : endpoints) {
//            		int id = monitorBiz.getEndpointId(endpoint);
//            		countsback = monitorBiz.getNodeCounterByEndpointId(id);
//            		counts.addAll(countsback);
//				}
                return monitorBiz.getNodeCountersByEndpoints(endpoints);
//            	return counts;
            case "pod":
            case "container":
                return monitorBiz.getContainerCountersByEndpoints(joinStringSet(endpoints, ","), joinStringSet(containers, ","));
            default:
                return new ArrayList<>(1);
        }
    }
    
    
    
    @Override
    public List<GraphHistoryResponse> postJson(String requestUrl, GraphHistoryRequest graphHistoryRequest) throws IOException {

        HttpURLConnection conn;
        List<GraphHistoryResponse> graphHistoryResponses;
        try {

            URL url = new URL(requestUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            mapper.writeValue(conn.getOutputStream(), graphHistoryRequest);
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.error("error response code while post:" + responseCode);
                return null;
            }
            InputStream inputStream = conn.getInputStream();
            graphHistoryResponses = mapper.readValue(inputStream, new TypeReference<List<GraphHistoryResponse>>() {
            });
            inputStream.close();
        } catch (Exception e) {

            logger.error("exception in sending post request!", e);
            return null;
        }
        return graphHistoryResponses;
    }
    
    private class postJsonTask implements Callable<List<GraphHistoryResponse>> {

        String requestUrl;
        GraphHistoryRequest graphHistoryRequest;

        public postJsonTask(String requestUrl, GraphHistoryRequest graphHistoryRequest) {
            this.requestUrl = requestUrl;
            this.graphHistoryRequest = graphHistoryRequest;
        }

        @Override
        public List<GraphHistoryResponse> call() throws Exception {
            return postJson(requestUrl, graphHistoryRequest);
        }
    }
    
    
    
    private String joinStringSet(Set<String> stringSet, String delimiter) {

        String result = "";
        if (stringSet == null || stringSet.size() == 0) {
            return result;
        }
        for (String item : stringSet) {
            result += "\"" + item + "\"" + delimiter;
        }
//        String substring = result.substring(0, result.length() - delimiter.length());
        return result.substring(0, result.length() - delimiter.length());
    }
    
    // create graphHistoryRequest for overview
    private GraphHistoryRequest getGraphHistoryRequestForOverview(MonitorDataRequest monitorDataRequest) {

        GraphHistoryRequest graphHistoryRequest = new GraphHistoryRequest();
        graphHistoryRequest.setStart(monitorDataRequest.getStartTime() / 1000);
        graphHistoryRequest.setEnd(monitorDataRequest.getEndTime() / 1000);
        graphHistoryRequest.setCf(monitorDataRequest.getDataSpec());

        List<String> counters = retrieveCountersByTargetInfoList(monitorDataRequest.getTargetType(), monitorDataRequest.getTargetInfos());
        if (counters != null) {
            for (String counter : counters) {
                if (( counter.startsWith("df.bytes") && !counter.startsWith("df.bytes.used.percent") && !counter.contains("kubernetes"))
                        || ( (counter.startsWith("cpu.busy") || counter.startsWith("mem.memtotal") || counter.startsWith("mem.memused")))) {
                    for (TargetInfo targetInfo : monitorDataRequest.getTargetInfos()) {
                        graphHistoryRequest.getEndpoint_counters().add(new EndpointCounter(targetInfo.getNode(), counter));
                    }
                }
            }
        }
        return graphHistoryRequest;
    }
    
    // create graphHistoryRequest for overview
    private GraphHistoryRequest getGraphHistoryRequestForOverview(MonitorDataRequest monitorDataRequest, boolean isDisk) {

        GraphHistoryRequest graphHistoryRequest = new GraphHistoryRequest();
        graphHistoryRequest.setStart(monitorDataRequest.getStartTime() / 1000);
        graphHistoryRequest.setEnd(monitorDataRequest.getEndTime() / 1000);
        graphHistoryRequest.setCf(monitorDataRequest.getDataSpec());

        List<String> counters = retrieveCountersByTargetInfoList(monitorDataRequest.getTargetType(), monitorDataRequest.getTargetInfos());
        if (counters != null) {
            for (String counter : counters) {
                if (( isDisk && counter.startsWith("df.bytes") && !counter.startsWith("df.bytes.used.percent") && !counter.contains("kubernetes"))
                        || (isDisk &&  (counter.startsWith("cpu.busy") || counter.startsWith("mem.memtotal") || counter.startsWith("mem.memused")))) {
                    for (TargetInfo targetInfo : monitorDataRequest.getTargetInfos()) {
                        graphHistoryRequest.getEndpoint_counters().add(new EndpointCounter(targetInfo.getNode(), counter));
                    }
                }
            }
        }
        return graphHistoryRequest;
    }
    
 // re-arrage GraphHistoryResponses
    private Map<String, List<GraphHistoryResponse>> arrangeGraphHistoryResponseList(List<GraphHistoryResponse> graphHistoryResponses,
                                                                                    String targetType) {

        Map<String, List<GraphHistoryResponse>> result = new HashMap<>();

        for (GraphHistoryResponse graphHistoryResponse : graphHistoryResponses) {

            // ignore endpoint-counter pair with no data
            if (graphHistoryResponse.getValues() == null || graphHistoryResponse.getValues().size() == 0) {
                continue;
            }

            // fix counter name
            String counter = graphHistoryResponse.getCounter();
            switch (targetType) {
                case "node":
                    if (counter.startsWith("df.bytes")) {
                        counter = counter.substring(0, counter.indexOf("fstype=")) + counter.substring(counter.indexOf("mount="));
                    }
                    break;
                case "pod":
                case "container":
                    if (!counter.contains("/id=")) {
                        continue;
                    }
                    counter = counter.substring(0, counter.indexOf("/id="));
            }
            if (!result.containsKey(counter)) {
                result.put(counter, new ArrayList<GraphHistoryResponse>());
            }
            result.get(counter).add(graphHistoryResponse);
        }

        return result;
    }
    
    @Override
    public MonitorResult getMonitorDataForOverview(TargetRequest targetRequest, long startTime, long endTime, String dataSpec) {
        if (targetRequest == null) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_REQUEST_NOT_LEGAL, "target request info not exists");
        }
        MonitorDataRequest monitorDataRequest = new MonitorDataRequest(
                startTime,
                endTime,
                dataSpec,
                targetRequest.getTargetType(),
                targetRequest.getTargetInfos());

        if (!StringUtils.isBlank(monitorDataRequest.checkLegality())) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_REQUEST_NOT_LEGAL, monitorDataRequest.checkLegality());
        }

        // preparation
        GlobalInfo queryInfo = globalBiz.getGlobalInfoByType(GlobalType.MONITOR_QUERY);
        if (queryInfo == null) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_QUERY_ERROR, "query is null");
        }
        String queryUrl = "http://" + queryInfo.getValue() + "/graph/history";

        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setTargetType(monitorDataRequest.getTargetType());
        monitorResult.setDataSpec(monitorDataRequest.getDataSpec());

        // fetch data from query api
        // create graphHistoryRequest
        GraphHistoryRequest graphHistoryRequest = getGraphHistoryRequestForOverview(monitorDataRequest);
        List<EndpointCounter> endpointCounterList = graphHistoryRequest.getEndpoint_counters();
        //Concurrent requests, sending 500 counters at a time
        int max_size = 500;
        int times = endpointCounterList.size() % max_size == 0 ? endpointCounterList.size() / max_size : endpointCounterList.size() / max_size + 1;
        List<postJsonTask> tasks = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            GraphHistoryRequest tmp = new GraphHistoryRequest();
            tmp.setStart(graphHistoryRequest.getStart());
            tmp.setEnd(graphHistoryRequest.getEnd());
            tmp.setCf(graphHistoryRequest.getCf());
            tmp.setEndpoint_counters(endpointCounterList.subList(i * max_size, Math.min((i + 1) * max_size, endpointCounterList.size())));
            tasks.add(new postJsonTask(queryUrl, tmp));
        }
        List<List<GraphHistoryResponse>> taskResult = ClientConfigure.executeCompletionService(tasks);
        List<GraphHistoryResponse> graphHistoryResponses = new ArrayList<>();
        for (List<GraphHistoryResponse> result : taskResult) {
            if (result != null && result.size() > 0) {
                graphHistoryResponses.addAll(result);
            }
        }

        // re-arrage GraphHistoryResponses
        Map<String, List<GraphHistoryResponse>> graphHistoryResponseMap = arrangeGraphHistoryResponseList(graphHistoryResponses,
                monitorDataRequest.getTargetType());

        // create MonitorResult
        createMonitorResult(monitorResult, graphHistoryResponseMap, monitorDataRequest);

        return monitorResult;
    }

    @Override
    public MonitorResult getMonitorDataForOverview(TargetRequest targetRequest, long startTime, long endTime, String dataSpec, boolean isDisk) {
        if (targetRequest == null) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_REQUEST_NOT_LEGAL, "target request info not exists");
        }
        MonitorDataRequest monitorDataRequest = new MonitorDataRequest(
                startTime,
                endTime,
                dataSpec,
                targetRequest.getTargetType(),
                targetRequest.getTargetInfos());

        if (!StringUtils.isBlank(monitorDataRequest.checkLegality())) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_REQUEST_NOT_LEGAL, monitorDataRequest.checkLegality());
        }

        // preparation
        GlobalInfo queryInfo = globalBiz.getGlobalInfoByType(GlobalType.MONITOR_QUERY);
        if (queryInfo == null) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_QUERY_ERROR, "query is null");
        }
        String queryUrl = "http://" + queryInfo.getValue() + "/graph/history";

        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setTargetType(monitorDataRequest.getTargetType());
        monitorResult.setDataSpec(monitorDataRequest.getDataSpec());

        // fetch data from query api
        // create graphHistoryRequest
        GraphHistoryRequest graphHistoryRequest = getGraphHistoryRequestForOverview(monitorDataRequest, isDisk);
        List<EndpointCounter> endpointCounterList = graphHistoryRequest.getEndpoint_counters();
        //Concurrent requests, sending 500 counters at a time
        int max_size = 500;
        int times = endpointCounterList.size() % max_size == 0 ? endpointCounterList.size() / max_size : endpointCounterList.size() / max_size + 1;
        List<postJsonTask> tasks = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            GraphHistoryRequest tmp = new GraphHistoryRequest();
            tmp.setStart(graphHistoryRequest.getStart());
            tmp.setEnd(graphHistoryRequest.getEnd());
            tmp.setCf(graphHistoryRequest.getCf());
            tmp.setEndpoint_counters(endpointCounterList.subList(i * max_size, Math.min((i + 1) * max_size, endpointCounterList.size())));
            tasks.add(new postJsonTask(queryUrl, tmp));
        }
        List<List<GraphHistoryResponse>> taskResult = ClientConfigure.executeCompletionService(tasks);
        List<GraphHistoryResponse> graphHistoryResponses = new ArrayList<>();
        for (List<GraphHistoryResponse> result : taskResult) {
            if (result != null && result.size() > 0) {
                graphHistoryResponses.addAll(result);
            }
        }

        // re-arrage GraphHistoryResponses
        Map<String, List<GraphHistoryResponse>> graphHistoryResponseMap = arrangeGraphHistoryResponseList(graphHistoryResponses,
                monitorDataRequest.getTargetType());

        // create MonitorResult
        createMonitorResult(monitorResult, graphHistoryResponseMap, monitorDataRequest);

        return monitorResult;
    }

 // create monitorResult by arranged GraphHistoryResponse-Map
    private void createMonitorResult(MonitorResult monitorResult, Map<String, List<GraphHistoryResponse>> graphHistoryResponseMap,
                                     MonitorDataRequest monitorDataRequest) {

        Map<String, String> containerPodMap = getContainerPodMap(monitorDataRequest);

        for (String counter : graphHistoryResponseMap.keySet()) {

            if (graphHistoryResponseMap.get(counter) == null || graphHistoryResponseMap.get(counter).size() == 0) {
                continue;
            }

            // create key : counter
            monitorResult.getCounterResults().put(counter, new ArrayList<Map<String, Double>>());

            // get timeStampCount for this counter
            int timeStampCount = 0;
            int maxCountIndex = 0;
            for (GraphHistoryResponse graphHistoryResponse : graphHistoryResponseMap.get(counter)) {
                if (graphHistoryResponse.getValues().size() > timeStampCount) {
                    timeStampCount = graphHistoryResponse.getValues().size();
                    maxCountIndex = graphHistoryResponseMap.get(counter).indexOf(graphHistoryResponse);
                }
            }

            // set interval
            if (graphHistoryResponseMap.get(counter).get(maxCountIndex).getValues().size() == 1) {
                monitorResult.setInterval(graphHistoryResponseMap.get(counter).get(maxCountIndex).getStep());
            } else {
                monitorResult.setInterval((int) (graphHistoryResponseMap.get(counter).get(maxCountIndex).getValues().get(1).getTimestamp()
                        - graphHistoryResponseMap.get(counter).get(maxCountIndex).getValues().get(0).getTimestamp()));
            }

            // cache targetValueKeys and index offsets
            List<String> targetValueKeys = new ArrayList<>();
            List<Integer> offsets = new ArrayList<>();
            for (GraphHistoryResponse graphHistoryResponse : graphHistoryResponseMap.get(counter)) {
                offsets.add(0);
                switch (monitorDataRequest.getTargetType()) {
                    case "node":
                        targetValueKeys.add(graphHistoryResponse.getEndpoint());
                        break;
                    case "pod":
                        targetValueKeys.add(containerPodMap.get(graphHistoryResponse.getCounter()
                                .substring(graphHistoryResponse.getCounter().indexOf("/id=") + 4)));
                        break;
                    case "container":
                        targetValueKeys.add(graphHistoryResponse.getCounter().substring(graphHistoryResponse.getCounter().indexOf("/id=") + 4));
                }
            }

            // for every timeStamp: create Map<String,Double>
            for (int index = 0; index < timeStampCount; index++) {

                Map<String, Double> targetValueMap = new HashMap<>();

                // add key "timeStamp"
                long currentTimeStamp = graphHistoryResponseMap.get(counter).get(maxCountIndex).getValues().get(index).getTimestamp();
                targetValueMap.put("timeStamp", (double) currentTimeStamp * 1000.0);

                for (GraphHistoryResponse graphHistoryResponse : graphHistoryResponseMap.get(counter)) {

                    int indexGraphHistoryResponse = graphHistoryResponseMap.get(counter).indexOf(graphHistoryResponse);
                    int indexActual = index - offsets.get(indexGraphHistoryResponse);
                    Double insertValue = null;

                    if (graphHistoryResponse.getValues().size() > indexActual) {
                        long actualTimeStamp = graphHistoryResponse.getValues().get(indexActual).getTimestamp();
                        if (actualTimeStamp > currentTimeStamp) {
                            offsets.set(indexGraphHistoryResponse, offsets.get(indexGraphHistoryResponse) + 1);
                        } else {
                            switch (monitorDataRequest.getTargetType()) {
                                case "pod":
                                    String pod = targetValueKeys.get(indexGraphHistoryResponse);
                                    if (!targetValueMap.containsKey(pod)) {
                                        insertValue = graphHistoryResponse.getValues().get(indexActual).getValue();
                                    } else if (graphHistoryResponse.getValues().get(indexActual).getValue() != null) {
                                        Double currentValue = targetValueMap.get(pod);
                                        if (currentValue == null) {
                                            insertValue = graphHistoryResponse.getValues().get(indexActual).getValue();
                                        } else {
                                            insertValue = currentValue + graphHistoryResponse.getValues().get(indexActual).getValue();
                                        }
                                    } else {
                                        insertValue = targetValueMap.get(pod);
                                    }
                                    break;
                                default:
                                    insertValue = graphHistoryResponse.getValues().get(indexActual).getValue();
                            }
                        }
                    }
                    String targetValueKey = targetValueKeys.get(indexGraphHistoryResponse);
                    targetValueMap.put(targetValueKey, insertValue);
                }

                monitorResult.getCounterResults().get(counter).add(targetValueMap);
            }
        }

        // change pod memory usage percent
        if (monitorDataRequest.getTargetType().equals("pod")) {
            int length = monitorResult.getCounterResults().get("container.mem.usage.percent").size();
            if (monitorResult.getCounterResults().get("container.mem.usage").size() < length) {
                length = monitorResult.getCounterResults().get("container.mem.usage").size();
            }
            if (monitorResult.getCounterResults().get("container.mem.limit").size() < length) {
                length = monitorResult.getCounterResults().get("container.mem.limit").size();
            }
            for (int index = 0; index < length; index++) {
                Map<String, Double> currentMap = monitorResult.getCounterResults().get("container.mem.usage.percent").get(index);
                for (String key : currentMap.keySet()) {
                    if (key.equals("timeStamp")) {
                        continue;
                    }
                    if (monitorResult.getCounterResults().get("container.mem.usage").get(index).containsKey(key) &&
                            monitorResult.getCounterResults().get("container.mem.usage").get(index).get(key) != null &&
                            monitorResult.getCounterResults().get("container.mem.limit").get(index).containsKey(key) &&
                            monitorResult.getCounterResults().get("container.mem.limit").get(index).get(key) != null) {
                        monitorResult.getCounterResults().get("container.mem.usage.percent").get(index).put(key,
                                monitorResult.getCounterResults().get("container.mem.usage").get(index).get(key)
                                        / monitorResult.getCounterResults().get("container.mem.limit").get(index).get(key) * 100
                        );
                    } else {
                        monitorResult.getCounterResults().get("container.mem.usage.percent").get(index).put(key, null);
                    }
                }
            }
        }
    }
    
 // create containerId-pod mapping
    private Map<String, String> getContainerPodMap(MonitorDataRequest monitorDataRequest) {

        Map<String, String> containerPodMap = new HashMap<>();
        if (!monitorDataRequest.getTargetType().equals("pod")) {
            return containerPodMap;
        }

        for (TargetInfo targetInfo : monitorDataRequest.getTargetInfos()) {
            for (ContainerInfo containerInfo : targetInfo.getPod().getContainers()) {
                containerPodMap.put(containerInfo.getContainerId(), targetInfo.getPod().getPodName());
            }
        }
        return containerPodMap;
    }

}