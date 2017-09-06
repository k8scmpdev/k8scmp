package org.k8scmp.appmgmt.service;

import java.util.List;

import org.k8scmp.appmgmt.domain.AppInfo;


public interface AppService {
    Long createApp(AppInfo appInfo);

    void deleteApp(String id);

    void modifyApp(AppInfo appInfo);

    List<AppInfo> listApps(AppInfo appInfo);
}
