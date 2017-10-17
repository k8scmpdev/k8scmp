package k8scmp.deploy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.k8scmp.Application;
import org.k8scmp.appmgmt.domain.ContainerDraft;
import org.k8scmp.appmgmt.domain.LabelSelector;
import org.k8scmp.appmgmt.domain.NodePortDraft;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.service.ServiceService;
import org.k8scmp.model.ImagePullPolicy;
import org.k8scmp.model.LoadBalancerProtocol;
import org.k8scmp.model.ServiceStatus;
import org.k8scmp.model.VersionType;
import org.k8scmp.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ServiceServiceTest {
	
	@Autowired
	private ServiceService serviceService;
	
	
	@Test
	public void buildService(){
//		//构造 app
//		
//		AppInfo app = new AppInfo();
//		app.setId("0");
		
		//构造service
		ServiceConfigInfo sc = new ServiceConfigInfo();
		sc.setAppId("0");
		sc.setServiceCode("ss");
		sc.setDescription("");
		sc.setStartSeq(0);
		sc.setState(ServiceStatus.STOP.name());
		sc.setCreateTime(DateUtil.dateFormatToMillis(new Date()));
		sc.setCreatorId("admin");
		sc.setDefaultReplicas(1);
		sc.setVersionType(VersionType.CUSTOM);
		sc.setExternal(true);
		
		
		List<NodePortDraft> nodePorts = new ArrayList<>();
		NodePortDraft nd = new NodePortDraft();
		nd.setTargetPort(3306);
		nd.setNodePort(33306);
		nd.setProtocol(LoadBalancerProtocol.TCP);
		nodePorts.add(nd);
		
		sc.setNodePorts(nodePorts);
		
		//构造version
		List<Version> vs = new ArrayList<>();
		
		Version ver = new Version();
		ver.setVersionType(VersionType.CUSTOM);
		ver.setDescription("");
		ver.setState("");
		ver.setVersion(1);
		
//		List<LabelSelector> labelSelectors = new ArrayList<>();
//		LabelSelector ls = new LabelSelector();
//		ls.setName("host1");
//		ls.setContent("USER_LABEL_VALUE");
//		labelSelectors.add(ls);
		
//		ver.setLabelSelectors(labelSelectors);
		
		
		
		List<ContainerDraft> containerDrafts = new ArrayList<>();
		ContainerDraft cd = new ContainerDraft();
//		cd.setRegistry("index.docker.io/library");
		cd.setImage("mysql");
		cd.setTag("latest");
		cd.setCpu(0.2);
		cd.setMem(512);
//		cd.setCpuRequest(0);
//		cd.setMemRequest(0);
		cd.setImagePullPolicy(ImagePullPolicy.IfNotPresent);
		containerDrafts.add(cd);
		
		ver.setContainerDrafts(containerDrafts);
		
		vs.add(ver);
		
		ServiceDetail sd = new ServiceDetail();
		
		sd.setServiceConfigInfo(sc);
		sd.setVersions(vs);
		
		serviceId = serviceService.createService(sd);
		System.out.println(serviceId);
	}

	String serviceId = "c3543dc6f8f3441ab344fcf3ab6ab0e0";
	@Test
	public void deleteService() throws Exception{
		serviceService.deleteService(serviceId);
	}
	
	@Test
	public void startService() throws Exception{
		serviceService.startService(serviceId, 1, 1);
	}
	
	@Test
	public void createLoadBalancer() throws Exception{
		List<NodePortDraft> nodePorts = new ArrayList<>();
		NodePortDraft nd = new NodePortDraft();
		nd.setTargetPort(3306);
		nd.setNodePort(30306);
		nd.setProtocol(LoadBalancerProtocol.TCP);
		nodePorts.add(nd);
		
		serviceService.createLoadBalancer(serviceId,nodePorts);
	}
}
