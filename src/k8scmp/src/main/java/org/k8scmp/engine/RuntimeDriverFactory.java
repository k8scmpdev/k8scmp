package org.k8scmp.engine;

import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.common.SpringContextManager;


/**
 */
public class RuntimeDriverFactory {

    /**
     * create a runtime driver with init called
     * @param clazz
     * @param cluster
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends RuntimeDriver> T getRuntimeDriver(Class<T> clazz, Cluster cluster) {
        return (T) SpringContextManager.getBean(clazz).init(cluster);
    }
}
