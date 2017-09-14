package org.k8scmp.monitormgmt.service.alarm;

import org.k8scmp.basemodel.HttpResponseTemp;

/**
 * Created by baokangwang on 2016/4/13.
 */
public interface AlarmEventService {

    /**
     *
     * @return
     */
//    HttpResponseTemp<?> listAlarmEventInfo();

    /**
     *
     * @param alarmString a list of alarm event id separated by two commas(,,)
     * @return
     */
    HttpResponseTemp<?> ignoreAlarms(String alarmString);

    /**
     *
     * @param alarmString a list of alarm event id separated by two commas(,,)
     */
    void ignoreAlarmsInside(String alarmString);
}
