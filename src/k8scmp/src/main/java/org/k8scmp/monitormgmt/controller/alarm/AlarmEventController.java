package org.k8scmp.monitormgmt.controller.alarm;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.common.ApiController;
import org.k8scmp.monitormgmt.service.alarm.AlarmEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baokangwang on 2016/4/14.
 */
@Controller
@RequestMapping("/alarm")
public class AlarmEventController extends ApiController {

    @Autowired
    AlarmEventService alarmEventService;

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
