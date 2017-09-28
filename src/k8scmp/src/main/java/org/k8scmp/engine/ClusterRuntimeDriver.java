package org.k8scmp.engine;

import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.engine.k8s.K8sDriver;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
@Component
@DependsOn("springContextManager")
public class ClusterRuntimeDriver {
    private static ConcurrentHashMap<String, RuntimeDriver> clusterDriverMap = new ConcurrentHashMap<>();

    static GlobalBiz globalBiz;

    @Autowired
    public void setClusterBiz(GlobalBiz globalBiz) {
        ClusterRuntimeDriver.globalBiz = globalBiz;
    }

    @PostConstruct
    public static void init() {
        // TODO: add init, read cluster from database here
//        List<Cluster> clusters = clusterBiz.listClusters();
//
//        if (clusters != null) {
//            for (Cluster cluster : clusters) {
//                if (cluster.getVer() == 1) {
//                    clusterDriverMap.putIfAbsent(cluster.getId(), RuntimeDriverFactory.getRuntimeDriver(K8sDriver.class, cluster));
//                }
//            }
//        }
    	GlobalInfo global = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
    	Cluster cluster = new Cluster();
    	cluster.setApi(global.getValue());
    	cluster.setId(global.getId()+"");
    	clusterDriverMap.putIfAbsent(cluster.getId(), RuntimeDriverFactory.getRuntimeDriver(K8sDriver.class, cluster));
    }

    public static RuntimeDriver getClusterDriver(String clusterId) {
        RuntimeDriver driver = clusterDriverMap.get(clusterId);
        //校验集群是否已删除
//        Cluster cluster = clusterBiz.getClusterById(clusterId);
//        if (cluster == null) {
//            clusterDriverMap.remove(clusterId);
//            return null;
//        }
//        if (driver == null) {
//            driver = clusterDriverMap.putIfAbsent(clusterId, RuntimeDriverFactory.getRuntimeDriver(K8sDriver.class, cluster));
//        } else {
//            if (!driver.isDriverLatest(cluster)) {
//                driver = RuntimeDriverFactory.getRuntimeDriver(K8sDriver.class, cluster);
//                clusterDriverMap.put(clusterId, driver);
//            }
//        }
        return driver;
    }
}
