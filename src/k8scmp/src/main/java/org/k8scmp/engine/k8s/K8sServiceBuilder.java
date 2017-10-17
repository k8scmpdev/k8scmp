package org.k8scmp.engine.k8s;

import io.fabric8.kubernetes.api.model.*;


import java.util.ArrayList;
import java.util.List;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.NodePortDraft;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.common.GlobalConstant;

/**
 */
public class K8sServiceBuilder {
	private AppInfo appInfo;
	private ServiceConfigInfo serviceConfigInfo;

	public K8sServiceBuilder(ServiceConfigInfo serviceConfigInfo,AppInfo appInfo) {
		this.serviceConfigInfo = serviceConfigInfo;
		this.appInfo = appInfo;
	}

	public Service build(){
		return buildService();
	}
	
	private Service buildService() {
		List<NodePortDraft> nodePorts = serviceConfigInfo.getNodePorts();
    	if(!serviceConfigInfo.isExternal() || nodePorts==null || nodePorts.size()==0){
    		return null;
    	}
    	
		Service service = new ServiceBuilder().withNewMetadata()
				.withName(GlobalConstant.RC_NAME_PREFIX + serviceConfigInfo.getServiceCode())
				.withNamespace(appInfo.getNamespace()).endMetadata().build();
		// init serivce spec
		ServiceSpec spec = new ServiceSpec();
		List<ServicePort> servicePorts = new ArrayList<>(nodePorts.size());
		for (NodePortDraft port : nodePorts) {
			ServicePort servicePort = new ServicePortBuilder().withProtocol(port.getProtocol().name())
					.withPort(port.getTargetPort()).withTargetPort(new IntOrString(port.getTargetPort()))
					.withNodePort(port.getNodePort())
					.withName("port" + port.getNodePort()).build();

			servicePorts.add(servicePort);
		}
		spec.setPorts(servicePorts);
		spec.setType(GlobalConstant.NODE_PORT_STR);
		spec.setSelector(new K8sLabel(GlobalConstant.DEPLOY_ID_STR,
				String.valueOf(serviceConfigInfo.getId())));
		service.setSpec(spec);
		return service;
	}
	
//    private List<NodePortDraft> loadBalancer;
//    public K8sServiceBuilder(LoadBalancer loadBalancer) {
//        this.loadBalancer = loadBalancer;
//    }
//    public Service build() {
//        if (loadBalancer.getType() == LoadBalancerType.INNER_SERVICE ||
//            loadBalancer.getType() == LoadBalancerType.EXTERNAL_SERVICE) {
//            return buildService();
//        } else {
//            return null;
//        }
//    }
//    private Service buildService() {
//        Service service = new io.fabric8.kubernetes.api.model.ServiceBuilder()
//                .withNewMetadata()
//                .withName(GlobalConstant.RC_NAME_PREFIX + loadBalancer.getName())
//                .withNamespace(loadBalancer.getNamespace())
//                .endMetadata()
//                .build();
//        // init serivce spec
//        ServiceSpec spec = new ServiceSpec();
//        if (loadBalancer.getType() == LoadBalancerType.EXTERNAL_SERVICE) {
//            List<String> ips = loadBalancer.getExternalIPs();
//            spec.setExternalIPs(ips);
//        }
//        List<LoadBalancerPort> lbPorts = loadBalancer.getServiceDraft().getLbPorts();
//        List<ServicePort> servicePorts = new ArrayList<>(lbPorts.size());
//        for (LoadBalancerPort port : lbPorts) {
//            ServicePort servicePort = new ServicePortBuilder()
//                    .withProtocol(port.getProtocol().name())
//                    .withPort(port.getPort())
//                    .withTargetPort(new IntOrString(port.getTargetPort()))
//                    .withName("port" + port.getPort())
//                    .build();
//
//            servicePorts.add(servicePort);
//        }
//        spec.setPorts(servicePorts);
//        if (loadBalancer.getType() == LoadBalancerType.EXTERNAL_SERVICE) {
//            spec.setType(GlobalConstant.NODE_PORT_STR);
//        } else if (loadBalancer.getType() == LoadBalancerType.INNER_SERVICE) {
//            spec.setType(GlobalConstant.CLUSTER_IP_STR);
//        }
//        if(loadBalancer.getServiceDraft().isSessionAffinity()){
//            spec.setSessionAffinity("ClientIP");
//        }
//        spec.setSelector(new K8sLabel(GlobalConstant.DEPLOY_ID_STR,
//             String.valueOf(loadBalancer.getServiceDraft().getDeployId())));
//        service.setSpec(spec);
//        return service;
//    }

}