package org.k8scmp.monitormgmt.service.alarm.impl;

import org.k8scmp.login.dao.AuthBiz;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.related.UserInfo;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.dao.alarm.PortalDao;
import org.k8scmp.monitormgmt.domain.alarm.asist.ActionWrap;
import org.k8scmp.monitormgmt.domain.alarm.asist.Link;
import org.k8scmp.monitormgmt.domain.alarm.asist.UserWrap;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Action;
import org.k8scmp.monitormgmt.service.alarm.AssistService;
import org.k8scmp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baokangwang on 2016/4/14.
 */
@Service
public class AssistServiceImpl implements AssistService {

    private static Logger logger = LoggerFactory.getLogger(AssistServiceImpl.class);

    @Autowired
    PortalDao portalBiz;

    @Autowired
    AlarmDao alarmBiz;

    @Autowired
    AuthBiz authBiz;

    @Override
    public ActionWrap getActionById(long actionId) {

        Action action = portalBiz.getActionById(actionId);
        if (action == null) {
            logger.info("error from alarm : no such action : " + actionId);
            return new ActionWrap("no such action", null);
        }

        return new ActionWrap("", action);
    }

    @Override
    public UserWrap getUsers(String group) {

        if (StringUtils.isBlank(group)) {
            return new UserWrap("team is blank", null);
        }


//        UserGroupBasic selectedGroup = alarmBiz.getUserGroupInfoBasicByName(group);
//        if (selectedGroup == null) {
//            return new UserWrap("", new ArrayList<User>());
//        }
//        List<UserInfo> userInfoList = alarmBiz.getUserInfoByUserGroupId(selectedGroup.getId());
//        if (userInfoList == null) {
//            return new UserWrap("", new ArrayList<User>());
//        }
//        List<User> users = new ArrayList<>(userInfoList.size());
//        for (UserInfo userInfo : userInfoList) {
//            if (userInfo == null) {
//                continue;
//            }
//            users.add(new User(userInfo.getUsername(), userInfo.getEmail(), userInfo.getPhone()));
//        }
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("admin");
        user.setEmail("");
        user.setPhone("");
        users.add(user);
        return new UserWrap("", users);
    }

    @Override
    public String storeLink(String content) {

        Link link = new Link();
        link.setContent(content);
        alarmBiz.addLink(link);
        return String.valueOf(link.getId());
    }

    @Override
    public String retrieveLink(int linkId) {

        Link link = alarmBiz.getLinkById(linkId);
        if (link == null) {
            return null;
        }
        return link.getContent();
    }
}
