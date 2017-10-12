package org.k8scmp.monitormgmt.controller.alarm;

import java.util.ArrayList;
import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ApiController;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.HostInfo;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;
import org.k8scmp.monitormgmt.service.alarm.HostGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by baokangwang on 2016/4/13.
 */
@Controller
@RequestMapping("/hostgroup")
public class HostGroupController extends ApiController {

    @Autowired
    HostGroupService hostGroupService;
    
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listHostGroupInfo() {
    	List<HostGroupInfo> hostGroupInfoList = hostGroupService.listHostGroupInfo();
        return new ModelAndView("alarm/alarm-hostgroup", "hostGroupInfoList", hostGroupInfoList);
    }
    
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public HttpResponseTemp<?> searchHostGroupInfo(@RequestParam("hostGroupName") String hostGroupName) {
        return hostGroupService.searchHostGroupInfo("%"+hostGroupName+"%");
    }
    
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public HttpResponseTemp<?> createHostGroup(@RequestBody String hostGroupName) {
    	String[] split = hostGroupName.split("=");
    	hostGroupName = split[1];
    	if(hostGroupName == null){
    		return null;
    	}
    	HostGroupInfoBasic hostGroupInfoBasic = new HostGroupInfoBasic();
    	hostGroupInfoBasic.setHostGroupName(hostGroupName);
        return hostGroupService.createHostGroup(hostGroupInfoBasic);
    }

    @ResponseBody
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public HttpResponseTemp<?> modifyHostGroup(@RequestBody HostGroupInfoBasic hostGroupInfoBasic) {
        return hostGroupService.modifyHostGroup(hostGroupInfoBasic);
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteHostGroup(@PathVariable String id) {
    	int ID = Integer.parseInt(id);
        return hostGroupService.deleteHostGroup(ID);
    }
    
    @ResponseBody
    @RequestMapping(value = "/bindHostGroup", method = RequestMethod.GET)
    public ModelAndView bindHostGroup() {
    	//获取主机列表
    	List<NodeInfo> nodeList = hostGroupService.getNodeList();
        return new ModelAndView("alarm/hostGroup-bind", "nodeList", nodeList);
    }
    
    @ResponseBody
    @RequestMapping(value = "/bind/{id}", method = RequestMethod.POST)
    public HttpResponseTemp<?> bindHostList(@PathVariable String id, @RequestBody List<HostInfo> hostInfoList) {
    	int ID = Integer.parseInt(id);
        return hostGroupService.bindHostList(ID, hostInfoList);
    }

    @ResponseBody
    @RequestMapping(value = "/bind/{id}/{hostId}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> unbindHost(@PathVariable String id, @PathVariable String hostId) {
    	int ID = Integer.parseInt(id);
    	int hostID = Integer.parseInt(hostId);
        return hostGroupService.unbindHost(ID, hostID);
    }

}
