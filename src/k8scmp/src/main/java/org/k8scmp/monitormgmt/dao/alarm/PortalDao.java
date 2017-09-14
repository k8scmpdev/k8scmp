package org.k8scmp.monitormgmt.dao.alarm;

import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;

public interface PortalDao {

	void insertTemplateByTemplateInfo(TemplateInfo templateInfo);

	void updateTemplateByTemplateInfo(TemplateInfo templateInfo);

	void deleteTemplateByIdAndType(long id, String templateType);

}
