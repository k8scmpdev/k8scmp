package org.k8scmp.monitormgmt.dao.alarm;

import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;

public interface PortalDao {

	void insertTemplateByTemplateInfo(TemplateInfo templateInfo);

	void updateTemplateByTemplateInfo(TemplateInfo templateInfo);

	void deleteTemplateByIdAndType(int id, String templateType);

	int insertHostGroupByHostGroupBasicInfo(HostGroupInfoBasic hostGroupInfoBasic);

	void updateHostGroupByHostGroupBasicInfo(HostGroupInfoBasic updatedHostGroupInfoBasic);

	Integer getHostIdByHostname(String hostname);

	void insertGroupHostBind(int id, int hostId);

	void deleteGroupHostBind(int id, int hostId);

	void deleteHostGroupById(int id);

}
