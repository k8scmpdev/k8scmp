package org.k8scmp.monitormgmt.service.alarm;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;

/**
 * Created by baokangwang on 2016/4/13.
 */
public interface TemplateService {

    /**
     *
     * @return
     */
    HttpResponseTemp<?> listTemplateInfo();

    /**
     *
     * @param templateInfo
     * @return
     */
    HttpResponseTemp<?> createTemplate(TemplateInfo templateInfo);

    /**
     *
     * @param templateInfo
     * @return
     */
    HttpResponseTemp<?> modifyTemplate(TemplateInfo templateInfo);

    /**
     *
     * @param id
     * @return
     */
    HttpResponseTemp<?> getTemplateInfo(int id);

    /**
     *
     * @param id
     * @return
     */
    HttpResponseTemp<?> deleteTemplate(long id);
}
