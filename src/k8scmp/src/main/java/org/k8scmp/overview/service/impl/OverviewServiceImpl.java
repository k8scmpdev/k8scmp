package org.k8scmp.overview.service.impl;


import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.k8scmp.globalmgmt.service.impl.NodeWrapper;
import org.k8scmp.login.domain.User;
import org.k8scmp.monitormgmt.domain.monitor.MonitorResult;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;
import org.k8scmp.monitormgmt.domain.monitor.TargetInfo;
import org.k8scmp.monitormgmt.domain.monitor.TargetRequest;
import org.k8scmp.monitormgmt.service.MonitorService;
import org.k8scmp.overview.dao.OverviewBiz;
import org.k8scmp.overview.domain.OverviewCountInfo;
import org.k8scmp.overview.domain.ResourceOverview;
import org.k8scmp.overview.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jason on 17-9-8.
 */
@Service
public class OverviewServiceImpl implements OverviewService {

    protected static Logger logger = LoggerFactory.getLogger(OverviewServiceImpl.class);
    @Autowired
    OverviewBiz overviewBiz;
    @Autowired
    MonitorService monitorService;

	@Override
	public Map<String, Integer> getAppInfo() {
		List<OverviewCountInfo> overviewCountInfo = overviewBiz.getAppInfo();
//        if (globalInfo == null) {
//            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
//        }
        return parseCountInfo(overviewCountInfo);
	}
	
	@Override
	public Map<String, Integer> getServiceInfo() {
		List<OverviewCountInfo> overviewCountInfo = overviewBiz.getServiceInfo();
//        if (globalInfo == null) {
//            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
//        }
        return parseCountInfo(overviewCountInfo);
	}
	
	public Map<String, Integer> parseCountInfo(List<OverviewCountInfo> countInfoList){
		Map<String, Integer> data = new HashMap<>();
     	int count_r = 0;
     	int count_s = 0;
     	int count_o = 0;
     	for(int i=0;i<countInfoList.size();i++){
     		OverviewCountInfo count_info = countInfoList.get(i);
     		if("running".equals(count_info.getCountName())) count_r=count_info.getCount();
     		if("stop".equals(count_info.getCountName())) count_s=count_info.getCount();
     		if("operating".equals(count_info.getCountName())) count_o=count_info.getCount();
     	}
     	data.put("运行中: "+count_r,   count_r);
     	data.put("已停止: "+count_s,   count_s);
     	data.put("操作中: "+count_o,   count_o);
		return data;
	}
	
	@Override
	public Map<String, Double> getMemoryInfo() {
		//获取cpu、memory、node概览数据
     	ResourceOverview resourceOverviewInfo = getResourceOverview();
     	Map<String, Double> data = new HashMap<>();
     	data.put("已使用："+parseDouble2Str(resourceOverviewInfo.getMemoryUsed()/1024/1024/1024)+"G", resourceOverviewInfo.getMemoryUsed());
     	data.put("总可用："+parseDouble2Str(resourceOverviewInfo.getMemoryTotal()/1024/1024/1024)+"G", resourceOverviewInfo.getMemoryTotal());
     	return data;
	}
	
	@Override
	public Map<String, Integer> getCPUInfo() {
		//获取cpu、memory、node概览数据
     	ResourceOverview resourceOverviewInfo = getResourceOverview();
     	Map<String, Integer> data = new HashMap<>();
     	data.put("0-25%："+resourceOverviewInfo.getCpu0To25()+"台", resourceOverviewInfo.getCpu0To25());
     	data.put("25-50%："+resourceOverviewInfo.getCpu25To50()+"台", resourceOverviewInfo.getCpu25To50());
     	data.put("50-75%："+resourceOverviewInfo.getCpu50To75()+"台", resourceOverviewInfo.getCpu50To75());
     	data.put("75-100%："+resourceOverviewInfo.getCpu75To100()+"台", resourceOverviewInfo.getCpu75To100());
     	return data;
	}
	
	@Override
	public Map<String, Integer> getNodeInfo() {
		//获取cpu、memory、node概览数据
     	ResourceOverview resourceOverviewInfo = getResourceOverview();
     	Map<String, Integer> data = new HashMap<>();
     	data.put("在线："+resourceOverviewInfo.getNodeOnline()+"台", resourceOverviewInfo.getNodeOnline());
     	data.put("离线："+resourceOverviewInfo.getNodeOffline()+"台", resourceOverviewInfo.getNodeOffline());
     	return data;
	}
	
	public String parseDouble2Str(Double num){
		DecimalFormat df = new DecimalFormat("######0.00");
		String result = df.format(num);
		return result;
	}
	
	@Override
    public ResourceOverview getResourceOverview() {
        ResourceOverview result = new ResourceOverview();
        try {
            User user = AuthUtil.getUser();
            //insert all clusters' nodes' information to monitor_targets table
            List<NodeInfo> nodeList = new ArrayList<>();
//            List<CollectionAuthorityMap> clusterAuthorityMapList = AuthUtil.getCollectionList(user.getId(), ResourceType.CLUSTER);
//            if (!clusterAuthorityMapList.isEmpty()) {
//                int tmpClusterId = clusterAuthorityMapList.get(0).getCollectionId();
                List<TargetInfo> targetInfos = new ArrayList<>();
//                for (CollectionAuthorityMap authorityMap : clusterAuthorityMapList) {
                    try {
                        NodeWrapper nodeWrapper = new NodeWrapper().init("default");
                        List<NodeInfo> nodeInfoInCluster = nodeWrapper.getNodeInfoListWithoutPods();
                        nodeList.addAll(nodeInfoInCluster);
                        for (NodeInfo nodeInfo : nodeInfoInCluster) {
                            targetInfos.add(new TargetInfo(nodeInfo.getName(), null, null));
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
//                }
                TargetRequest targetRequest = new TargetRequest(2, "node", targetInfos);
//                monitorService.insertTargets(targetRequest);

                //use the existing function to get monitor data
                Calendar current = Calendar.getInstance();
                long endTime = current.getTimeInMillis();
                current.set(Calendar.MINUTE, current.get(Calendar.MINUTE) - 1);
                long startTime = current.getTimeInMillis();
                MonitorResult monitorResult = monitorService.getMonitorDataForOverview(targetRequest, startTime, endTime, "AVERAGE", false);
                if (null != monitorResult) {
                    Map<String, List<Map<String, Double>>> data = monitorResult.getCounterResults();
                    result.setMemoryTotal(getAverageSum(data, "mem.memtotal", null));
                    result.setMemoryUsed(getAverageSum(data, "mem.memused", null));
                    getAverageCpu(data, result);
                }
//            }
            int onlineNode = 0;
            for (NodeInfo node : nodeList) {
                if (node.getStatus().equals("Ready")) {
                    onlineNode++;
                }
            }
            result.setNode(nodeList.size());
            result.setNodeOnline(onlineNode);
            result.setNodeOffline(nodeList.size() - onlineNode);
        } catch (Exception e) {
            logger.warn("get the overview of resources error, message is:" + e.getMessage());
        }
        return result;
    }
	
	private Double getAverageSum(Map<String, List<Map<String, Double>>> data, String keyPrefix, String pathIgnore) {
	    Double result = 0.0;
	    if (data != null && StringUtils.isNotBlank(keyPrefix)) {
	        for (String key : data.keySet()) {
	            if (StringUtils.isNotBlank(pathIgnore) && key.contains(pathIgnore)) {
	                continue;
	            }
	            if (key.startsWith(keyPrefix)) {
	                Map<String, List<Double>> all = new HashMap<>();
	                for (Map<String, Double> total : data.get(key)) {
	                    for (String node : total.keySet()) {
	                        if (!node.equals("timeStamp") && total.get(node) != null) {
	                            if (all.containsKey(node)) {
	                                all.get(node).add(total.get(node));
	                            } else {
	                                List<Double> tmp = new ArrayList<>(1);
	                                tmp.add(total.get(node));
	                                all.put(node, tmp);
	                            }
	                        }
	                    }
	                }
	                for (String node : all.keySet()) {
	                    List<Double> detail = all.get(node);
	                    if (detail.size() != 0) {
	                        Double sum = 0.0;
	                        for (Double d : detail) {
	                            sum += d;
	                        }
	                        result += sum / detail.size();
	                    }
	                }
	            }
	        }
	    }
	    return result;
	}
	
	private void getAverageCpu(Map<String, List<Map<String, Double>>> data, ResourceOverview result) {
        if (data.containsKey("cpu.busy")) {
            Map<String, Double> total = new HashMap<>();
            for (Map<String, Double> cpu : data.get("cpu.busy")) {
                for (String key : cpu.keySet()) {
                    if (!key.equals("timeStamp") && cpu.get(key) != null) {
                        if (total.containsKey(key)) {
                            total.put(key, total.get(key) + cpu.get(key));
                        } else {
                            total.put(key, cpu.get(key));
                        }
                    }
                }
            }
            int cpu0To25 = 0;
            int cpu25To50 = 0;
            int cpu50To75 = 0;
            int cpu75To100 = 0;
            if (data.get("cpu.busy").size() != 0) {
                for (String key : total.keySet()) {
                    if (total.get(key) / data.get("cpu.busy").size() >= 0 && total.get(key) / data.get("cpu.busy").size() < 25) {
                        cpu0To25++;
                    } else if (total.get(key) / data.get("cpu.busy").size() < 50) {
                        cpu25To50++;
                    } else if (total.get(key) / data.get("cpu.busy").size() < 75) {
                        cpu50To75++;
                    } else if (total.get(key) / data.get("cpu.busy").size() <= 100) {
                        cpu75To100++;
                    }
                }
            }
            result.setCpu0To25(cpu0To25);
            result.setCpu25To50(cpu25To50);
            result.setCpu50To75(cpu50To75);
            result.setCpu75To100(cpu75To100);
        }
    }
    

}
