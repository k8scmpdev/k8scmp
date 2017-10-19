package org.k8scmp.appmgmt.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.k8scmp.appmgmt.dao.AppDao;
import org.k8scmp.appmgmt.dao.ServiceDao;
import org.k8scmp.appmgmt.dao.VersionDao;
import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.Cluster;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;
import org.k8scmp.appmgmt.service.VersionService;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.engine.ClusterRuntimeDriver;
import org.k8scmp.engine.RuntimeDriver;
import org.k8scmp.exception.ApiException;
import org.k8scmp.exception.DeploymentEventException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.model.ServiceStatus;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.DateUtil;
import org.k8scmp.util.StringUtils;
import org.k8scmp.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by KaiRen on 2016/9/22.
 */
@Service
public class VersionServiceImpl implements VersionService {
    @Autowired
    VersionDao versionDao;
    
    @Autowired
    ServiceDao serviceDao;
    
    @Autowired
    AppDao appDao;
    
    @Autowired
    GlobalBiz globalBiz;
    
    @Autowired
    OperationLog operationLog;
    
    private static Logger logger = LoggerFactory.getLogger(VersionServiceImpl.class);

	@Override
	public String createVersion(String serviceId, Version version) {
		String errInfo = version.checkLegality();
        if (!StringUtils.isBlank(errInfo)) {
            throw new ApiException(ResultStat.SERVICE_NOT_LEGAL,errInfo);
        }
        
        String versionId = null;
        try {
        	ServiceInfo serviceInfo = serviceDao.getService(serviceId);
            if (serviceInfo == null) {
                throw ApiException.wrapResultStat(ResultStat.SERVICE_NOT_EXIST);
            }
            Cluster cluster = getCluster();
            if (cluster == null) {
                throw ApiException.wrapResultStat(ResultStat.CLUSTER_NOT_EXIST);
            }
            versionId = UUIDUtil.generateUUID();
            version.setId(versionId);
            version.setCreatorId(AuthUtil.getCurrentLoginName());
            version.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
    		version.setServiceId(serviceId);
    		version.setState("");
    		version.setDeprecate(false);
    		
            versionDao.insertVersion(version);
        } catch (Exception e) {
            versionDao.deleteVersionById(versionId);
            throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, e.getMessage());
        }
        return versionId;
	}

	@Override
	public Version getVersion(String serviceId, int ver) {
		VersionBase verBase = versionDao.getVersion(serviceId, ver);
		if(verBase == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
		Version version = verBase.toModel(Version.class);
//		if (version.getVersionType() != VersionType.CUSTOM) {
//			 DeployResourceHandler deployResourceHandler = new DeploymentDeployHandler(null, null, null);
////			 VersionString versionString = deployResourceHandler.getVersionString(version,null);
////			 if(versionString != null){
////				 versionString.setPodSpecStr(version.getPodSpecStr());
////			 }
//		}
		return version;
	}

	@Override
	public Version getMaxVersion(String serviceId) {
		int ver = versionDao.getMaxVersion(serviceId);
		VersionBase verBase = versionDao.getVersion(serviceId, ver);
		if(verBase == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
		return verBase.toModel(Version.class);
	}
	
	@Override
	public List<Version> getVersionNames(String serviceId) {
		return versionDao.getVersionNames(serviceId);
	}
	
	@Override
	public List<Version> listVersion(String serviceId) {
		List<VersionBase> versionBases = versionDao.getAllVersionByServiceId(serviceId);
		if(versionBases == null || versionBases.size()==0){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
		List<Version> versions = new ArrayList<>(versionBases.size());
		for(VersionBase versionBase:versionBases){
			Version version = versionBase.toModel(Version.class);
			versions.add(version);
		}
		
		Collections.sort(versions, new Comparator<Version>() {
            @Override
            public int compare(Version o1, Version o2) {
                return ((Integer) o2.getVersion()).compareTo(o1.getVersion());
            }
        });
		
		return versions;
	}

	@Override
	public void deprecateVersion(String serviceId, int ver) throws Exception {
		VersionBase verBase = versionDao.getVersion(serviceId, ver);
		if(verBase == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
		Version version = verBase.toModel(Version.class);
		
		ServiceInfo serviceInfo = serviceDao.getService(serviceId);
        if (serviceInfo == null) {
            throw ApiException.wrapResultStat(ResultStat.SERVICE_NOT_EXIST);
        }
		
        ServiceConfigInfo serviceConfigInfo = serviceInfo.toModel(ServiceConfigInfo.class);
        
        AppInfo appInfo = appDao.getApp(serviceInfo.getAppId());
        
        if (checkDeprecated(appInfo, serviceConfigInfo, version.getId())) {
        	version.setLastModifierId(AuthUtil.getCurrentLoginName());
            version.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
            version.setDeprecate(true);
            versionDao.updateVersion(version);
        } else {
            throw ApiException.wrapMessage(ResultStat.CANNOT_DEPRECATE_VERSION, "can't  deprecate current version");
        }
	}

	@Override
	public void enableVersion(String serviceId, int ver) {
		VersionBase verBase = versionDao.getVersion(serviceId, ver);
		if(verBase == null){
			throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, "no such service version:" + serviceId);
		}
		
        try {
        	Version version = verBase.toModel(Version.class);
    		version.setLastModifierId(AuthUtil.getCurrentLoginName());
            version.setLastModifiedTime(DateUtil.dateFormatToMillis(new Date()));
            version.setDeprecate(false);
        	versionDao.updateVersion(version);
        } catch (Exception e) {
        	throw ApiException.wrapMessage(ResultStat.PARAM_ERROR, e.getMessage());
        }
	}

   private boolean checkDeprecated(AppInfo appInfo, ServiceConfigInfo serviceConfigInfo, String deprecatedVersionId) {
       if (!ServiceStatus.STOP.name().equals(serviceConfigInfo.getState())) {
        	Cluster cluster = getCluster();
    		RuntimeDriver driver = ClusterRuntimeDriver.getClusterDriver(cluster.getId());
            if (driver == null) {
                throw ApiException.wrapMessage(ResultStat.CLUSTER_NOT_EXIST, " There is no RuntimeDriver for cluster(" + cluster.getId() + ").");
            }
            // get current versions
            List<VersionBase> versions;
            try {
                versions = driver.getCurrnetVersionsByService(appInfo,serviceConfigInfo);
            } catch (DeploymentEventException e) {
                return false;
            }
            if (versions != null) {
                for (VersionBase ver : versions) {
                    if (ver.getId() == deprecatedVersionId) {
                        return false;
                    }
                }
            } 
        }
        return true;
    }
	   
	private Cluster getCluster(){
		GlobalInfo cluster_host = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
		GlobalInfo cluster_name = globalBiz.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
    	Cluster cluster = new Cluster();
    	cluster.setApi(cluster_host.getValue());
    	cluster.setId(cluster_host.getId()+"");
    	cluster.setName(cluster_name.getValue());
    	
		return cluster;
	}
	
}
