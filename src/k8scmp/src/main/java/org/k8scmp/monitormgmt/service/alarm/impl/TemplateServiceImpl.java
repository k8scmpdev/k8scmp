package org.k8scmp.monitormgmt.service.alarm.impl;

import java.util.LinkedList;
import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.exception.ApiException;
import org.k8scmp.login.dao.AuthBiz;
import org.k8scmp.login.domain.User;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.dao.alarm.PortalDao;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;
import org.k8scmp.monitormgmt.domain.alarm.DeploymentInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostEnv;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.TemplateType;
import org.k8scmp.monitormgmt.service.alarm.TemplateService;
import org.k8scmp.operation.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by baokangwang on 2016/4/13.
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    private static Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private final ResourceType resourceType = ResourceType.ALARM;

    @Autowired
    AlarmDao alarmDao;

    @Autowired
    AuthBiz authBiz;

    @Autowired
    PortalDao portalDao;

//    @Autowired
//    ClusterBiz clusterBiz;

//    @Autowired
//    DeploymentBiz deploymentBiz;

    @Override
    public List<TemplateInfoBasic> listTemplateInfo() {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);
        return alarmDao.listTemplateInfoBasic();
    }

    @Override
    public HttpResponseTemp<?> createTemplate(TemplateInfo templateInfo) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        if (templateInfo == null) {
            throw ApiException.wrapMessage(ResultStat.TEMPLATE_NOT_LEGAL, "template info is null");
        }
        if (templateInfo.checkLegality() != null) {
            throw ApiException.wrapMessage(ResultStat.TEMPLATE_NOT_LEGAL, templateInfo.checkLegality());
        }
        if (alarmDao.getTemplateInfoBasicByName(templateInfo.getTemplateName()) != null) {
            throw ApiException.wrapResultStat(ResultStat.TEMPLATE_EXISTED);
        }

        TemplateInfoBasic templateInfoBasic = new TemplateInfoBasic();
        templateInfoBasic.setTemplateName(templateInfo.getTemplateName());
        templateInfoBasic.setTemplateType(templateInfo.getTemplateType());
//        templateInfoBasic.setCreatorId(AuthUtil.getUserId());
//        templateInfoBasic.setCreatorName(AuthUtil.getCurrentUserName());
        templateInfoBasic.setCreateTime(System.currentTimeMillis());
        templateInfoBasic.setUpdateTime(templateInfoBasic.getCreateTime());
        alarmDao.addTemplateInfoBasic(templateInfoBasic);
        templateInfo.setId(templateInfoBasic.getId());
        templateInfo.setCreateTime(templateInfoBasic.getCreateTime());
        templateInfo.setCreatorName(templateInfoBasic.getCreatorName());

        createTemplateRelated(templateInfo);

        // insert template into portal database
        portalDao.insertTemplateByTemplateInfo(templateInfo);

        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> modifyTemplate(TemplateInfo templateInfo) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        if (templateInfo == null) {
            throw ApiException.wrapMessage(ResultStat.TEMPLATE_NOT_LEGAL, "template info is null");
        }
        if (templateInfo.checkLegality() != null) {
            throw ApiException.wrapMessage(ResultStat.TEMPLATE_NOT_LEGAL, templateInfo.checkLegality());
        }
        TemplateInfoBasic updatedTemplateInfoBasic = alarmDao.getTemplateInfoBasicById(templateInfo.getId());
        if (updatedTemplateInfoBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.TEMPLATE_NOT_EXISTED);
        }

        updatedTemplateInfoBasic.setTemplateName(templateInfo.getTemplateName());
        updatedTemplateInfoBasic.setUpdateTime(System.currentTimeMillis());
        alarmDao.updateTemplateInfoBasicById(updatedTemplateInfoBasic);

        deleteTemplateRelated(templateInfo.getId());
        createTemplateRelated(templateInfo);

        templateInfo.setCreateTime(updatedTemplateInfoBasic.getCreateTime());
        templateInfo.setCreatorName(updatedTemplateInfoBasic.getCreatorName());

        // update template into portal database
        portalDao.updateTemplateByTemplateInfo(templateInfo);

        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> getTemplateInfo(long id) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        TemplateInfoBasic templateInfoBasic = alarmDao.getTemplateInfoBasicById(id);
        if (templateInfoBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.TEMPLATE_NOT_EXISTED);
        }

        TemplateInfo templateInfo = new TemplateInfo(templateInfoBasic);
        if (templateInfoBasic.getTemplateType().equals(TemplateType.host.name())) {
            templateInfo.setHostGroupList(alarmDao.listHostGroupInfoBasicByTemplateId(id));
        } else if (templateInfoBasic.getTemplateType().equals(TemplateType.deploy.name())) {
            /*Deployment deployment = alarmDao.getDeploymentByTemplateId(id);
            DeploymentInfo deploymentInfo;
            if (deployment == null) {
                deploymentInfo = new DeploymentInfo(0, null, "non-existed deployment", HostEnv.TEST);
            } else {
                deploymentInfo = new DeploymentInfo(deployment);
                Cluster cluster = clusterBiz.getClusterById(deployment.getClusterId());
                if (cluster != null) {
                    deploymentInfo.setClusterName(cluster.getName());
                }
            }
            templateInfo.setDeploymentInfo(deploymentInfo);*/
        }
        templateInfo.setStrategyList(alarmDao.listStrategyInfoByTemplateId(id));
        List<User> userInfos = new LinkedList<>();
        List<Long> userIds = alarmDao.listUserIdByTemplateId(id);
        for (Long userId : userIds) {
            if (userId == null) {
                continue;
            }
            User userBasic = alarmDao.getUserById(userId);
            if (userBasic == null || userBasic.getUsername() == null) {
                continue;
            }
//            String userGroupName = userBasic.getUsername();
            userInfos.add(userBasic);
        }
        templateInfo.setUserList(userInfos);
        templateInfo.setCallback(alarmDao.getCallbackInfoByTemplateId(id));

        return ResultStat.OK.wrap(templateInfo);
    }

    @Override
    public HttpResponseTemp<?> deleteTemplate(long id) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        TemplateInfoBasic templateInfoBasic = alarmDao.getTemplateInfoBasicById(id);
        if (templateInfoBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.TEMPLATE_NOT_EXISTED);
        }

        deleteTemplateRelated(id);
        alarmDao.deleteTemplateInfoBasicById(id);

        // delete template from portal database
        portalDao.deleteTemplateByIdAndType(id, templateInfoBasic.getTemplateType());

        return ResultStat.OK.wrap(null);
    }

    private void createTemplateRelated(TemplateInfo templateInfo) {

        long templateId = templateInfo.getId();
        long current = System.currentTimeMillis();

        if (templateInfo.getTemplateType().equals(TemplateType.host.name())) {
            // for host group : only host alarm need to record this
            for (HostGroupInfoBasic hostGroupInfoBasic : templateInfo.getHostGroupList()) {
                if (hostGroupInfoBasic == null) {
                    continue;
                }
                alarmDao.addTemplateHostGroupBind(templateId, hostGroupInfoBasic.getId(), current);
            }
        } else if (templateInfo.getTemplateType().equals(TemplateType.deploy.name())) {
            alarmDao.setTemplateDeployIdByTemplateId(templateId, templateInfo.getDeploymentInfo().getId());
        }

        // for strategy
        for (StrategyInfo strategyInfo : templateInfo.getStrategyList()) {
            if (strategyInfo == null) {
                continue;
            }
            strategyInfo.setCreateTime(System.currentTimeMillis());
            strategyInfo.setTemplateId(templateId);
            alarmDao.addStrategyInfo(strategyInfo);
        }
        // for user group
        for (User userInfo : templateInfo.getUserList()) {
            if (userInfo == null) {
                continue;
            }
            alarmDao.addTemplateUserGroupBind(templateId, userInfo.getId(), current);
        }
        // for callback
        CallBackInfo callbackInfo = templateInfo.getCallback();
        alarmDao.addCallBackInfo(callbackInfo);
        alarmDao.setTemplateCallbackIdByTemplateId(templateId, callbackInfo.getId());
    }

    private void deleteTemplateRelated(long templateId) {

        TemplateInfoBasic templateInfoBasic = alarmDao.getTemplateInfoBasicById(templateId);
        if (templateInfoBasic == null) {
            return;
        }

        if (templateInfoBasic.getTemplateType().equals(TemplateType.host.name())) {
            // for host group
            alarmDao.deleteTemplateHostGroupBindByTemplateId(templateId);
        }
        // for strategy
        alarmDao.deleteStrategyInfoByTemplateId(templateId);
//        alarmDao.deleteTemplateStrategyBindByTemplateId(templateId);
        // for user group
        alarmDao.deleteTemplateUserBindByTemplateId(templateId);
        // for callback
        alarmDao.deleteCallbackInfoByTemplateId(templateId);
    }

//    private long createStrategy(StrategyInfo strategyInfo) {
//
//        alarmDao.addStrategyInfo(strategyInfo);
//        return strategyInfo.getId();
//    }

}
