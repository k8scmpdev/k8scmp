package org.k8scmp.engine.k8s.updater;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.dao.ServiceEventDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.common.SpringContextManager;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.model.DeployOperation;
import org.k8scmp.model.ServiceStatus;
import org.k8scmp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
@Component
public class EventUpdater {

    @Autowired
    ServiceDao serviceDao;
    
    @Autowired
    AppDao appDao;

    @Autowired
    GlobalBiz globalBiz;

    @Autowired
    private ServiceEventDao serviceEventDao;


    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    private static AtomicBoolean started = new AtomicBoolean(false);

    private static Logger logger = LoggerFactory.getLogger(EventUpdater.class);

    @PostConstruct
    public void init() {
        if (started.compareAndSet(false, true)) {
            logger.info("init {}, start scheduled task checker.", EventUpdater.class.toString());
            scheduledExecutor.scheduleWithFixedDelay(new UpdateTask(), 10, 35, TimeUnit.SECONDS);
            //scheduledExecutor.scheduleAtFixedRate(new clearLogTask(), 1, 10, TimeUnit.MINUTES);
        }
    }

    private class UpdateTask implements Runnable {

        @Override
        public void run() {
            try {
                checkUpdateTask();
            } catch (Exception e) {
                logger.error("failed when check event update task", e);
            }
        }

    }

    private void checkUpdateTask() {
//        List<Cluster> clusters = clusterBiz.listClusters();
//
//        for (Cluster cluster : clusters) {
//            checkDeployStatus(cluster);
//        }
    	Cluster cluster = getCluster();
    	checkDeployStatus(cluster);
    	updateAppStatus(cluster);
    }

    public void updateAppStatus(Cluster cluster) {
		List<String> appIds = appDao.getAppIdListByClusterId(cluster.getId());
		if (appIds == null || appIds.size() == 0) {
            return;
        }
		AppStatusMgmt appStatusMgmt = SpringContextManager.getBean(AppStatusMgmt.class);
		for(String appId:appIds){
			try {
				appStatusMgmt.init(appId).updateAppState();
			} catch (Exception e) {
				logger.error("updateAppState error, appId=" + appId);
			}
		}
	}

	private Cluster getCluster(){
		GlobalInfo cluster_host = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
		GlobalInfo cluster_name = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
    	Cluster cluster = new Cluster();
    	cluster.setApi(cluster_host.getValue());
    	cluster.setId(cluster_host.getId()+"");
    	cluster.setName(cluster_name.getValue());
    	
		return cluster;
	}

    private void checkDeployStatus(Cluster cluster) {
        List<ServiceInfo> serviceInfos = serviceDao.getServicesByClusterId(cluster.getId());

        if (serviceInfos == null || serviceInfos.size() == 0) {
            return;
        }

        for (ServiceInfo serviceInfo : serviceInfos) {
            DeployEvent event = null;
            event = serviceEventDao.getNewestEvent(serviceInfo.getId());
            if (event == null) {
                continue;
            }
            
            if (!serviceInfo.isTerminated() && event.eventTerminated()) {
                switch (event.getState()) {
                    case FAILED:
                        serviceDao.updateServiceStatu(serviceInfo.getId(), ServiceStatus.ERROR.name());
                        logger.info("set service statu to error with id " + serviceInfo.getId());
                        break;
                    case ABORTED:
                        if (DeployOperation.ABORT_UPDATE.equals(event.getOperation())) {
                            serviceDao.updateServiceStatu(serviceInfo.getId(), ServiceStatus.UPDATE_ABORTED.name());
                            logger.info("set service statu to update_aborted with id " + serviceInfo.getId());
                        } else if (DeployOperation.ABORT_ROLLBACK.equals(event.getOperation())) {
                            serviceDao.updateServiceStatu(serviceInfo.getId(), ServiceStatus.BACKROLL_ABORTED.name());
                            logger.info("set service statu to rollback_aborted with id " + serviceInfo.getId());
                        } else if (DeployOperation.ABORT_START.equals(event.getOperation())) {
                            serviceDao.updateServiceStatu(serviceInfo.getId(), ServiceStatus.STOP.name());
                            logger.info("set service statu to stop with id " + serviceInfo.getId());
                        } else {
                            serviceDao.updateServiceStatu(serviceInfo.getId(), ServiceStatus.RUNNING.name());
                            logger.info("set service statu to running with id " + serviceInfo.getId());
                        }
                        break;
                    case SUCCESS:
                        if (DeployOperation.STOP.equals(event.getOperation())) {
                            serviceDao.updateServiceStatu(serviceInfo.getId(), ServiceStatus.STOP.name());
                            logger.info("set service statu to stop with id " + serviceInfo.getId());
                        } else {
                            serviceDao.updateServiceStatu(serviceInfo.getId(), ServiceStatus.RUNNING.name());
                            logger.info("set service statu to running with id " + serviceInfo.getId());
                        }
                        break;
                    default:
                        break;
                }
            } else {
                try {
                	AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
                	
                	ServiceConfigInfo serviceConfigInfo = null;
					try {
						serviceConfigInfo = serviceInfo.toModel(ServiceConfigInfo.class);
					} catch (Exception e) {
						logger.error("get serviceConfigInfo error, serviceId=" + serviceInfo.getId());
					}
                	
                    EventChecker eventChecker = new EventChecker(appInfo, serviceConfigInfo, event);
                    eventChecker.checkEvent();
                    try {
						if (!event.eventTerminated() && DateUtil.string2timestamp(event.getExpireTime()) < System.currentTimeMillis()) {
						    event.setMessage("service expired");
						    eventChecker.checkExpireEvent();
						}
					} catch (ParseException e) {
						logger.error("transform expireTime into Long Type error, expireTime=" + event.getExpireTime());
					}
                } catch (DeploymentEventException e) {
                    logger.warn("catch io exception when create event checker, message={}", e.getMessage());
                }
            }
        }
    }

//    private class clearLogTask implements Runnable {
//
//        @Override
//        public void run() {
//            List<Cluster> clusters = clusterBiz.listClusters();
//            for (Cluster cluster : clusters) {
//                List<ServiceConfigInfo> serviceConfigInfos = serviceDao.listDeploymentByClusterId(cluster.getId());
//                for (ServiceConfigInfo serviceConfigInfo : serviceConfigInfos) {
//                    try {
//                        long deleted = k8SEventBiz.deleteOldDeployEvents(cluster.getId(), serviceConfigInfo.getId());
//                        if (deleted > 0) {
//                            logger.info("deleted {} events for cluster {}, deploy {}", deleted, cluster.getName(),
//                                    serviceConfigInfo.getName());
//                        }
//                    } catch (RuntimeException e) {
//                        logger.warn("error happened when delete event for deploy " + serviceConfigInfo.getServiceCode() +
//                                " in cluster " + cluster.getName() + ", Message:" + e.getMessage(), e);
//                    }
//                }
//                try {
//                    long deleted = k8SEventBiz.deleteOldDeployEvents(cluster.getId(), -1, 2000);
//                    logger.info("deleted {} events for cluster {}, deploy none", deleted, cluster.getName());
//                } catch (RuntimeException e) {
//                    logger.warn("error happened when delete event for deploy none in cluster " + cluster.getName() +
//                            ", Message:" + e.getMessage(), e);
//                }
//            }
//        }
//    }
}
