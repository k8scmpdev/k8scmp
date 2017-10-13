package org.k8scmp.monitormgmt.dao;


import java.util.List;
import java.util.Set;

import org.k8scmp.monitormgmt.domain.monitor.MonitorTarget;

/**
 * Created by jason on 2017/10/13.
 */
public interface MonitorBiz {

    List<String> getNodeCountersByEndpoints(Set<String> endpoints);

    List<String> getNodeCountersByEndpoints(String endpoints);

    List<String> getContainerCountersByEndpoints(String endpoints, String containers);

}
