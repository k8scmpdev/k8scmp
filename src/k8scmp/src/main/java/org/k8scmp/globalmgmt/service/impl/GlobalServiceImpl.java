package org.k8scmp.globalmgmt.service.impl;


import org.k8scmp.shiro.token.MultiAuthenticationToken;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.CryptoUtil;
import org.k8scmp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.CurrentThreadInfo;
import org.k8scmp.exception.ApiException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.ClusterInfo;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.globalmgmt.domain.MonitorInfo;
import org.k8scmp.globalmgmt.domain.RegisterInfo;
import org.k8scmp.globalmgmt.service.GlobalService;
import org.k8scmp.login.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jason on 17-9-8.
 */
@Service
public class GlobalServiceImpl implements GlobalService {

    protected static Logger logger = LoggerFactory.getLogger(GlobalServiceImpl.class);
    @Autowired
    GlobalBiz globalBiz;


	@Override
	public GlobalInfo getGlobalInfoByType(GlobalType globalType) {
		GlobalInfo globalInfo = globalBiz.getGlobalInfoByType(globalType);
//        if (globalInfo == null) {
//            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
//        }
        return globalInfo;
	}
	
	@Override
	public HttpResponseTemp<?> editClusterInfo(ClusterInfo clusterinfo, boolean flag) {
		int userId = CurrentThreadInfo.getUserId();
        if (flag) {
//            if (!AuthUtil.isAdmin(userId)) {
//                throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "must be admin");
//            }
        }
        if (clusterinfo == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "ClusterInfo info is null");
        }

        GlobalInfo globalInfo_clname = new GlobalInfo(GlobalType.CI_CLUSTER_NAME,clusterinfo.getName());
        GlobalInfo globalInfo_clapi = new GlobalInfo(GlobalType.CI_CLUSTER_HOST,clusterinfo.getApiserver());
        GlobalInfo globalInfo_cldesc = new GlobalInfo(GlobalType.CI_CLUSTER_DESC,clusterinfo.getDescription());
        globalBiz.updateGlobalInfoByType(globalInfo_clname);
        globalBiz.updateGlobalInfoByType(globalInfo_clapi);
        globalBiz.updateGlobalInfoByType(globalInfo_cldesc);
        return ResultStat.OK.wrap(clusterinfo);
	}
	
	@Override
	public HttpResponseTemp<?> editRegistryInfo(RegisterInfo registryinfo, boolean flag) {
		int userId = CurrentThreadInfo.getUserId();
        if (flag) {
//            if (!AuthUtil.isAdmin(userId)) {
//                throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "must be admin");
//            }
        }
        if (registryinfo == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "registryInfo info is null");
        }

        GlobalInfo globalInfo_rname = new GlobalInfo(GlobalType.REGISTRY_NAME,registryinfo.getName());
        GlobalInfo globalInfo_rapi = new GlobalInfo(GlobalType.REGISTRY_URL,registryinfo.getUrl());
        GlobalInfo globalInfo_rdesc = new GlobalInfo(GlobalType.REGISTRY_DESCRIPTION,registryinfo.getDescription());
        globalBiz.updateGlobalInfoByType(globalInfo_rname);
        globalBiz.updateGlobalInfoByType(globalInfo_rapi);
        globalBiz.updateGlobalInfoByType(globalInfo_rdesc);
        return ResultStat.OK.wrap(registryinfo);
	}
	
	@Override
	public HttpResponseTemp<?> editMonitorInfo(MonitorInfo monitorInfo, boolean flag) {
		int userId = CurrentThreadInfo.getUserId();
        if (flag) {
//            if (!AuthUtil.isAdmin(userId)) {
//                throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "must be admin");
//            }
        }
        if (monitorInfo == null) {
            throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, "monitorInfo info is null");
        }

        GlobalInfo globalInfo_montran = new GlobalInfo(GlobalType.MONITOR_TRANSFER,monitorInfo.getTransfer());
        GlobalInfo globalInfo_mongra = new GlobalInfo(GlobalType.MONITOR_GRAPH,monitorInfo.getGraphy());
        GlobalInfo globalInfo_monque = new GlobalInfo(GlobalType.MONITOR_QUERY,monitorInfo.getQuery());
        GlobalInfo globalInfo_monhbs = new GlobalInfo(GlobalType.MONITOR_HBS,monitorInfo.getHbs());
        GlobalInfo globalInfo_monjud = new GlobalInfo(GlobalType.MONITOR_JUDGE,monitorInfo.getJudge());
        GlobalInfo globalInfo_monala = new GlobalInfo(GlobalType.MONITOR_ALARM,monitorInfo.getAlarm());
        GlobalInfo globalInfo_monsen = new GlobalInfo(GlobalType.MONITOR_SENDER,monitorInfo.getSender());
        GlobalInfo globalInfo_monred = new GlobalInfo(GlobalType.MONITOR_REDIS,monitorInfo.getRedis());
        globalBiz.updateGlobalInfoByType(globalInfo_montran);
        globalBiz.updateGlobalInfoByType(globalInfo_mongra);
        globalBiz.updateGlobalInfoByType(globalInfo_monque);
        globalBiz.updateGlobalInfoByType(globalInfo_monhbs);
        globalBiz.updateGlobalInfoByType(globalInfo_monjud);
        globalBiz.updateGlobalInfoByType(globalInfo_monala);
        globalBiz.updateGlobalInfoByType(globalInfo_monsen);
        globalBiz.updateGlobalInfoByType(globalInfo_monred);
        return ResultStat.OK.wrap(monitorInfo);
	}
	
	@Override
	public HttpResponseTemp<?> editClusterInfo(ClusterInfo clusterinfo) {
		return editClusterInfo(clusterinfo,true);
	}
	
	@Override
	public HttpResponseTemp<?> editRegistryInfo(RegisterInfo registryinfo) {
		return editRegistryInfo(registryinfo,true);
	}
	
	@Override
	public HttpResponseTemp<?> editMonitorInfo(MonitorInfo monitorInfo) {
		return editMonitorInfo(monitorInfo,true);
	}
}
