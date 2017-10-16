package org.k8scmp.monitormgmt.dao.monitor;


import java.util.List;
import java.util.Set;

/**
 * Created by baokangwang on 2016/4/6.
 */
public interface MonitorDao {

    List<String> getNodeCountersByEndpoints(Set<String> endpoints);

    List<String> getNodeCountersByEndpoints(String endpoints);

    List<String> getContainerCountersByEndpoints(String endpoints, String containers);

	String getLogicClusterById();

	int getEndpointId(String endpoint);

	List<String> getNodeCounterByEndpointId(int id);

}
