package org.k8scmp.monitormgmt.service;

import java.io.IOException;
import java.util.List;

import org.k8scmp.monitormgmt.domain.monitor.MonitorResult;
import org.k8scmp.monitormgmt.domain.monitor.TargetRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryResponse;

/**
 * Created by jason on 2017/10/13.
 */
public interface MonitorService {

    
    /**
     * Get monitor data for overview
     * @param targetRequest
     * @param startTime
     * @param endTime
     * @param dataSpec
     * @param isDisk
     * @return
     */
    MonitorResult getMonitorDataForOverview(TargetRequest targetRequest, long startTime, long endTime, String dataSpec, boolean isDisk);

	List<GraphHistoryResponse> postJson(String requestUrl, GraphHistoryRequest graphHistoryRequest) throws IOException;

    
}