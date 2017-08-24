package org.k8scmp.appmgmt.service.impl;

import org.domeos.basemodel.HttpResponseTemp;
import org.domeos.basemodel.ResultStat;
import org.domeos.framework.api.biz.OperationHistory;
import org.domeos.framework.api.biz.collection.CollectionBiz;
import org.domeos.framework.api.biz.deployment.AppBiz;
import org.domeos.framework.api.consolemodel.deployment.AppDraft;
import org.domeos.framework.api.consolemodel.deployment.AppInfo;
import org.domeos.framework.api.controller.exception.ApiException;
import org.domeos.framework.api.model.auth.related.Role;
import org.domeos.framework.api.model.collection.CollectionAuthorityMap;
import org.domeos.framework.api.model.collection.CollectionResourceMap;
import org.domeos.framework.api.model.deployment.App;
import org.domeos.framework.api.model.operation.OperationRecord;
import org.domeos.framework.api.model.operation.OperationType;
import org.domeos.framework.api.model.collection.related.ResourceType;
import org.domeos.framework.api.service.deployment.AppService;
import org.domeos.framework.engine.AuthUtil;
import org.domeos.global.ClientConfigure;
import org.domeos.global.CurrentThreadInfo;
import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

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
