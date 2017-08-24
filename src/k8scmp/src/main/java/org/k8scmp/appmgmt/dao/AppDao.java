package org.k8scmp.appmgmt.dao;

import org.k8scmp.appmgmt.domain.AppInfo;

import java.util.List;

/**
 * Created by KaiRen on 2016/9/20.
 */
public interface AppDao{


    long createApp(AppInfo appInfo);

    void updateApp(AppInfo appInfo);

    void deleteApp(String id);

    AppInfo getApp(String id);

    List<AppInfo> getAppByName(String name);
}
