package org.k8scmp.monitormgmt.controller.alarm;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ApiController;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.related.UserInfo;
import org.k8scmp.login.service.UserService;
import org.k8scmp.mapper.login.UserMapper;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfo;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupDetail;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupDraft;
import org.k8scmp.monitormgmt.service.alarm.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KaiRen on 2016/9/27.
 */
@Controller
@RequestMapping("/api")
public class UserGroupController extends ApiController {
    @Autowired
    UserGroupService userGroupService;
    @Autowired
    AlarmDao alarmDao;
    @Autowired
    UserMapper userMapper;
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listUserGroup() {
    	List<UserGroupDetail> userGroupList = (List<UserGroupDetail>)listUserGroupInfo().getResult();
        return new ModelAndView("alarm/alarm-userGroup", "userGroupList", userGroupList);
    }
    
    @SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public HttpResponseTemp<?> searchUserGroup(@RequestParam("userGroupName") String userGroupName){
    	List<UserGroupDetail> userGroups = (List<UserGroupDetail>)listUserGroupInfo().getResult();
    	List<UserGroupDetail> userGroupList = new ArrayList<>();
    	for (UserGroupDetail userGroupDetail : userGroups) {
			if(userGroupDetail != null && !"".equals(userGroupDetail)){
				if(userGroupDetail.getUserGroupName().indexOf(userGroupName) >= 0){
					userGroupList.add(userGroupDetail);
				}
			}
		}
    	return ResultStat.OK.wrap(userGroupList);
    }
    
    @ResponseBody
    @RequestMapping(value = "/alarm/usergroup", method = RequestMethod.GET)
    public HttpResponseTemp<?> listUserGroupInfo() {
        return userGroupService.listUserGroupInfo();
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/usergroup", method = RequestMethod.POST)
    public HttpResponseTemp<?> createUserGroup(@RequestBody UserGroupDraft userGroupDraft) {
        return userGroupService.createUserGroup(userGroupDraft);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/usergroup", method = RequestMethod.PUT)
    public HttpResponseTemp<?> modifyUserGroup(@RequestBody UserGroupDraft userGroupDraft) {
        return userGroupService.modifyUserGroup(userGroupDraft);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/usergroup/{id}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteUserGroup(@PathVariable int id) {
        return userGroupService.deleteUserGroup(id);
    }
    
    @RequestMapping(value = "/addUsers/{id}", method = RequestMethod.GET)
    public String addUsers(Model model, @PathVariable int id){
    	List<User> allUsers = userMapper.listAllUserInfo();
    	List<User> userInfoList = alarmDao.getUserInfoByUserGroupId(id);
    	List<String> userNames = new ArrayList<>();
    	for (User userInfo : userInfoList) {
    		userNames.add(userInfo.getUsername());
		}
    	model.addAttribute("userNames", userNames);
    	model.addAttribute("allUsers", allUsers);
    	model.addAttribute("userInfoList", userInfoList);
    	model.addAttribute("userGroupId", id);
    	return "alarm/userGroup-addUsers";
    }
    
    @ResponseBody
    @RequestMapping(value = "/alarm/usergroup/bind/{id}", method = RequestMethod.POST)
    public HttpResponseTemp<?> bindUserList(@PathVariable int id, @RequestBody List<User> userList) {
        return userGroupService.bindUserList(id, userList);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/usergroup/bind/{id}/{userId}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> unbindUser(@PathVariable int id, @PathVariable int userId) {
        return userGroupService.unbindUser(id, userId);
    }
}
