package org.k8scmp.appmgmt.dao;

import org.k8scmp.appmgmt.domain.AppInfo;

import java.util.List;

public interface AppDao{


    long createApp(AppInfo appInfo);

    void updateApp(AppInfo appInfo);

    void deleteApp(String id);

    AppInfo getApp(String id);

    List<AppInfo> getAppByAppId(String AppId);

	List<AppInfo> getApps(AppInfo appInfo);

}
