package org.k8scmp.appmgmt.dao;

import org.k8scmp.appmgmt.domain.AppInfo;

import java.util.List;

public interface AppDao{


    long createApp(AppInfo appInfo);

    void updateApp(AppInfo appInfo);

    void deleteApp(String id);

    AppInfo getApp(String id);

	List<AppInfo> getApps(AppInfo appInfo);

	List<AppInfo> getAppsByserviceCode(String serviceCode);

	void updateAppState(String id,String state);

}
