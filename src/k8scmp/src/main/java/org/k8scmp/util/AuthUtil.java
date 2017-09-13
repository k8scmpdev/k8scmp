package org.k8scmp.util;

import org.apache.shiro.SecurityUtils;
import org.k8scmp.login.dao.AuthBiz;
import org.k8scmp.login.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jaosn on 201/9/4.
 */
@Component
public class AuthUtil {


    private static AuthBiz authBiz;

    @Autowired
    public void setAuthBiz(AuthBiz authBiz) {
        AuthUtil.authBiz = authBiz;
    }


    public static boolean isAdmin(int userId) {
        return authBiz.isAdmin(userId);
    }

    public static String getCurrentLoginName() {
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * Get current user' id,
     *
     * @return userId or -1 if not aquired
     */
    public static int getUserId() {
        String loginName = getCurrentLoginName();
        return authBiz.getUserId(loginName);
    }

    public static User getUser() {
        String loginName = getCurrentLoginName();
        return authBiz.getUser(loginName);
    }
    
    public static String getUserName() {
    	String loginName = getCurrentLoginName();
        return authBiz.getUserName(loginName);
    }
    
    public static String getUserNameById (int id) {
        return authBiz.getUserNameById(id);
    }

//    /**
//     * Set resource ownerId and ownerType cause the resource may be created by user or by group
//     *
//     * @param resource     resource
//     * @param userId       user who submit the resource creation request
//     * @param type         resource type, USER or GROUP
//     * @param resourceName resourceName
//     */
//    public static void setResourceOwnerAndType(Resource resource, int userId,
//                                               OwnerType type, String resourceName) {
//        resource.setOwnerType(type);
//        if (type == OwnerType.USER) {
//            resource.setOwnerId(userId);
//        } else {
//            String groupName = ResourceUtil.getOwnerName(resourceName);
//            Group group = authBiz.getGroupByName(groupName);
//            if (group != null) {
//                resource.setOwnerId(group.getId());
//            }
//        }
//    }




}