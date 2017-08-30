package org.k8scmp.appmgmt.service;

import java.util.List;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.basemodel.HttpResponseTemp;

/**
 * Created by KaiRen on 2016/9/22.
 */
public interface AppService {
    Long createApp(AppInfo appInfo);

    void deleteApp(String id);

    void modifyApp(AppInfo appInfo);

    List<AppInfo> listApps(AppInfo appInfo);
}
