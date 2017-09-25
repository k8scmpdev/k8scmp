package org.k8scmp.monitormgmt.service.alarm.impl;

import java.net.HttpURLConnection;
import java.net.URL;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.exception.ApiException;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.monitormgmt.dao.alarm.AlarmDao;
import org.k8scmp.monitormgmt.service.alarm.AlarmEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by baokangwang on 2016/4/13.
 */
@Service
public class AlarmEventServiceImpl implements AlarmEventService {

    private static Logger logger = LoggerFactory.getLogger(AlarmEventServiceImpl.class);

    private final ResourceType resourceType = ResourceType.ALARM;

    @Autowired
    AlarmDao alarmDao;

    @Autowired
    GlobalBiz globalBiz;

//    @Autowired
//    ClusterBiz clusterBiz;
//
//    @Autowired
//    InstanceService instanceService;
//
//    @Autowired
//    DeployAlarmPortalManager deployAlarmPortalManager;

    /*@Override
    public HttpResponseTemp<?> listAlarmEventInfo() {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.GET, 0);

        List<AlarmEventInfoDraft> alarmEventInfoDrafts = alarmDao.listAlarmEventInfoDraft();
        if (alarmEventInfoDrafts == null) {
            return ResultStat.OK.wrap(null);
        }

        List<AlarmEventInfoTask> alarmEventInfoTasks = new LinkedList<>();
        for (AlarmEventInfoDraft alarmEventInfoDraft : alarmEventInfoDrafts) {
            alarmEventInfoTasks.add(new AlarmEventInfoTask(alarmEventInfoDraft));
        }
        List<AlarmEventInfo> alarmEventInfos = ClientConfigure.executeCompletionService(alarmEventInfoTasks);
        Collections.sort(alarmEventInfos, new AlarmEventInfo.AlarmEventInfoComparator());
        return ResultStat.OK.wrap(alarmEventInfos);
    }*/

    @Override
    public HttpResponseTemp<?> ignoreAlarms(String alarmString) {

//        AuthUtil.collectionVerify(CurrentThreadInfo.getUserId(), GlobalConstant.alarmGroupId, resourceType, OperationType.MODIFY, 0);

        GlobalInfo alarmInfo = globalBiz.getGlobalInfoByType(GlobalType.MONITOR_ALARM);
        if (alarmInfo == null) {
            throw ApiException.wrapMessage(ResultStat.MONITOR_DATA_ALARM_ERROR, "alarm is null");
        }
        String alarmUrl = "http://" + alarmInfo.getValue() + "/event/solve?ids=" + alarmString;

        HttpURLConnection conn;
        try {
            URL url = new URL(alarmUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.error("error response code while post ignore alarms:" + responseCode);
                throw ApiException.wrapMessage(ResultStat.POST_ALARM_ERROR, "responseCode:" + String.valueOf(responseCode));
            }
        } catch (Exception e) {

            logger.error("exception in sending post request! ", e);
            throw ApiException.wrapMessage(ResultStat.POST_ALARM_ERROR, "exception in sending post request! " + e.getMessage());
        }
        return ResultStat.OK.wrap(null);
    }

    @Override
    public void ignoreAlarmsInside(String alarmString) {

        GlobalInfo alarmInfo = globalBiz.getGlobalInfoByType(GlobalType.MONITOR_ALARM);
        if (alarmInfo == null) {
            logger.error("ignore alarms inside error : alarm is null");
            return;
        }
        String alarmUrl = "http://" + alarmInfo.getValue() + "/event/solve?ids=" + alarmString;

        HttpURLConnection conn;
        try {
            URL url = new URL(alarmUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.error("ignore alarms inside error : error response code while post ignore alarms:" + responseCode);
            }
        } catch (Exception e) {
            logger.error("ignore alarms inside error : exception in sending post request! ", e);
        }
    }

    // notice: template or host group can be deleted while alarm event still reserved
    /*private class AlarmEventInfoTask implements Callable<AlarmEventInfo> {
        AlarmEventInfoDraft alarmEventInfoDraft;

        public AlarmEventInfoTask(AlarmEventInfoDraft alarmEventInfoDraft) {
            this.alarmEventInfoDraft = alarmEventInfoDraft;
        }

        @Override
        public AlarmEventInfo call() throws Exception {

            if (convertMetricByCounter(alarmEventInfoDraft.getCounter()) == null) {
                return null;
            }

            AlarmEventInfo alarmEventInfo = new AlarmEventInfo();

            alarmEventInfo.setId(alarmEventInfoDraft.getId());
            if (alarmEventInfoDraft.getMetric().startsWith("container")) {
                alarmEventInfo.setTemplateType(TemplateType.deploy.name());
            } else {
                alarmEventInfo.setTemplateType(TemplateType.host.name());
            }

            if (alarmEventInfo.getTemplateType().equals(TemplateType.host.name())) {
                alarmEventInfo.setHostInfo(alarmDao.getHostInfoByHostname(alarmEventInfoDraft.getEndpoint()));
            } else if (alarmEventInfo.getTemplateType().equals(TemplateType.deploy.name())) {
                DeploymentAlarmInfo deploymentAlarmInfo = new DeploymentAlarmInfo();
                deploymentAlarmInfo.setContainerId(convertContainerIdByCounter(alarmEventInfoDraft.getCounter()));
                Deployment deployment = alarmDao.getDeploymentByTemplateId(alarmEventInfoDraft.getTemplate_id());
                if (deployment == null) {
                    deploymentAlarmInfo.setId(0);
                    deploymentAlarmInfo.setDeploymentName("non-existed deployment");
                } else {
                    deploymentAlarmInfo.setId(deployment.getId());
                    Cluster cluster = clusterBiz.getClusterById(deployment.getClusterId());
                    if (cluster == null) {
                        deploymentAlarmInfo.setClusterName("non-existed cluster");
                    } else {
                        deploymentAlarmInfo.setClusterName(cluster.getName());
                    }
                    deploymentAlarmInfo.setDeploymentName(deployment.getName());
                    deploymentAlarmInfo.setNamespace(deployment.getNamespace());
                    deploymentAlarmInfo.setHostEnv(deployment.getHostEnv());
                    try {
                        List<Instance> instances = instanceService.getInstances(deployment.getId());
                        if (instances != null && instances.isEmpty()) {
                            for (Instance instance : instances) {
                                for (Container container : instance.getContainers()) {
                                    if (container.getContainerId().equals(deploymentAlarmInfo.getContainerId())) {
                                        deploymentAlarmInfo.setInstanceName(instance.getInstanceName());
                                        deploymentAlarmInfo.setInstanceHostIp(instance.getHostIp());
                                        deploymentAlarmInfo.setInstanceCreateTime(instance.getStartTime());
                                        break;
                                    }
                                }
                                if (deploymentAlarmInfo.getInstanceName() != null) {
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("get instances for deployment " + deployment.getName() + " error: " + e.getMessage());
                    }
                }
                alarmEventInfo.setDeploymentAlarmInfo(deploymentAlarmInfo);
            }

            alarmEventInfo.setMetric(convertMetricByCounter(alarmEventInfoDraft.getCounter()));
            alarmEventInfo.setTag(convertTagByCounter(alarmEventInfoDraft.getCounter()));
            alarmEventInfo.setLeftValue(Double.valueOf(alarmEventInfoDraft.getLeft_value()));
            alarmEventInfo.setOperator(alarmEventInfoDraft.getOperator());
            alarmEventInfo.setRightValue(Double.valueOf(alarmEventInfoDraft.getRight_value()));
            alarmEventInfo.setNote(alarmEventInfoDraft.getNote());
            alarmEventInfo.setCurrentStep(alarmEventInfoDraft.getCurrent_step());
            alarmEventInfo.setMaxStep(alarmEventInfoDraft.getMax_step());
            alarmEventInfo.setTimeStamp(alarmEventInfoDraft.getTimestamp() * 1000);

            return alarmEventInfo;
        }
    }*/

    private static String convertMetricByCounter(String counter) {

        String metricWithEndpoint = counter.substring(0, counter.indexOf(" "));

        switch (metricWithEndpoint.substring(metricWithEndpoint.indexOf("/") + 1)) {
            case "cpu.busy":
            case "container.cpu.usage.busy":
                return "cpu_percent";
            case "mem.memused.percent":
            case "container.mem.usage.percent":
                return "memory_percent";
            case "df.bytes.used.percent":
                return "disk_percent";
            case "disk.io.read_bytes":
                return "disk_read";
            case "disk.io.write_bytes":
                return "disk_write";
            case "net.if.in.bytes":
            case "container.net.if.in.bytes":
                return "network_in";
            case "net.if.out.bytes":
            case "container.net.if.out.bytes":
                return "network_out";
            case "agent.alive":
                return "agent_alive";
            default:
                return null;
        }
    }

    private static String convertTagByCounter(String counter) {

        String tags = counter.substring(counter.indexOf(" ") + 1);
        String[] tagPairs = tags.split(",");
        for (String tag : tagPairs) {
            if (tag.startsWith("mount=") || tag.startsWith("iface=") || tag.startsWith("device=")) {
                return tag;
            }
        }
        return null;
    }

    private static String convertContainerIdByCounter(String counter) {

        String tags = counter.substring(counter.indexOf(" ") + 1);
        String[] tagPairs = tags.split(",");
        for (String tag : tagPairs) {
            if (tag.startsWith("id=")) {
                return tag.substring(3);
            }
        }
        return null;
    }


}
