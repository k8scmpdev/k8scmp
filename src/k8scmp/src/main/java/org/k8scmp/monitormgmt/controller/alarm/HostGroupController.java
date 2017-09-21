package org.k8scmp.monitormgmt.controller.alarm;

import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.common.ApiController;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.HostInfo;
import org.k8scmp.monitormgmt.service.alarm.HostGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baokangwang on 2016/4/13.
 */
@Controller
@RequestMapping("/api")
public class HostGroupController extends ApiController {

    @Autowired
    HostGroupService hostGroupService;

    @ResponseBody
    @RequestMapping(value = "/alarm/hostgroup", method = RequestMethod.GET)
    public HttpResponseTemp<?> listHostGroupInfo() {
        return hostGroupService.listHostGroupInfo();
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/hostgroup", method = RequestMethod.POST)
    public HttpResponseTemp<?> createHostGroup(@RequestBody HostGroupInfoBasic hostGroupInfoBasic) {
        return hostGroupService.createHostGroup(hostGroupInfoBasic);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/hostgroup", method = RequestMethod.PUT)
    public HttpResponseTemp<?> modifyHostGroup(@RequestBody HostGroupInfoBasic hostGroupInfoBasic) {
        return hostGroupService.modifyHostGroup(hostGroupInfoBasic);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/hostgroup/{id}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteHostGroup(@PathVariable long id) {
        return hostGroupService.deleteHostGroup(id);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/hostgroup/bind/{id}", method = RequestMethod.POST)
    public HttpResponseTemp<?> bindHostList(@PathVariable long id, @RequestBody List<HostInfo> hostInfoList) {
        return hostGroupService.bindHostList(id, hostInfoList);
    }

    @ResponseBody
    @RequestMapping(value = "/alarm/hostgroup/bind/{id}/{hostId}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> unbindHost(@PathVariable long id, @PathVariable long hostId) {
        return hostGroupService.unbindHost(id, hostId);
    }

}
