package org.k8scmp.monitormgmt.service.monitor;


import java.io.IOException;
import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.monitormgmt.domain.monitor.console.MonitorResult;
import org.k8scmp.monitormgmt.domain.monitor.console.TargetRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryRequest;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryResponse;

/**
 * Created by baokangwang on 2016/3/1.
 */
public interface MonitorService {

    /**
     *
     * @param targetRequest defines targetType(node/pod/container) and targetInfos
     * @return targetId saved in database
     */
    HttpResponseTemp<?> insertTargets(TargetRequest targetRequest);

    /**
     *
     * @param targetId saved in database
     * @param cid cluster id
     * @return targetRequest corresponding to specified targetId
     */
    HttpResponseTemp<?> fetchTargets(long targetId, int cid);

    /**
     *
     * @param targetId defines targetType(node/pod/container) and targetInfos
     * @param cid cluster id
     * @return CounterItems (cpuCounters/memCounters/diskCounters/networkCounters)
     */
    HttpResponseTemp<?> retrieveCounters(long targetId, int cid);

    /**
     *
     * @param targetId
     * @param startTime
     * @param endTime
     * @param dataSpec
     * @param cid
     * @return
     */
    HttpResponseTemp<?> getMonitorData(long targetId, long startTime, long endTime, String dataSpec, int cid);

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

    /**
     *
     * @param requestUrl
     * @param graphHistoryRequest
     * @return graphHistoryResponses
     * @throws IOException
     */
    List<GraphHistoryResponse> postJson(String requestUrl, GraphHistoryRequest graphHistoryRequest)  throws IOException;
}