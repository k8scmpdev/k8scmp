package org.k8scmp.appmgmt.dao.impl;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.mapper.appmgmt.AppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by KaiRen on 2016/9/20.
 */
@Service("appDao")
public class AppDaoImpl implements AppDao {

    @Autowired
    AppMapper mapper;

    @Override
    public long createApp(AppInfo appInfo) {

        return mapper.createApp(appInfo);
    }

    @Override
    public void updateApp(AppInfo appInfo) {
        mapper.updateApp(appInfo);
    }

	@Override
	public void deleteApp(String id) {
		 mapper.deleteApp(id);
		
	}

	@Override
	public AppInfo getApp(String id) {
		return mapper.getApp(id);
	}


	@Override
	public List<AppInfo> getApps(AppInfo appInfo) {
		return mapper.getApps(appInfo);
	}
 
	@Override
	public List<AppInfo> getAppsByserviceCode(String serviceCode) {
		return mapper.getAppsByserviceCode(serviceCode);
	}
}
