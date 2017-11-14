package org.k8scmp.monitormgmt.service.alarm.impl;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ClientConfigure;
import org.k8scmp.exception.ApiException;
import org.k8scmp.login.dao.AuthBiz;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.related.UserInfo;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupBasic;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupDetail;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupDraft;
import org.k8scmp.monitormgmt.service.alarm.UserGroupService;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by KaiRen on 2016/9/27.
 */
@Service
public class UserGroupServiceImpl implements UserGroupService {

    private static Logger logger = LoggerFactory.getLogger(UserGroupServiceImpl.class);

    private final ResourceType resourceType = ResourceType.ALARM;

    @Autowired
    AlarmDao alarmBiz;

    @Autowired
    AuthBiz authBiz;


    @Override
    public HttpResponseTemp<?> listUserGroupInfo() {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        List<UserGroupBasic> userGroupBasics = alarmBiz.listUserGroupInfoBasic();
        if (userGroupBasics == null) {
            return ResultStat.OK.wrap(null);
        }
        List<UserGroupDetailTask> userGroupDetailTasks = new LinkedList<>();

        for (UserGroupBasic userGroupBasic : userGroupBasics) {
            userGroupDetailTasks.add(new UserGroupDetailTask(userGroupBasic));
        }
        List<UserGroupDetail> userGroupDetails = ClientConfigure.executeCompletionService(userGroupDetailTasks);
        Collections.sort(userGroupDetails, new UserGroupDetail.UserGroupDetailComparator());
        return ResultStat.OK.wrap(userGroupDetails);
    }

    @Override
    public HttpResponseTemp<?> createUserGroup(UserGroupDraft userGroupDraft) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        if (userGroupDraft == null) {
            throw ApiException.wrapMessage(ResultStat.USERGROUP_NOT_LEGAL, "user group info is null");
        }
        if (userGroupDraft.checkLegality() != null) {
            throw ApiException.wrapMessage(ResultStat.USERGROUP_NOT_LEGAL, userGroupDraft.checkLegality());
        }
        if (alarmBiz.getUserGroupInfoBasicByName(userGroupDraft.getUserGroupName()) != null) {
            throw ApiException.wrapResultStat(ResultStat.USERGROUP_EXISTED);
        }

        UserGroupBasic userGroupBasic = userGroupDraft.toUserGroupBasic();
        userGroupBasic.setCreatorId(AuthUtil.getUserId());
        userGroupBasic.setCreatorName(AuthUtil.getCurrentLoginName());
        userGroupBasic.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
        userGroupBasic.setUpdateTime(userGroupBasic.getCreateTime());

        alarmBiz.addUserGroupInfoBasic(userGroupBasic);
        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> modifyUserGroup(UserGroupDraft userGroupDraft) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        if (userGroupDraft == null) {
            throw ApiException.wrapMessage(ResultStat.USERGROUP_NOT_LEGAL, "user group info is null");
        }
        if (userGroupDraft.checkLegality() != null) {
            throw ApiException.wrapMessage(ResultStat.USERGROUP_NOT_LEGAL, userGroupDraft.checkLegality());
        }
        UserGroupBasic updatedUserGroupBasic = alarmBiz.getUserGroupInfoBasicById(userGroupDraft.getId());
        if (updatedUserGroupBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.USERGROUP_NOT_EXISTED);
        }

        updatedUserGroupBasic.setUserGroupName(userGroupDraft.getUserGroupName());
        updatedUserGroupBasic.setUpdateTime(DateUtil.dateFormatToMillis(new Date()));

        alarmBiz.updateUserGroupInfoBasicById(updatedUserGroupBasic);
        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> deleteUserGroup(int id) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        UserGroupBasic userGroupBasic = alarmBiz.getUserGroupInfoBasicById(id);
        if (userGroupBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.USERGROUP_NOT_EXISTED);
        }

        alarmBiz.deleteUserGroupUserBindByUserGroupId(id);
        alarmBiz.deleteUserGroupInfoBasicById(id);
        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> bindUserList(int id, List<User> userList) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        if (userList == null) {
            throw ApiException.wrapMessage(ResultStat.USER_NOT_LEGAL, "user info list is null");
        }
        for (User userInfo : userList) {
            User user = authBiz.getUserById(userInfo.getId());
            if (user == null) {
                throw ApiException.wrapResultStat(ResultStat.USER_NOT_LEGAL);
            }
        }
        UserGroupBasic userGroupBasic = alarmBiz.getUserGroupInfoBasicById(id);
        if (userGroupBasic == null) {
            throw ApiException.wrapResultStat(ResultStat.USERGROUP_NOT_EXISTED);
        }

        for (User userInfo : userList) {
            int userId = userInfo.getId();
            if (alarmBiz.getUserGroupUserBindTime(id, userId) != null) {
            	
                alarmBiz.updateUserGroupUserBind(id, userId, DateUtil.dateFormat(new Date()));
            } else {
                alarmBiz.addUserGroupUserBind(id, userId, DateUtil.dateFormat(new Date()));
            }
        }

        return ResultStat.OK.wrap(null);
    }

    @Override
    public HttpResponseTemp<?> unbindUser(int id, int userId) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        if (alarmBiz.getUserGroupInfoBasicById(id) == null) {
            throw ApiException.wrapResultStat(ResultStat.USERGROUP_NOT_EXISTED);
        }
        if (authBiz.getUserById(userId) == null) {
            throw ApiException.wrapResultStat(ResultStat.USER_NOT_EXIST);
        }

        alarmBiz.deleteUserGroupUserBind(id, userId);
        return ResultStat.OK.wrap(null);
    }

    private class UserGroupDetailTask implements Callable<UserGroupDetail> {
        UserGroupBasic userGroupBasic;

        public UserGroupDetailTask(UserGroupBasic userGroupBasic) {
            this.userGroupBasic = userGroupBasic;
        }

        @Override
        public UserGroupDetail call() throws Exception {
            UserGroupDetail userGroupDetail = new UserGroupDetail(userGroupBasic);

            int userGroupId = userGroupBasic.getId();
            List<User> userList = alarmBiz.getUserInfoByUserGroupId(userGroupId);
            userGroupDetail.setUserList(userList);
            List<TemplateInfoBasic> templateInfoBasicList = alarmBiz.getTemplateInfoBasicByUserGroupId(userGroupId);
            userGroupDetail.setTemplateList(templateInfoBasicList);

            return userGroupDetail;
        }
    }
}
