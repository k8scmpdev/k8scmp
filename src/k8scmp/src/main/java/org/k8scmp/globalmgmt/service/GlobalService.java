package org.k8scmp.globalmgmt.service;

import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.globalmgmt.domain.ClusterInfo;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.globalmgmt.domain.MonitorInfo;
import org.k8scmp.globalmgmt.domain.RegisterInfo;

import io.fabric8.kubernetes.api.model.NamespaceList;

import java.util.List;
import java.util.Set;

/**
 * Created by jason on 17-8-24.
 */
public interface GlobalService {

	GlobalInfo getGlobalInfoByType(GlobalType globalType);

	HttpResponseTemp<?> editClusterInfo(ClusterInfo clusterinfo, boolean flag);

	HttpResponseTemp<?> editClusterInfo(ClusterInfo clusterinfo);

	HttpResponseTemp<?> editRegistryInfo(RegisterInfo registryinfo);

	HttpResponseTemp<?> editRegistryInfo(RegisterInfo registryinfo, boolean flag);

	HttpResponseTemp<?> editMonitorInfo(MonitorInfo monitorInfo);

	HttpResponseTemp<?> editMonitorInfo(MonitorInfo monitorInfo, boolean flag);

	List<NamespaceList> getAllNamesapce();

	List<String> getAllNamesapceName();

	List<Cluster> getAllCluster();

	List<String> getAllNamesapceNameByCluster(Cluster cluster);

}
