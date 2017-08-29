package org.k8scmp.appmgmt.service.impl;

import java.util.List;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.service.AppService;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by KaiRen on 2016/9/22.
 */
@Service
public class AppServiceImpl implements AppService {
    @Autowired
    AppDao appDao;


    private static Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);


	@Override
	public HttpResponseTemp<?> createApp(
			AppInfo appInfo) {
		Long result = appDao.createApp(appInfo);
		return ResultStat.OK.wrap(result);
	}


	@Override
	public HttpResponseTemp<?> deleteApp(
			String id) {
		appDao.deleteApp(id);
		return ResultStat.OK.wrap(null);
	}


	@Override
	public HttpResponseTemp<?> modifyApp(AppInfo appInfo) {
		appDao.updateApp(appInfo);
		return ResultStat.OK.wrap(null);
	}


	@Override
	public HttpResponseTemp<?> listApps(AppInfo appInfo) {
		List<AppInfo> apps = appDao.getApps(appInfo);
		return ResultStat.OK.wrap(apps);
	}


}
