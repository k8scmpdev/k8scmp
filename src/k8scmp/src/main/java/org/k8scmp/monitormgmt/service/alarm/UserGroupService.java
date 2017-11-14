package org.k8scmp.monitormgmt.service.alarm;


import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.login.domain.User;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupDraft;

/**
 * Created by KaiRen on 2016/9/27.
 */
public interface UserGroupService {

    /**
     *
     * @return
     */
    HttpResponseTemp<?> listUserGroupInfo();

    /**
     *
     * @param userGroupDraft
     * @return
     */
    HttpResponseTemp<?> createUserGroup(UserGroupDraft userGroupDraft);

    /**
     *
     * @param userGroupDraft
     * @return
     */
    HttpResponseTemp<?> modifyUserGroup(UserGroupDraft userGroupDraft);

    /**
     *
     * @param id
     * @return
     */
    HttpResponseTemp<?> deleteUserGroup(int id);

    /**
     *
     * @param id
     * @param userInfoList
     * @return
     */
    HttpResponseTemp<?> bindUserList(int id, List<User> userList);

    /**
     *
     * @param id
     * @param userId
     * @return
     */
    HttpResponseTemp<?> unbindUser(int id, int userId);
}
