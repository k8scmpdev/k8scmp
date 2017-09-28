package org.k8scmp.appmgmt.service.impl;

import java.util.Date;
import java.util.List;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.service.AppService;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.operation.OperationType;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.DateUtil;
import org.k8scmp.util.UUIDUtil;
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
    
    @Autowired
    OperationLog operationLog;
    
    private static Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

	@Override
	public Long createApp(AppInfo appInfo) {
		appInfo.setId(UUIDUtil.generateUUID());
		appInfo.setCreatorId("");
		appInfo.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
		Long result = appDao.createApp(appInfo);
		operationLog.insertRecord(new OperationRecord(
				appInfo.getId(), 
				ResourceType.APPLICATION,
				OperationType.SET, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"新增应用信息", 
				DateUtil.dateFormatToMillis(new Date())
		));
		
		return result;
	}

	@Override
	public void deleteApp(String id) {
		appDao.deleteApp(id);
		operationLog.insertRecord(new OperationRecord(
				id, 
				ResourceType.APPLICATION,
				OperationType.DELETE, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"删除应用信息", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}

	@Override
	public void modifyApp(AppInfo appInfo) {
		appInfo.setLastModifierId("");
		appInfo.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
		appDao.updateApp(appInfo);
		operationLog.insertRecord(new OperationRecord(
				appInfo.getId(), 
				ResourceType.APPLICATION,
				OperationType.MODIFY, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(),
				"OK", 
				"编辑应用信息", 
				DateUtil.dateFormatToMillis(new Date())
		));
	}

	@Override
	public List<AppInfo> listApps(AppInfo appInfo) {
		List<AppInfo> apps = appDao.getApps(appInfo);
		return apps;
	}

}
