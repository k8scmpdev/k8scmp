package org.k8scmp.overview.service.impl;


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
import org.k8scmp.overview.dao.OverviewBiz;
import org.k8scmp.overview.domain.OverviewCountInfo;
import org.k8scmp.overview.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jason on 17-9-8.
 */
@Service
public class OverviewServiceImpl implements OverviewService {

    protected static Logger logger = LoggerFactory.getLogger(OverviewServiceImpl.class);
    @Autowired
    OverviewBiz overviewBiz;


	@Override
	public Map<String, Integer> getAppInfo() {
		List<OverviewCountInfo> overviewCountInfo = overviewBiz.getAppInfo();
//        if (globalInfo == null) {
//            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
//        }
        return parseCountInfo(overviewCountInfo);
	}
	
	@Override
	public Map<String, Integer> getServiceInfo() {
		List<OverviewCountInfo> overviewCountInfo = overviewBiz.getServiceInfo();
//        if (globalInfo == null) {
//            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
//        }
        return parseCountInfo(overviewCountInfo);
	}
	
	public Map<String, Integer> parseCountInfo(List<OverviewCountInfo> countInfoList){
		Map<String, Integer> data = new HashMap<>();
     	int count_r = 0;
     	int count_s = 0;
     	int count_o = 0;
     	for(int i=0;i<countInfoList.size();i++){
     		OverviewCountInfo count_info = countInfoList.get(i);
     		if("running".equals(count_info.getCountName())) count_r=count_info.getCount();
     		if("stop".equals(count_info.getCountName())) count_s=count_info.getCount();
     		if("operating".equals(count_info.getCountName())) count_o=count_info.getCount();
     	}
     	data.put("运行中: "+count_r,   count_r);
     	data.put("已停止: "+count_s,   count_s);
     	data.put("操作中: "+count_o,   count_o);
		return data;
	}
	
}
