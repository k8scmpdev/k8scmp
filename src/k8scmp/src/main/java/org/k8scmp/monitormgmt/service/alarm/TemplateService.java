package org.k8scmp.monitormgmt.service.alarm;

import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;

/**
 * Created by baokangwang on 2016/4/13.
 */
public interface TemplateService {

    /**
     *
     * @return
     */
    List<TemplateInfoBasic> listTemplateInfo();

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
    HttpResponseTemp<?> getTemplateInfo(long id);

    /**
     *
     * @param id
     * @return
     */
    HttpResponseTemp<?> deleteTemplate(long id);
}
