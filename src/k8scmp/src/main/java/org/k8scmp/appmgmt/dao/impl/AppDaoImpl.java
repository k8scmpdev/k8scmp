package org.k8scmp.appmgmt.dao.impl;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.mapper.AppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by KaiRen on 2016/9/20.
 */
@Service("deployCollectionBiz")
public class AppDaoImpl implements AppDao {

    @Autowired
    AppMapper mapper;

    @Override
    public long createApp(AppInfo appInfo) {

        return mapper.createApp(appInfo, appInfo.toString());
    }

    @Override
    public void updateApp(AppInfo appInfo) {
        mapper.updateApp(appInfo, appInfo.toString());
    }

	@Override
	public void deleteApp(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AppInfo getApp(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppInfo> getAppByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

 
}
