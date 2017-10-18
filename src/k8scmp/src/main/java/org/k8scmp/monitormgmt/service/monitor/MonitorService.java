package org.k8scmp.monitormgmt.service.monitor;

import java.io.IOException;
import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.monitormgmt.domain.monitor.InstenceInfoBack;
import org.k8scmp.monitormgmt.domain.monitor.MonitorResult;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfoBack;
import org.k8scmp.monitormgmt.domain.monitor.TargetRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryResponse;

public interface MonitorService {

	List<NodeInfoBack> getNodeMonitorData(String hostName);

	List<InstenceInfoBack> getInstenceMonitorData(String serviceName);

	HttpResponseTemp<?> getMonitorData(String type, long startTime, long endTime, String dataSpec);

	MonitorResult getMonitorDataForOverview(TargetRequest targetRequest, long startTime, long endTime, String dataSpec,
			boolean isDisk);

	List<GraphHistoryResponse> postJson(String requestUrl, GraphHistoryRequest graphHistoryRequest) throws IOException;

	MonitorResult getMonitorDataForOverview(TargetRequest targetRequest, long startTime, long endTime, String dataSpec);

}
