package org.k8scmp.appmgmt.service;

import org.k8scmp.appmgmt.domain.AppInfo;

/**
 * Created by KaiRen on 2016/9/22.
 */
public interface AppService {
    HttpResponseTemp<?> createApp(AppInfo appInfo);

    HttpResponseTemp<?> deleteApp(String id);

    HttpResponseTemp<?> modifyApp(String id, AppInfo appInfo);

    HttpResponseTemp<?> listApps();
}