package org.k8scmp.monitormgmt.service.alarm;

import org.k8scmp.basemodel.HttpResponseTemp;

/**
 * Created by baokangwang on 2016/5/6.
 */
public interface AlarmSenderService {

    /**
     *
     * @param tos
     * @param content
     * @param subject
     * @param sender
     * @return
     */
    HttpResponseTemp<?> sendSMS(String tos, String content, String subject, String sender);

    /**
     *
     * @param tos
     * @param content
     * @param subject
     * @param sender
     * @return
     */
    HttpResponseTemp<?> sendMail(String tos, String content, String subject, String sender);

    /**
     *
     * @param tos
     * @param content
     * @param subject
     * @param sender
     * @return
     */
    HttpResponseTemp<?> sendWechat(String tos, String content, String subject, String sender);
}
