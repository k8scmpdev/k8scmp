package org.k8scmp.appmgmt.service.impl;

import org.k8scmp.appmgmt.service.ServiceStatusManager;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.exception.ApiException;
import org.k8scmp.model.ServiceStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 */
@Service
public class ServiceStatusManagerImpl implements ServiceStatusManager {

   

    @Override
    public void checkStateAvailable(ServiceStatus curState, ServiceStatus dstState) {
        Set<ServiceStatus> availables = new HashSet<>();
        switch (curState) {
            case DEPLOYING:
                availables.add(ServiceStatus.RUNNING);
                availables.add(ServiceStatus.ABORTING);
                availables.add(ServiceStatus.ERROR);
                break;
            case STOP:
                availables.add(ServiceStatus.DEPLOYING);
                break;
            case UPSCALING:
            case DOWNSCALING:
            case BACKROLLING:
            case UPDATING:
                availables.add(ServiceStatus.ABORTING);
                availables.add(ServiceStatus.ERROR);
                availables.add(ServiceStatus.RUNNING);
                break;
            case ERROR:
            case UPDATE_ABORTED:
            case BACKROLL_ABORTED:
                availables.add(ServiceStatus.BACKROLLING);
                availables.add(ServiceStatus.UPDATING); // update or rollback depends on request version
                availables.add(ServiceStatus.STOPPING);
                break;
            case STOPPING:
                availables.add(ServiceStatus.STOP);
                availables.add(ServiceStatus.ERROR);
                break;
            case RUNNING:
                availables.add(ServiceStatus.STOPPING);
                availables.add(ServiceStatus.UPDATING);
                availables.add(ServiceStatus.BACKROLLING);
                availables.add(ServiceStatus.UPSCALING);
                availables.add(ServiceStatus.DOWNSCALING);
                break;
            case ABORTING:
                availables.add(ServiceStatus.ERROR);
                availables.add(ServiceStatus.STOP);
                availables.add(ServiceStatus.RUNNING);
                availables.add(ServiceStatus.UPDATE_ABORTED);
                availables.add(ServiceStatus.BACKROLL_ABORTED);
                break;
        }
        if (!availables.contains(dstState)) {
            throw ApiException.wrapMessage(ResultStat.SERVICE_STATUS_NOT_ALLOW, "Can not change to " + dstState.name() + " status from " + curState.name());
        }
    }
}
