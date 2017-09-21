package org.k8scmp.monitormgmt.dao.alarm;

import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;

public interface PortalDao {

	void insertTemplateByTemplateInfo(TemplateInfo templateInfo);

	void updateTemplateByTemplateInfo(TemplateInfo templateInfo);

	void deleteTemplateByIdAndType(long id, String templateType);

	void insertHostGroupByHostGroupBasicInfo(HostGroupInfoBasic hostGroupInfoBasic);

	void updateHostGroupByHostGroupBasicInfo(HostGroupInfoBasic updatedHostGroupInfoBasic);

	Integer getHostIdByHostname(String hostname);

	void insertGroupHostBind(long id, long hostId);

	void deleteGroupHostBind(long id, long hostId);

}
