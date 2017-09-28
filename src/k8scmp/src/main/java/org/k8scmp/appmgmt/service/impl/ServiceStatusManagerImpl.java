package org.k8scmp.appmgmt.service.impl;

import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.dao.ServiceEventDao;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.DeploymentSnapshot;
import org.k8scmp.appmgmt.service.ServiceStatusManager;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.exception.ApiException;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.exception.DeploymentTerminatedException;
import org.k8scmp.login.domain.User;
import org.k8scmp.model.DeployEventStatus;
import org.k8scmp.model.DeployOperation;
import org.k8scmp.model.ServiceStatus;
import org.k8scmp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
@Service
public class ServiceStatusManagerImpl implements ServiceStatusManager {

	@Autowired
	private ServiceEventDao serviceEventDao;
	
	@Autowired
	private ServiceDao serviceDao;
	
	 private static boolean isMonitorStart = false;
	    private static Logger logger = LoggerFactory.getLogger(ServiceStatusManagerImpl.class);
	    // in ms
	    private static long expirePeriod = 10 * 60 * 1000;
	    private static long checkPeriod = 5000;

	    void setExpirePeriod(long expirePeriod) {
	        this.expirePeriod = expirePeriod;
	    }

	    void setCheckPeriod(long checkPeriod) {
	        this.checkPeriod = checkPeriod;
	    }

	    public ServiceStatusManagerImpl() {
	        if (!isMonitorStart) {
	            isMonitorStart = true;
	        }
	    }

	    @Override
	    public String registerEvent(String serviceId, DeployOperation operation, User user, List<DeploymentSnapshot> srcSnapshot,
	                              List<DeploymentSnapshot> currentSnapshot, List<DeploymentSnapshot> dstSnapshot) throws DeploymentEventException, IOException {
	        // ** check event status and service status
	        DeployEvent event = serviceEventDao.getNewestEvent(serviceId);
	        if (event == null && !operation.equals(DeployOperation.START)) {
	            throw new DeploymentEventException("no history event found, no start record.");
	        }
	        if (event != null && !DeployEventStatus.isTerminal(event.getState())) {
	            throw new DeploymentEventException("latest event(" + event.getOperation() + ") with id="
	                    + event.getId() + "is in status " + event.getState() + ", not terminated");
	        }

	        event = buildEvent(serviceId, user, srcSnapshot, currentSnapshot, dstSnapshot);
	        event.setOperation(operation);
	        return serviceEventDao.createEvent(event);
	    }

	    @Override
	    public String registerAbortEvent(String serviceId, User user) throws DeploymentEventException, IOException {
	        // update current event
	        DeployEvent currentEvent = serviceEventDao.getNewestEvent(serviceId);
	        if (currentEvent == null) {
	            throw ApiException.wrapMessage(ResultStat.SERVICE_ABORT_EVENT_FAILED, "There is no service event for service with serviceId=" + serviceId);
	        }
	        if (!currentEvent.getState().equals(DeployEventStatus.PROCESSING)) {
	            throw ApiException.wrapMessage(ResultStat.SERVICE_ABORT_EVENT_FAILED, "The newest service event status is "
	                    + currentEvent.getState() + ", can not be aborted.");
	        }
	        DeployEvent abortEvent = buildEvent(serviceId, user, currentEvent.getCurrentSnapshot(),
	                currentEvent.getCurrentSnapshot(), currentEvent.getCurrentSnapshot());
	        abortEvent.setMessage("abort");
	        switch (currentEvent.getOperation()) {
	            case START:
	                abortEvent.setOperation(DeployOperation.ABORT_START);
	                break;
	            case UPDATE:
	                abortEvent.setOperation(DeployOperation.ABORT_UPDATE);
	                break;
	            case ROLLBACK:
	                abortEvent.setOperation(DeployOperation.ABORT_ROLLBACK);
	                break;
	            case SCALE_UP:
	                abortEvent.setOperation(DeployOperation.ABORT_SCALE_UP);
	                break;
	            case SCALE_DOWN:
	                abortEvent.setOperation(DeployOperation.ABORT_SCALE_DOWN);
	                break;
	            default:
	                throw ApiException.wrapMessage(ResultStat.SERVICE_ABORT_EVENT_FAILED, "The newest service event operation is "
	                        + currentEvent.getOperation() + ", can not be aborted.");
	        }
	        currentEvent.setState(DeployEventStatus.ABORTED);
	        serviceEventDao.updateEvent(currentEvent);
	        return serviceEventDao.createEvent(abortEvent);
	    }

	    @Override
	    public void freshEvent(String id, List<DeploymentSnapshot> currentSnapshot)
	            throws IOException, DeploymentEventException {
	        // ** get and check latest event
	        DeployEvent event = serviceEventDao.getEvent(id);
	        if (event == null) {
	            throw new DeploymentEventException("could not find event(id=" + id + ")");
	        }
	        if (DeployEventStatus.isTerminal(event.getState())) {
	            return;
//	            throw new DeploymentEventException("latest event(" + event.getOperation() + ") with id="
//	                    + event.getEid() + " is in status " + event.getState() + ", not terminated");
	        }
	        if (event.getState().equals(DeployEventStatus.START)) {
	            // ** update event
	            long current = System.currentTimeMillis();
	            event.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date(current)));
	            event.setExpireTime(DateUtil.dateFormatToMillis(new Date(current + expirePeriod)));
	            event.setState(DeployEventStatus.PROCESSING);
	            event.setCurrentSnapshot(currentSnapshot);
	            serviceEventDao.updateEvent(event);
	            switch (event.getOperation()) {
	                case UPDATE:
	                    serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.UPDATING.name());
	                    break;
	                case ROLLBACK:
	                    serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.BACKROLLING.name());
	                    break;
	                case SCALE_UP:
	                    serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.UPSCALING.name());
	                    break;
	                case SCALE_DOWN:
	                    serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.DOWNSCALING.name());
	                    break;
	                case START:
	                    serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.DEPLOYING.name());
	                    break;
	                case STOP:
	                    serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.STOPPING.name());
	                    break;
	                case ABORT_START:
	                case ABORT_UPDATE:
	                case ABORT_ROLLBACK:
	                case ABORT_SCALE_UP:
	                case ABORT_SCALE_DOWN:
	                    serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.ABORTING.name());
	                    break;
	                default:
	                    throw new DeploymentEventException("event(id=" + event.getId() + ") operation(" + event.getOperation()
	                            + ") can not match any service status");
	            }
	        }
	    }

	    @Override
	    public void succeedEvent(String id, List<DeploymentSnapshot> currentSnapshot)
	            throws IOException, DeploymentEventException, DeploymentTerminatedException {
	        // ** get and check latest event
	        DeployEvent event = serviceEventDao.getEvent(id);
	        if (event == null) {
	            throw new DeploymentEventException("could not find event(id=" + id + ")");
	        }
	        if (DeployEventStatus.SUCCESS.equals(event.getState())) {
//	            throw new DeploymentTerminatedException("latest event(" + event.getOperation() + ") with id="
//	                    + event.getEid() + "is in status " + event.getState() + ", has terminated");
	            return;
	        }
	        // update event status
	        long current = System.currentTimeMillis();
	        event.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date(current)));
	        event.setExpireTime(DateUtil.dateFormatToMillis(new Date(current + expirePeriod)));
	        event.setState(DeployEventStatus.SUCCESS);
	        event.setCurrentSnapshot(currentSnapshot);
	        event.setTargetSnapshot(currentSnapshot);
	        serviceEventDao.updateEvent(event);
	        // update service status
	        switch (event.getOperation()) {
	            case UPDATE:
	            case ROLLBACK:
	            case SCALE_UP:
	            case SCALE_DOWN:
	            case START:
	            case ABORT_SCALE_UP:
	            case ABORT_SCALE_DOWN:
	                serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.RUNNING.name());
	                break;
	            case STOP:
	            case ABORT_START:
	                serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.STOP.name());
	                break;
	            case ABORT_UPDATE:
	                serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.UPDATE_ABORTED.name());
	                break;
	            case ABORT_ROLLBACK:
	                serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.BACKROLL_ABORTED.name());
	                break;
	            default:
	                throw new DeploymentEventException("Can not update service status according to " + event.getOperation() + " event");
	        }
	    }

	    @Override
	    public void failedEvent(String id, List<DeploymentSnapshot> currentSnapshot, String message)
	            throws IOException, DeploymentEventException, DeploymentTerminatedException {
	        // ** get and check latest event
	        DeployEvent event = serviceEventDao.getEvent(id);
	        if (event == null) {
	            throw new DeploymentEventException("could not find event(id=" + id + ")");
	        }
	        if (DeployEventStatus.FAILED.equals(event.getState())) {
	            return;
//	            throw new DeploymentTerminatedException("latest event(" + event.getOperation() + ") with id="
//	                    + event.getEid() + " is in status " + event.getState() + ", has terminated");
	        }
	        // update event status
	        event.setState(DeployEventStatus.FAILED);
	        long current = System.currentTimeMillis();
	        event.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date(current)));
	        event.setExpireTime(DateUtil.dateFormatToMillis(new Date(current + expirePeriod)));
	        event.setCurrentSnapshot(currentSnapshot);
	        event.setMessage(message);
	        serviceEventDao.updateEvent(event);
	        // update service status
	        serviceDao.updateServiceStatu(event.getServiceId(), ServiceStatus.ERROR.name());
	    }

	    @Override
	    public void failedEventForDeployment(String serviceId, List<DeploymentSnapshot> currentSnapshot, String message)
	            throws IOException, DeploymentEventException, DeploymentTerminatedException {
	        try {
	            failedEvent(serviceEventDao.getNewestEvent(serviceId).getId(), currentSnapshot, message);
	        } catch (DeploymentEventException e) {
	            serviceDao.updateServiceStatu(serviceId, ServiceStatus.ERROR.name());
	            throw e;
	        }
	    }

	    public DeployEvent buildEvent(
	            String serviceId,
	            User user,
	            List<DeploymentSnapshot> srcSnapshot,
	            List<DeploymentSnapshot> currentSnapshot,
	            List<DeploymentSnapshot> dstSnapshot) {
	        DeployEvent event = new DeployEvent();
	        long time = System.currentTimeMillis();
	        event.setStartTime(DateUtil.dateFormatToMillis(new Date(time)));
	        event.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date(time)));
	        event.setExpireTime(DateUtil.dateFormatToMillis(new Date(time + expirePeriod)));
	        event.setPrimarySnapshot(srcSnapshot);
	        event.setCurrentSnapshot(currentSnapshot);
	        event.setTargetSnapshot(dstSnapshot);
	        event.setServiceId(serviceId);
	        event.setState(DeployEventStatus.START);
	        event.setOperatorId(user.getLoginname());
	        event.setUserName(user.getUsername());
	        return event;
	    }

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
