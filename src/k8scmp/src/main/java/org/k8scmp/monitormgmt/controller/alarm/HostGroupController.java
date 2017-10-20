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
    

    @RequestMapping(value = "/bindHostGroup", method = RequestMethod.GET)
    public String bindHostGroup(Model model,@RequestParam("id") int id) {
    	//获取主机列表
//    	List<HostInfo> nodeList = hostGroupService.getHostInfo();
    	List<HostInfo> nodeList = hostGroupService.getHostList();
    	List<HostInfo> bindList = hostGroupService.getHostBindList(id);
    	model.addAttribute("nodeList", nodeList);
    	model.addAttribute("bindList", bindList);
    	model.addAttribute("hostGroupId", id);
        return "alarm/hostGroup-bind";
    }
    
    @ResponseBody
    @RequestMapping(value = "/bind/{id}", method = RequestMethod.POST)
    public HttpResponseTemp<?> bindHostList(@PathVariable int id, @RequestBody List<HostInfo> hostInfoList) {
        return hostGroupService.bindHostList(id, hostInfoList);
    }

    @ResponseBody
    @RequestMapping(value = "/bind/{id}/{hostId}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> unbindHost(@PathVariable int id, @PathVariable int hostId) {
        return hostGroupService.unbindHost(id, hostId);
    }

}
