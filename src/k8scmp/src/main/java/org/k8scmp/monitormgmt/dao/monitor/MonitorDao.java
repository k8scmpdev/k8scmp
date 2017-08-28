package org.k8scmp.monitormgmt.dao.monitor;


import java.util.List;
import java.util.Set;

import org.k8scmp.monitormgmt.domain.monitor.MonitorTarget;

/**
 * Created by baokangwang on 2016/4/6.
 */
public interface MonitorDao {

    List<String> getNodeCountersByEndpoints(Set<String> endpoints);

    List<String> getNodeCountersByEndpoints(String endpoints);

    List<String> getContainerCountersByEndpoints(String endpoints, String containers);

    int addMonitorTarget(MonitorTarget monitorTarget);

    int updateMonitorTargetById(MonitorTarget monitorTarget);

    String getMonitorTargetById(long id);
}
