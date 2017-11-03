package org.k8scmp.engine.k8s.updater;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.engine.ClusterRuntimeDriver;
import org.k8scmp.engine.RuntimeDriver;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.exception.DeploymentTerminatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 */
public class EventChecker {
    private static Logger logger = LoggerFactory.getLogger(EventChecker.class);
    
    private AppInfo appInfo;

    private DeployEvent event;

    private ServiceConfigInfo serviceConfigInfo;

    public EventChecker() {
    }

    public EventChecker(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, DeployEvent event) throws DeploymentEventException {
        if (serviceConfigInfo == null || event == null) {
            throw new DeploymentEventException("service or event is null for event checker!!!");
        }
        this.appInfo = appInfo;
        this.serviceConfigInfo = serviceConfigInfo;
        this.event = event;
    }

    public void checkEvent() {
        try {
            RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(appInfo.getClusterId());
            if (driver == null) {
                return;
            }
            switch (event.getOperation()) {
                case START:
                case SCALE_DOWN:
                case SCALE_UP:
                case UPDATE:
                case ROLLBACK:
                    driver.checkBasicEvent(appInfo, serviceConfigInfo, event);
                    break;
                case STOP:
                    driver.checkStopEvent(appInfo, serviceConfigInfo, event);
                    break;
                case ABORT_START:
                case ABORT_UPDATE:
                case ABORT_ROLLBACK:
                case ABORT_SCALE_UP:
                case ABORT_SCALE_DOWN:
//                    driver.checkAbortEvent(appInfo,serviceConfigInfo, event);
                    break;
            }
        } catch (IOException | DeploymentEventException e) {
            logger.error("Check service event status error: " + e.getMessage());
        } catch (DeploymentTerminatedException e) {
            logger.debug("catch event terminal error, message = " + e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void checkExpireEvent() {
        try {
            RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(appInfo.getClusterId());
            if (driver == null) {
                return;
            }
            driver.expiredEvent(appInfo,serviceConfigInfo, event);
        } catch (IOException | DeploymentEventException e) {
            logger.error("change expired event status failed, eid="
                    + event.getId() + ", serviceId=" + event.getServiceId()
                    + ", error message=" + e.getMessage());
        } catch (Exception e) {
            logger.error("get unhandled excption:" + e.getMessage());
        }
    }
}
