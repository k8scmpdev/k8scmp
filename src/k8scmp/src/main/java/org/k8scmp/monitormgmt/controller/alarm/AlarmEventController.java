package org.k8scmp.monitormgmt.controller.alarm;

import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.common.ApiController;
import org.k8scmp.monitormgmt.domain.alarm.AlarmEventInfo;
import org.k8scmp.monitormgmt.service.alarm.AlarmEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baokangwang on 2016/4/14.
 */
@Controller
@RequestMapping("/alarmEvent")
public class AlarmEventController extends ApiController {

    @Autowired
    AlarmEventService alarmEventService;
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listAlarmEventInfo(Model model) {
    	List<AlarmEventInfo> alarmEventList = (List<AlarmEventInfo>)listAlarmEventInfo().getResult();
    	model.addAttribute("total", alarmEventList.size());
    	model.addAttribute("alarmEventList", alarmEventList);
        return "alarm/alarm-event";
    }
    
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public List<AlarmEventInfo> searchHostGroupInfo(@RequestParam("eventName") String eventName) {
        return alarmEventService.searchAlarmEvent(eventName);
    }
    
    @ResponseBody
    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public HttpResponseTemp<?> listAlarmEventInfo() {
        return alarmEventService.listAlarmEventInfo();
    }

    @ResponseBody
    @RequestMapping(value = "/event/ignore", method = RequestMethod.POST)
    public HttpResponseTemp<?> ignoreAlarms(@RequestBody String alarmString) {
        return alarmEventService.ignoreAlarms(alarmString);
    }
}
