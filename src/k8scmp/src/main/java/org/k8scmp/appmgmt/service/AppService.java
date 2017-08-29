package org.k8scmp.appmgmt.service;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.basemodel.HttpResponseTemp;

/**
 * Created by KaiRen on 2016/9/22.
 */
public interface AppService {
    HttpResponseTemp<?> createApp(AppInfo appInfo);

    HttpResponseTemp<?> deleteApp(String id);

    HttpResponseTemp<?> modifyApp(AppInfo appInfo);

    HttpResponseTemp<?> listApps(AppInfo appInfo);
}
