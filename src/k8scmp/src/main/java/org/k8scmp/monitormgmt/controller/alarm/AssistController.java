package org.k8scmp.monitormgmt.controller.alarm;

import org.k8scmp.common.ApiController;
import org.k8scmp.monitormgmt.domain.alarm.asist.ActionWrap;
import org.k8scmp.monitormgmt.domain.alarm.asist.UserWrap;
import org.k8scmp.monitormgmt.service.alarm.AssistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baokangwang on 2016/4/14.
 */
@Controller
@RequestMapping("/api")
public class AssistController extends ApiController {

    @Autowired
    AssistService assistService;

    @ResponseBody
    @RequestMapping(value = "/alarm/action/wrap/{actionId}", method = RequestMethod.GET)
    public ActionWrap getActionById(@PathVariable long actionId) {
        return assistService.getActionById(actionId);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/group/users/wrap", method = RequestMethod.GET)
    public UserWrap getUsers(@RequestParam(value = "group", required = false) String group) {
        return assistService.getUsers(group);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/link/store", method = RequestMethod.POST)
    public String storeLink(@RequestBody String content) {
        return assistService.storeLink(content);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/link/{linkId}", method = RequestMethod.GET)
    public String retrieveLink(@PathVariable int linkId) {
        return assistService.retrieveLink(linkId);
    }
}
