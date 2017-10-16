package org.k8scmp.overview.service;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.globalmgmt.domain.ClusterInfo;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.globalmgmt.domain.MonitorInfo;
import org.k8scmp.globalmgmt.domain.RegisterInfo;
import org.k8scmp.overview.domain.OverviewCountInfo;
import org.k8scmp.overview.domain.ResourceOverview;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jason on 17-8-24.
 */
public interface OverviewService {

	Map<String, Integer> getAppInfo();

	Map<String, Integer> getServiceInfo();

	ResourceOverview getResourceOverview();

	Map<String, Double> getMemoryInfo();

	Map<String, Integer> getCPUInfo();

	Map<String, Integer> getNodeInfo();

	Map<String, Double> getDiskInfo();

}
