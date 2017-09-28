package org.k8scmp.appmgmt.dao;

import java.util.List;

import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.model.DeployEventStatus;

public interface ServiceEventDao {
	public String createEvent(DeployEvent deployEvent);

	public DeployEvent getEvent(String id);

	public DeployEvent getNewestEvent(String serviceId);

	public List<DeployEvent> getEventByServiceId(String serviceId);

	public void updateEvent(DeployEvent deployEvent);

	public  List<DeployEvent> getUnfinishedEvent();

	public List<DeployEvent> listRecentEventByAppIdTime(String idList,String startTime);

	public List<DeployEvent> listServiceExistedEventByTime(String startTime);
}
