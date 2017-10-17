package org.k8scmp.appmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.k8scmp.appmgmt.dao.ServiceEventDao;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.mapper.appmgmt.ServiceEventMapper;
import org.k8scmp.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("serviceEventDao")
public class ServiceEventDaoImpl implements ServiceEventDao{
	
	@Autowired
    ServiceEventMapper mapper;
	
	@Override
	public String createEvent(DeployEvent deployEvent) {
		deployEvent.setId(UUIDUtil.generateUUID());
		mapper.createEvent(deployEvent, deployEvent.toString());
		return deployEvent.getId();
	}

	@Override
	public DeployEvent getEvent(String id) {
		DeployEvent deployEvent = mapper.getEvent(id);
		return deployEvent==null?null:deployEvent.toModel();
	}

	@Override
	public DeployEvent getNewestEvent(String serviceId) {
		DeployEvent deployEvent = mapper.getNewestEvent(serviceId);
		return deployEvent==null?null:deployEvent.toModel();
	}

	@Override
	public List<DeployEvent> getEventByServiceId(String serviceId) {
		List<DeployEvent> deployEvents = mapper.getEventByServiceId(serviceId);
		
		if(deployEvents!=null && deployEvents.size()>0){
			List<DeployEvent> deployEventsNew = new ArrayList<>(deployEvents.size());
			for(DeployEvent deployEvent:deployEvents){
				deployEventsNew.add(deployEvent.toModel());
			}
			return deployEventsNew;
		}
		
		return null;
	}

	@Override
	public void updateEvent(DeployEvent deployEvent) {
		mapper.updateEvent(deployEvent.getId(), deployEvent.getState(), deployEvent.getExpireTime(), deployEvent.toString());
	}

	@Override
	public List<DeployEvent> getUnfinishedEvent() {
		List<DeployEvent> deployEvents = mapper.getUnfinishedEvent();
		
		if(deployEvents!=null && deployEvents.size()>0){
			List<DeployEvent> deployEventsNew = new ArrayList<>(deployEvents.size());
			for(DeployEvent deployEvent:deployEvents){
				deployEventsNew.add(deployEvent.toModel());
			}
			return deployEventsNew;
		}
		
		return null;
	}

	@Override
	public List<DeployEvent> listRecentEventByAppIdTime(String idList, String startTime) {
		
		List<DeployEvent> deployEvents = mapper.listRecentEventByAppIdTime(idList, startTime);
		
		if(deployEvents!=null && deployEvents.size()>0){
			List<DeployEvent> deployEventsNew = new ArrayList<>(deployEvents.size());
			for(DeployEvent deployEvent:deployEvents){
				deployEventsNew.add(deployEvent.toModel());
			}
			return deployEventsNew;
		}
		
		return null;
	}

	@Override
	public List<DeployEvent> listServiceExistedEventByTime(String startTime) {
		List<DeployEvent> deployEvents = mapper.listServiceExistedEventByTime(startTime);
		
		if(deployEvents!=null && deployEvents.size()>0){
			List<DeployEvent> deployEventsNew = new ArrayList<>(deployEvents.size());
			for(DeployEvent deployEvent:deployEvents){
				deployEventsNew.add(deployEvent.toModel());
			}
			return deployEventsNew;
		}
		
		return null;
	}

	
}
