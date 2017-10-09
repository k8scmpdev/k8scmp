package org.k8scmp.monitormgmt.service.alarm;


import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.HostInfo;
import org.springframework.ui.Model;

/**
 * Created by baokangwang on 2016/4/13.
 */
public interface HostGroupService {

    /**
     *
     * @return
     */
    List<HostGroupInfo> listHostGroupInfo();

    /**
     *
     * @param hostGroupInfoBasic
     * @return
     */
    HttpResponseTemp<?> createHostGroup(HostGroupInfoBasic hostGroupInfoBasic);

    /**
     *
     * @param hostGroupInfoBasic
     * @return
     */
    HttpResponseTemp<?> modifyHostGroup(HostGroupInfoBasic hostGroupInfoBasic);

    /**
     *
     * @param id
     * @return
     */
    HttpResponseTemp<?> deleteHostGroup(int id);

    /**
     *
     * @param id
     * @param hostInfoList
     * @return
     */
    HttpResponseTemp<?> bindHostList(int id, List<HostInfo> hostInfoList);

    /**
     *
     * @param id
     * @param hostId
     * @return
     */
    HttpResponseTemp<?> unbindHost(int id, int hostId);

    /**
     *
     * @param hostInfo
     */
    void createHostIfNotExist(HostInfo hostInfo);

	HttpResponseTemp<?> searchHostGroupInfo(String hostGroupName);
}
