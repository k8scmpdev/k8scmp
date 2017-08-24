package org.k8scmp.appmgmt.service.impl;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.service.AppService;
import org.k8scmp.basemodel.HttpResponseTemp;
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
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public HttpResponseTemp<?> deleteApp(
			String id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public HttpResponseTemp<?> modifyApp(
			String id, AppInfo appInfo) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public HttpResponseTemp<?> listApps() {
		// TODO Auto-generated method stub
		return null;
	}


}
