package org.k8scmp.monitormgmt.service.alarm;

import org.k8scmp.monitormgmt.domain.alarm.asist.ActionWrap;
import org.k8scmp.monitormgmt.domain.alarm.asist.UserWrap;

/**
 * Created by baokangwang on 2016/4/14.
 */
public interface AssistService {

    ActionWrap getActionById(long actionId);

    UserWrap getUsers(String group);

    String storeLink(String content);

    String retrieveLink(int linkId);
}
