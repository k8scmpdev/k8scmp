function showPortMapped(){
    var childdiv=$('<div></div>');
	//childdiv.attr("id","id" + new Date().getTime());
    childdiv.attr("class","newPort");
	childdiv.load("/js/statichtml/app/portMappedTemplate.html #portMappedTemplate");
	$("#newPortMapped").append(childdiv);
	$("#saveData").show();
}

$(".radioItem").change(function(event){
	var selectValue=$("span[class='checked']").children("input:radio[name='publishRadios']").val();
	if(selectValue == 1){
		$(".portMappes").css("display","block");
	}else if(selectValue == 2){
		$(".portMappes").css("display","none");
	}
});

var oldContainers = []; 

var volumeDrafts = [];
var volumeDraftsMap = {};

var containerDrafts = [];
var currentContainerMap = {};
var serviceId = $('#serviceID').val();
$(document).ready(function(){
	$.ajax({
		type: "GET",
		dataType: "json",
		url: "/app/version/getVersionNames?serviceId="+$('#serviceID').val(),
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				var versionList = data.result;
				
				$.ajax({
					type: "GET",
					dataType: "json",
					url: "/app/service/getCurrentVersion?serviceId="+$('#serviceID').val(),
					contentType:"application/json",
					success: function (data) {
						if(data.resultCode == 200){
							var currenttVersion = data.result;
							if(currenttVersion!=null){
								console.log(currenttVersion);
								versionDetail = getContainerVolumes(currenttVersion);
								loadsVersionDetail(versionDetail);
								createVersionSelect(currenttVersion.version,versionList);
							}
						}else{
							alert("获取当前版本详情失败!");
						}
					},
					error: function(data) {
						alert("获取当前版本详情失败!");
					}
				});
				
				
			}else{
				alert("获取版本列表失败!");
			}
		},
		error: function(data) {
			alert("获取版本列表失败!");
		}
	});
	
	var oTable=$('.storage-table').dataTable({
		"scrollY": "160px",
		"scrollCollapse": "true",
		"pagingType":"full_numbers",
		"paging": "true",
		"lengthMenu":[5],
		"language":	{
			"search":"搜索",
	        "lengthMenu": "每页 _MENU_ 条记录",
	        "zeroRecords": "没有找到记录",
	        "info": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
	        "infoEmpty": "无记录",
	        "infoFiltered": "(从 _MAX_ 条记录过滤)",
	        "paginate": {
	            "first": "首页",
	            "previous": "上页",
	            "next": "下页",
	            "last": "末页"
	        }
	    },
	    "fnDrawCallback":function(oSettings){
	    	$(".dataTables_length").offset({top:$(".dataTables_info").offset().top+6});
	    }
	});
	
	$.ajax({
		type: "GET",
		dataType: "json",
		url: "/app/version/getMaxVersion?serviceId="+serviceId,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				var newestVersion = data.result;
				if(newestVersion!=null){
					oldContainers = getContainerVolumes(newestVersion);
					loadsUpgradeDatas(oldContainers);
				}
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	//init service info common data
	/**init**/
	initReplicasByServiceId(serviceId);//replies
	initCurrentVersionNum(serviceId);//current version number
	initServiceAddress(serviceId);//service urls
	
	//add onchange event on input
	$("#portMapped >div").find("input,select").bind("change",function(){
		$("#saveData").show();
	});
});

//init port mapped
function loadNodePorts(hiddenNodePorts,portLength){
	var childdiv=$('<div></div>');  
	childdiv.attr("id","id" + new Date().getTime());
	$("#portMapped").append(childdiv);
	childdiv.load("/js/statichtml/app/portMappedTemplate.html #portDisplay",function(responseTxt,statusTxt,xhr){
		var nodePortItem = hiddenNodePorts[portLength];
		//set value
		$(this).find("input[name='iNodePort']").val(nodePortItem["nodePort"]);
		$(this).find("input[name='iTargetPort']").val(nodePortItem["targetPort"]);
		//set select2 select value
		var iprotocal = $(this).find("select[name='iProtocol']").select2();
		iprotocal.val(nodePortItem["protocol"]).trigger("change");
		$(this).find("input[name='description']").val(nodePortItem["description"]);
		portLength--;
		if(portLength>=0){
			loadNodePorts(hiddenNodePorts,portLength);
		}
	});
}

//create load balancer
/*$("#btnnew2pre").bind("click",function(event){
	var nodePortItem = {};
	//show port mapped detail into sPort tab
	//save ports
	$("#portMapped >div").each(function(){
		$("#showLoadBalancer").css("display","none");
		var nodePortItem = {};
		
		*//****show port mapped detail into sPort tab***//*
		var nodePort = $("input[name='nodePort']").val();
		var targetPort = $("input[name='targetPort']").val();
		var protocol = $("select[name='protocol']").val();
		var description = $("input[name='description']").val();
		var isExternal = true;
		
		
		
		//load html
		var childdiv=$('<div></div>');  
		childdiv.attr("id","id" + new Date().getTime());
		childdiv.load("/js/statichtml/app/portMappedTemplate.html #portDisplay",function(responseTxt,statusTxt,xhr){
			//set value
			$(this).find("span[name='iNodePort']").html(nodePort);
			$(this).find("span[name='iTargetPort']").html(targetPort);
			$(this).find("span[name='iProtocol']").html(protocol);
			$(this).find("span[name='description']").html(description);
		});
		
		//package value going to save into db
		nodePortItem["nodePort"] = nodePort;
		nodePortItem["targetPort"] = targetPort;
		nodePortItem["protocol"] = protocol;
		nodePortItem["description"] = description;
		
	});
	
	//save into db
	var ajaxUrl = "/app/service/createLoadBalancer?serviceId="+serviceId;
	var paramData = nodePorts;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: ajaxUrl,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			//do some notification
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	//close this window
	$("#sPortMappedModel").modal("hide");
});*/

//get set replies when document ready
function initReplicasByServiceId(serviceId){
	var returnCount = 0;
	var AjaxURL= "/app/service/getReplicasByServiceId?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				returnCount = data.result;
				$("#repliesCount").html(returnCount==null?"":returnCount);//replies
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//get set current version num when document ready
function initCurrentVersionNum(serviceId){
	var returnNumber = 0;
	var AjaxURL= "/app/service/getCurrentVersionNum?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				returnNumber = data.result;
				$("#currentVersionNumber").html(returnNumber==null?"":returnNumber);
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//get set service address when document ready
function initServiceAddress(serviceId){
	var serviceUrls = [];
	var AjaxURL= "/app/service/getServiceURLs?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				serviceUrls = data.result;
				$("#serviceUrls").html(serviceUrls==null?"":serviceUrls.join(","));//service urls
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
}

function loadsUpgradeDatas(containers){
	var htmlContent = "";
	if($("#tBody1").children("tr").children("td[class='dataTables_empty']").length){
		//empty tables
		$("#tBody1").html("");
	}
	
	htmlContent = $("#tBody1").html();
	if(containers!=null){
		for(var i=0;i<containers.length;i++){
			var container = containers[i];
			var image = container["image"]==null?"":container["image"];
			var registry = container["registry"]==null?"":container["registry"];
			var tag = container["tag"]==null?"":container["tag"];
			var id = registry+image;
			//add new row to the images list table
			var trRow = "<tr>" +
					"<td class='i10' style='width: 30%;min-width:30%;max-width:30%;text-align: center;'>"+image+"</td>" +
					"<td class='i20' style='width: 30%;min-width:30%;max-width:30%;text-align: center;'>"+registry+"</td>" +
					"<td class='i30' style='width: 10%;min-width:10%;max-width:10%;text-align: center;'>"+tag+"</td>" +
					"<td class='i40' style='width: 30%;min-width:30%;max-width:30%;text-align: center;'><a title='配置' class='tip-bottom iconOperation' data-target='#service-new3' data-toggle='modal' onclick='showImageConfiguration(\""+id+"\");'><i class='icon-edit'></i><span>配置</span></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
					"<a title='删除' class='tip-bottom iconOperation' href='javascript:void(0);' onclick='deleteImage(this,\""+id+"\");'><i class='icon-trash'></i><span>删除</span></a></td>" +
					"</tr>";
			htmlContent = htmlContent.concat(trRow);
			currentContainerMap[id]= container;
		}
	}
	$("#tBody1").html(htmlContent);
}





//save choose image window value into a image map
$("#imagesbtn").bind("click",function(event){
	loadsUpgradeDatas(getSelectedRow())
	$("#myModal").modal("hide");
});

//get all checked rows
function getSelectedRow(){
	var returnObj = [];
	var tmpObj = {};
	var valid = true;
	$(".image-table span[class='checked'] input[type='checkbox']").each(function(){
		tmpObj = $(this).parent().parent().parent().parent().parent().parent();
		var tr = {};
		tr["image"] = tmpObj.children("td[id='id']").html()==null?"":tmpObj.children("td[id='id']").html();
		tr["registry"] = tmpObj.children("td[id='registry']").html()==null?"":tmpObj.children("td[id='registry']").html();
		tr["tag"] = tmpObj.children("td[id='versions']").children("select[name='versions']").val();
		
		if(currentContainerMap[tr["registry"]+tr["image"]]!=null){
			alert("镜像["+tr["image"]+"]已存在!");
			valid = false;
			return false;
		}
		returnObj.push(tr);
	});
	if(!valid){
		return null;
	}
	return returnObj;
}

//remove image tr from image list table
function deleteImage(obj,id){
	var tr=obj.parentNode.parentNode;
	var tbody=tr.parentNode;
	tbody.removeChild(tr);
	
	delete currentContainerMap[id];
}

function showImageConfiguration(id){
	console.log(currentContainerMap);
	var container = currentContainerMap[id];
	$("#storages").html("");//clear all sub divs
	
	/************************show storages*******************/
	//haved setted part containerdraft
	$("#uniqueImage").val(id);
	$("#registryConfig").val(container["registry"]);
	$("#imageConfig").val(container["image"]);
	$("#tagConfig").val(container["tag"]);
	
	if(container != null){
		volumeMountDrafts = container["volumeMountDrafts"];
		//曾经打开过该页面设过值
		if(volumeMountDrafts != null){
			loadStorages(volumeMountDrafts,volumeMountDrafts.length);
		}
		/*************show commands***********/
		$("#startCommands").val(container["commands"]==null?"":container["commands"].join(","));
		
		/*************show envs***********/
		$("#envvar").val(container["envs"]==null?"":getEnvsToString(container["envs"]));
		
		/*************show cpu***********/
		$("#cpus").val(container["cpu"]==null?0:container["cpu"]);

		/*************show mem***********/
		$("#mems").val(container["mem"]==null?0:container["mem"]);
	}
	
}

function loadStorages(volumeMountDrafts,seq){
	if(seq>0){
		var childdiv=$('<div></div>'); 
		$("#storages").append(childdiv);
		childdiv.load("/js/statichtml/app/storageTemplate.html  #storageTemplate",
				function(responseTxt,statusTxt,xhr){
			tmpDraft = volumeMountDrafts[seq-1];
			//set value
			if(tmpDraft["volumeType"] == "EMPTYDIR"){
				$(this).find("select[name='dirs']").find("option[value='EMPTYDIR']").attr("selected","selected");
				$(this).find("select[name='writeOrRead']").find("option[value='"+tmpDraft["readOnly"]+"']").attr("selected","selected");
				$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
				$(this).find("input[name='insPath']").val(tmpDraft["mountPath"]);
				
				//hide hostpath and show emptydirs info
				$(this).find("input[name='mountPath']").css("display","none");
				$(this).find("input[name='hostPath']").css("display","none");
				$(this).find("input[name='insPath']").css("display","");
			}else if(tmpDraft["volumeType"] == "HOSTPATH"){
				$(this).find("select[name='dirs']").find("option[value='HOSTPATH']").attr("selected","selected");
				$(this).find("select[name='writeOrRead']").find("option[value='"+tmpDraft["readOnly"]+"']").attr("selected","selected");
				$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
				$(this).find("input[name='mountPath']").val(tmpDraft["mountPath"]);
				$(this).find("input[name='hostPath']").val(tmpDraft["hostPath"]);
				
				//hide emptydirs and show hostpath info
				$(this).find("input[name='mountPath']").css("display","");
				$(this).find("input[name='hostPath']").css("display","");
				$(this).find("input[name='insPath']").css("display","none");
			}
			
			//bind change event
			$($(this).find("select[name='dirs']")).bind("change",function(){
				var currentSelect = $(this).val();
				//host
				if(currentSelect == "HOSTPATH"){
					$(this).parent().children("input[name='insPath']").hide();
					$(this).parent().children("input[name='mountPath']").show();
					$(this).parent().children("input[name='hostPath']").show();
				//instance
				}else if(currentSelect == "EMPTYDIR"){
					$(this).parent().children("input[name='insPath']").show();
					$(this).parent().children("input[name='mountPath']").hide();
					$(this).parent().children("input[name='hostPath']").hide();
				}
			});
			seq--;
			loadStorages(volumeMountDrafts,seq);
			
		});
	}
}

function getEnvsToString(envs){
	if(envs==null || envs.length==0){
		return "";
	}
	var arr=[];
	for(var i=0;i<envs.length;i++){
		var map = envs[i];
		arr.push(map.key+"="+map.value);
	}
	return arr.join(",");
}

function getEnvsToArray(envsStr){
	if(envsStr==null || envsStr.trim()==""){
		return null;
	}
	var envs=[];
	var arr = envsStr.split(",");
	for(var i=0;i<arr.length;i++){
		envStr = arr[i];
		var env={};
		env.key=envStr.split("=")[0]+"";
		env.value=envStr.split("=")[1]+"";
		envs.push(env);
	}
	return envs;
}
//save single image configuration 
$("#btnnew3pre").bind("click", function(event) {
	var uniqueImage = $("#uniqueImage").val();
	var registry = $("#registryConfig").val();
	var image = $("#imageConfig").val();
	
	var tag = $("#tagConfig").val();
	
	var commands = null;
	if($("#startCommands").val() != null){
		if($("#startCommands").val() == ""){
			commands = null;
		}else{
			commands = $("#startCommands").val().split(",");
		}
	}
	var envs = getEnvsToArray($("#envvar").val());
	var cpu = $("#cpus").val();
	var mem = $("#mems").val();
	var currentContainerDraft = {};
	var volumeMountDrafts=[];
	//save storages
	$("#storages >div").each(function(){
		var volumeMountDraft = {};
		var selectDir = $(this).find("select[name='dirs']").val();
		var volumeName = $(this).find("input[name='volumeName']").val();
		if(volumeName==null || volumeName.trim()==""){
			return;
		}
		var writeOrRead = new Boolean($(this).find("select[name='writeOrRead']").val());
		var mountPath = $(this).find("input[name='mountPath']") == null?"":$(this).find("input[name='mountPath']").val();
		var hostPath = $(this).find("input[name='hostPath']") == null?"":$(this).find("input[name='hostPath']").val();
		var insPath = $(this).find("input[name='insPath']") == null?"":$(this).find("input[name='insPath']").val();
		volumeMountDraft["name"] = volumeName;
		volumeMountDraft["readOnly"] = writeOrRead;
		volumeMountDraft["volumeType"] = selectDir;
		if(selectDir == "HOSTPATH"){
			volumeMountDraft["mountPath"] = mountPath;
			volumeMountDraft["hostPath"] = hostPath;
		}else if(selectDir == "EMPTYDIR"){
			volumeMountDraft["mountPath"] = insPath;
		}
		volumeMountDrafts.push(volumeMountDraft);
	});
	
	currentContainerDraft["registry"] = registry;
	currentContainerDraft["image"] = image;
	currentContainerDraft["tag"] = tag;
	
	//save commands
	currentContainerDraft["commands"] = commands;
	
	//save envs
	currentContainerDraft["envs"] = envs;
	
	//save cpu and mem
	currentContainerDraft["cpu"] = cpu;
	currentContainerDraft["mem"] = mem;
	//readd into
	currentContainerDraft["volumeMountDrafts"] = volumeMountDrafts;
	
	currentContainerMap[uniqueImage] = currentContainerDraft;
	console.log(currentContainerMap);
	$("#service-new3").modal("hide");
});

//save single image configuration 
$("#versionUpdate").bind("click", function(event) {
	var version = getVersion(currentContainerMap);
	version.serviceId = $('#serviceID').val();
	var paramData={};
	paramData = version;
	var AjaxURL= "/app/version/createVersion?serviceId="+$('#serviceID').val();
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode==200){
				alert("版本配置升级成功!");
			}else{
				alert("版本配置升级失败!");
			}
			
		},
		error: function(data) {
			alert("版本配置升级失败!");
		}
	});
});

//add new storage
function showStorage(){
    var childdiv=$('<div></div>');  
	childdiv.attr("id","id" + new Date().getTime());
	$("#storages").append(childdiv);
	childdiv.load("/js/statichtml/app/storageTemplate.html #storageTemplate",
			function(responseTxt,statusTxt,xhr){
		
		//bind change event
		$($(this).find("select[name='dirs']")).bind("change",function(){
			var currentSelect = $(this).val();
			//host
			if(currentSelect == "HOSTPATH"){
				$(this).parent().children("input[name='insPath']").hide();
				$(this).parent().children("input[name='mountPath']").show();
				$(this).parent().children("input[name='hostPath']").show();
			//instance
			}else if(currentSelect == "EMPTYDIR"){
				$(this).parent().children("input[name='insPath']").show();
				$(this).parent().children("input[name='mountPath']").hide();
				$(this).parent().children("input[name='hostPath']").hide();
			}
		});
		
	});
}

$("#btncancel3pre").bind("click",function(event){
	//show port mapped detail into sPort tab
	
	//close this window
	$("#service-new3").modal("hide");
});



//根据version信息生成页面div
function loadsVersionDetail(versionDetail){
	var htmlContent = "";
	$("#versionDetail").html("");
	
//	htmlContent = $("#versionDetail").html();
//	if(containers!=null){
		for(var i=0;i<versionDetail.length;i++){
			var container = versionDetail[i];
			var image = container["image"]==null?"-":container["image"];
			var registry = container["registry"]==null?"-":container["registry"];
			var tag = container["tag"]==null?"-":container["tag"];
			var volumeMountDrafts = container["volumeMountDrafts"];
//			console.log(volumeMountDrafts)
			var id = registry+image;
			//add new row to the images list table
			var childDiv1 = "<div class='filletLi' style='border:1px solid #000;width: 97%;margin-top:20px'>"+
					"<ul>"+
						"<li style='list-style-type: none; padding-bottom: 3px;'>镜像名称:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+image+"</li>"+
						"<li style='list-style-type: none; padding-bottom: 3px;'>镜像仓库:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+registry+"</li>"+
						"<li style='list-style-type: none; padding-bottom: 3px;'>镜像版本:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tag+"</li>"+
//						"<li style='list-style-type: none; padding-bottom: 3px;'>是否已废弃:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tag+"</li>"+
//						"<li style='list-style-type: none; padding-bottom: 3px;'>访问地址:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+registry+"</li>"+
						"<li style='list-style-type: none; padding-bottom: 3px;'>挂载存储:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			
			var tableStr = "";
			if(volumeMountDrafts!=null){
				tableStr = "<table class='table table-bordered data-table display' style='border:1px;width:95%'><tr><th>挂载类型</th><th>名称</th><th>是否只读</th><th>主机目录</th><th>挂载目录</th></tr>";
				for(var j=0;j<volumeMountDrafts.length;j++){	
					var vm = volumeMountDrafts[j];
					var volumeType = vm["volumeType"]==null?"-":(vm["volumeType"] == "HOSTPATH"?"主机内目录":"实例内目录");
					var name = vm["name"]==null?"-":vm["name"];
					var readOnly = vm["readOnly"]==null?"-":((vm["readOnly"] == "true"|| vm["readOnly"] == true)?"是":"否");
					var hostPath = vm["hostPath"]==null?"-":vm["hostPath"];
					var mountPath = vm["mountPath"]==null?"-":vm["mountPath"];
					tableStr += "<tr><td>"+volumeType+"</td><td>"+name+"</td><td>"+readOnly+"</td><td>"+hostPath+"</td><td>"+mountPath+"</td></tr>";
				}
				tableStr+="</table>";
			}
			var childDiv2 ="</li>"+
						"</ul>"+
				"</div>";
			htmlContent = htmlContent.concat(childDiv1).concat(tableStr).concat(childDiv2);
//			currentContainerMap[id]= container;
		}
//	}
	$("#versionDetail").html(htmlContent);
}

function changeVersion(){
	var version = $('#depVersion option:selected').val();
	$.ajax({
		type: "GET",
		dataType: "json",
		url: "/app/version/getVersion?serviceId="+$('#serviceID').val()+"&version="+version,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				var currenttVersion = data.result;
				console.log(currenttVersion)
				if(currenttVersion!=null){
					versionDetail = getContainerVolumes(currenttVersion);
					loadsVersionDetail(versionDetail);
					//console.log(oldContainers);
					if(currenttVersion.deprecate){
						$('#fqbutton').attr('style','display:none');
						$('#qybutton').attr("style","display:'';");
					}else{
						$('#fqbutton').attr("style","display:'';");
						$('#qybutton').attr('style','display:none');
					}
				}
			}else{
				alert("获取版本详情失败!");
			}
		},
		error: function(data) {
			alert("获取版本详情失败!");
		}
	});
}

function createVersionSelect(currentVersion,versionList){
//	console.log(versionList)
	for(var i=0;i<versionList.length;i++){   
		var version = versionList[i];
		var versionid = version.version;
		var versionname = version.versionName;
		if(versionid==currentVersion){
			$('#depVersion').append("<option value='"+versionid+"' selected='selected'>"+versionname+"</option>");
			$('#depVersion').prev().children().find('span').html(versionname);
		}else{
			$('#depVersion').append("<option value='"+versionid+"'>"+versionname+"</option>");
		}
		changeVersion();
    }

}

function deprecate(){
	if(confirm('确认废除？')){
		var version = $('#depVersion option:selected').val();
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "/app/version/deprecateVersion?serviceId="+$('#serviceID').val()+"&version="+version,
			contentType:"application/json",
			success: function (data) {
				if(data.resultCode == 200){
					alert("废除成功!");
					changeVersion();
				}else{
					alert("废除版本失败!");
				}
			},
			error: function(data) {
				alert("废除版本失败!");
			}
		});
	}
}

function enableVersion(){
	if(confirm('确认启用？')){
		var version = $('#depVersion option:selected').val();
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "/app/version/enableVersion?serviceId="+$('#serviceID').val()+"&version="+version,
			contentType:"application/json",
			success: function (data) {
				if(data.resultCode == 200){
					alert("启用成功!");
					changeVersion();
				}else{
					alert("启用版本失败!");
				}
			},
			error: function(data) {
				alert("启用版本失败!");
			}
		});
	}
}

function deleteVersion(){
	if(confirm('确认删除？')){
		var version = $('#depVersion option:selected').val();
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "/app/version/deleteVersion?serviceId="+$('#serviceID').val()+"&version="+version,
			contentType:"application/json",
			success: function (data) {
				if(data.resultCode == 200){
					alert("删除成功!");
					changeVersion();
				}else{
					alert("删除版本失败!");
				}
			},
			error: function(data) {
				alert("删除版本失败!");
			}
		});
	}
}

//服务实例列表
function getInstenceByServiceId(serviceId){
	$.ajax({
		url:"/app/service/listPodsByServiceId",
		 data:{
			 serviceId:serviceId
		 },
	 	 type:"get",
	 	 dataType:"json",
	 	 success:function(data){
	 		 var podList = data.result;
	 		$("#podTBody").empty();
	 		$.each(podList,function(i,v){
	 			var images = '';
	 			$.each(v.containers,function(t,n){
					var image = '<span>' + n.imageName + '</span>';
					images += image;
				})
	 			var $tr = '<tr>'
						+'<td style="text-align:center;">'+v.instanceName+'</td>'
						+'<td style="text-align:center;">'+v.status+'</td>'
						+'<td style="text-align:center;">'+images+'</td>'
						+'<td style="text-align:center;">'+v.startTime+'</td>'
						+'<td style="text-align:center;"><a>日志</a></td>'
						+'</tr>';
				$("#podTBody").append($tr);
	 		})
	 	 }
	})
}

//服务事件
function getEventByServiceId(serviceId){
	$.ajax({
		url:"/app/service/listDeployEvent",
		 data:{
			 serviceId:serviceId
		 },
	 	 type:"get",
	 	 dataType:"json",
	 	 success:function(data){
	 		 var eventList = data.result;
	 		$("#eventTBody").empty();
	 		$.each(eventList,function(i,v){
	 			var message = '';
	 			var primary = new Array();
	 			var target = new Array();
	 			var current = new Array();
	 			if(v.primarySnapshot != null){
	 				$.each(v.primarySnapshot,function(t,n){
	 					primary.push('v'+(t+1)+':'+n.version +',r'+(t+1)+':'+n.replicas);
	 				});
	 				$.each(v.targetSnapshot,function(t,n){
	 					target.push('v'+(t+1)+':'+n.version +',r'+(t+1)+':'+n.replicas);
	 				});
	 				$.each(v.currentSnapshot,function(t,n){
	 					current.push('v'+(t+1)+':'+n.version +',r'+(t+1)+':'+n.replicas);
	 				});
	 				message = '起始版本:'+primary.join(';')+' 目标版本:'+target.join(';')+' 当前版本:'+current.join(';');
	 				
	 			}else{
	 				$.each(v.targetSnapshot,function(t,n){
	 					target.push('v'+(t+1)+':'+n.version +',r'+(t+1)+':'+n.replicas);
	 				});
	 				$.each(v.currentSnapshot,function(t,n){
	 					current.push('v'+(t+1)+':'+n.version +',r'+(t+1)+':'+n.replicas);
	 				});
	 				message = '目标版本:'+target.join(';')+' 当前版本:'+current.join(';');
	 			}
	 			var $tr = '<tr>'
						+'<td style="text-align:center;">'+v.operation+'</td>'
						+'<td style="text-align:center;">'+v.startTime+'</td>'
						+'<td style="text-align:center;">'+v.expireTime+'</td>'
						+'<td style="text-align:center;">'+v.lastModifiedTime+'</td>'
						+'<td style="text-align:center;">'+v.state+'</td>'
						+'<td style="text-align:center;">'+v.userName+'</td>'
						+'<td style="text-align:center;">'+message+'</td>'
						+'</tr>';
				$("#eventTBody").append($tr);
	 		})
	 	 }
	})
}

/***********deal with funcion button of service info common data************/
//cancle sacle
$("#cancleScales").bind("click",function(event){
	$("#scaleUpDowns").modal("hide");
});

//cancle upgrade
$("#cancleUpgrades").bind("click",function(event){
	$("#updateRollbacks").modal("hide");
});

//cancle start service
$("#cancleStartServices").bind("click",function(event){
	$("#startServiceDisplays").modal("hide");
});

//start servie display
function infoStartService(){
	var currentServiceState = $("#infoServicestate").html();
	if(currentServiceState != "STOP"){
		alert("当前服务非停止状态，不能启动服务！");
		return;
	}
	
	//clear start instance number
	$("#wishStartInstanceNumbers").val("");
	
	//init start version,get version names
	var versionList = null;
	AjaxURL = "/app/version/getVersionNames?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					versionList = eval(data.result);
					var selectSecond = $("#selectStartVersionNumber");
					selectSecond.empty();
					for(var i=0;i<versionList.length;i++){
/*						var opt= new Option();
						opt.value=versionList[i].version;
						opt.text = versionList[i].versionName;
						selectSecond.options.add(opt);*/
//						$("#selectStartVersionNumbers").append("<option value='"+versionList[i].version+"' selected='selected'>"+versionList[i].versionName+"</option>");
					}
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//rollback or upgrade service diaplay
function infoRollback(){
	var currentServiceState = $("#infoServicestate").html();
	if(currentServiceState != "RUNNING"){
		alert("当前服务非运行状态，不能升级回滚服务！");
		return;
	}
	//get current version number
	var currentVersionNum = "";
	var AjaxURL = "/app/service/getCurrentVersionNum?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					currentVersionNum = data.result.join(",");
					//set init value
					$("#currentVersionNumbers").html(currentVersionNum);
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	//get version names
	var versionList = null;
	AjaxURL = "/app/version/getVersionNames?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					versionList = eval(data.result);
					var selectSecond = $("#selectVersionNumbers");
					selectSecond.empty();
					for(var i=0;i<versionList.length;i++){
						/*var opt= new Option();
						opt.value=versionList[i].version;
						opt.text = versionList[i].versionName;
						selectSecond.options.add(opt);*/
					}
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//sacle diaplay 
function infoScale(){
	var currentServiceState = $("#infoServicestate").html();
	if(currentServiceState != "RUNNING"){
		alert("当前服务非运行状态，不能扩容缩容服务！");
		return;
	}
	var AjaxURL = "/app/service/getReplicasByServiceId?serviceId="+serviceId;
	var instanceNumber = 0;
	var currentVersionNum = 0;
	
	//get current instance count
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				instanceNumber = data.result;
				
				//set current instance number
				$("#instanceNumbers").html(instanceNumber);
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	AjaxURL = "/app/service/getCurrentVersionNum?serviceId="+serviceId;
	//get current version num
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					currentVersionNum = data.result.join(",");
					//set current version number
					$("#currentVersionNums").html(currentVersionNum==null?"":currentVersionNum);
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//start service
$("#startServiceSubmits").bind("click",function(event){
	var version = $("#selectStartVersionNumbers").val();
	var replicas = $("#wishStartInstanceNumbers").val();
	var AjaxURL = "/app/service/start?serviceId="+serviceId+"&version="+version+"&replicas="+replicas;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			//success
			if(data.resultCode == 200){
				//refresh service info
				refreshServiceInfo();
			}else{
				alert("启动服务异常！");
			}
		},
		error: function(data) {
			alert("启动服务异常！");
		}
	});
});

//refresh service info
function refreshServiceInfo(){
	window.location.href="/app/service/service-info/"+serviceId;
}

//rollback or upgrade
$("#upgradeSubmits").bind("click",function(event){
	var currentVersionNumber = $("#currentVersionNumbers").html();
	var selectVersionNumber = $("#selectVersionNumbers").val();
	var wishReplies = $("#wishInstanceNumbers").val();
	var ajaxUrl = "";
	
	if(selectVersionNumber == currentVersionNumber){
		alert("期望运行版本与当前版本相同，无法进行升级回滚！");
		return;
	}
	
	//upgrade
	if(selectVersionNumber>currentVersionNumber){
		ajaxUrl = "/app/service/startUpdate?serviceId="+serviceId+"&version="+selectVersionNumber+"&replicas="+wishReplies;
	}else{
		ajaxUrl = "/app/service/startRollback?serviceId="+serviceId+"&version="+selectVersionNumber+"&replicas="+wishReplies;
	}
	
	$.ajax({
		type: "POST",
		dataType: "json",
		url: ajaxUrl,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				alert("操作成功！");
				//refresh service info
				refreshServiceInfo();
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
});

//scale
$("#scaleSubmits").bind("click",function(event){
	var currentVersionNumer = $("#currentVersionNums").html();
	var currentNumber = $("#instanceNumbers").html();
	var futureNumber = $("#futureNumbers").val();
	var ajaxUrl = "";
	if(futureNumber == currentNumber){
		alert("期望实例个数与当前实例个数相等，无法进行扩容缩容！");
	}
	
	//up
	if(futureNumber>currentNumber){
		ajaxUrl = "/app/service/scaleUp?serviceId="+serviceId+"&version="+currentVersionNumer+"&replicas="+futureNumber;
	//down
	}else{
		ajaxUrl = "/app/service/scaleDown?serviceId="+serviceId+"&version="+currentVersionNumer+"&replicas="+futureNumber;
	}
	$.ajax({
		type: "POST",
		dataType: "json",
		url: ajaxUrl,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				alert("操作成功！");
				//refresh service info
				refreshServiceInfo();
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
});

//stp service
function infoStopService(){
	var currentServiceState = $("#infoServicestate").html();
	if(currentServiceState != "RUNNING"){
		alert("当前服务非运行状态，不能停止服务！");
		return;
	}
	var AjaxURL = "/app/service/stopService?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			//success
			if(data.resultCode == 200){
				//refresh service info
				refreshServiceInfo();
			}else{
				alert("停止服务异常！");
			}
		},
		error: function(data) {
			alert("停止服务异常！");
		}
	});
}

//save node ports into db
function saveNodePort(){
	var count = 0;
	//check required
	if(checkNodePortInfo()){
		var paramData = [];
		if($("#portMapped >div").find("input[name='nodePort']").length>0){
			$("#portMapped >div").find("input[name='nodePort']").each(function(){
				var nodePortItem = {};
				var nodePort = $(this).val();
				var targetPort = $(this).parent().find("input[name='targetPort']").val();
				var protocol = $(this).parent().find("select[name='protocol']").val();
				var description = $(this).parent().find("input[name='description']").val();
				
				//package value going to save into db
				nodePortItem["nodePort"] = nodePort;
				nodePortItem["targetPort"] = targetPort;
				nodePortItem["protocol"] = protocol;
				nodePortItem["description"] = description;
				paramData.push(nodePortItem);
				
				//each complete,request for save
				if(++count == $("#portMapped >div").find("input[name='nodePort']").length){
					saveLoadBalancerAjax(paramData);
				}
			});
		}else{
			//empty loadbalancer
			saveLoadBalancerAjax([]);
		}
	}else{
		alert("请重新检查填写的数据，格式为非0开头[1~6]位数字！");
	}
}

//ajax request to save loadbalancer into db
function saveLoadBalancerAjax(data){
	var paramData = JSON.stringify(data);
	var ajaxUrl = "/app/service/createLoadBalancer?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: ajaxUrl,
		data: paramData,
		contentType:"application/json",
		success: function (data) {
			alert("操作成功！");
			$("#saveData").css("display","none");
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//check required field when save node ports
function checkNodePortInfo(){
	var flag = true;
	$("#portMapped >div").each(function(){
		if($(this).find("input[name='nodePort']").length>0){
			var nodePort = $(this).find("input[name='nodePort']").val();
			var targetPort = $(this).find("input[name='targetPort']").val();
			/**any of nodeport or targertport is invalid,break this each***/
			if(!checkNumberic(nodePort)){
				$(this).find("input[name='nodePort']").css("background-color","red");
				flag = false;
				return false;
			}else{
				$(this).find("input[name='nodePort']").css("background-color","");
			}
			if(!checkNumberic(targetPort)){
				$(this).find("input[name='targetPort']").css("background-color","red");
				flag = false;
				return false;
			}else{
				$(this).find("input[name='targetPort']").css("background-color","");
			}
			/**any of nodeport or targertport is invalid,break this each***/
		}else{
			return true;
		}
	});
	return flag;
}

function deleteLoadBalancer(obj){
	$(obj).parent().remove();
	$("#saveData").show();
}

//numberic check
function checkNumberic(data){
	if(data == null || data.trim()==""){
		return false;
	}
	//var reg=/^d+(.d+)?$/
	var reg = /^[1-9][0-9]{0,5}$/;
	return(reg.test(data));	
}

//disable
function disableNodePort(){
	$("input[class='disen']").attr("disabled",true);//disabled input
	$("select[name='iProtocol']").prop("disabled","disabled");
	$("select[name='iProtocol']").select2();
}

//delete node port
function deleteNodePort(){
	if(confirm("删除操作将删除所有的端口映射，确认删除？")){
		$("#portMapped").css("display","none");//hide
		$("input[class='disen']").val("");//clear
		
		//delete from db means save null
		var ajaxUrl = "/app/service/createLoadBalancer?serviceId="+serviceId;
		$.ajax({
			type: "POST",
			dataType: "json",
			url: ajaxUrl,
			contentType:"application/json",
			success: function (data) {
				alert("操作成功！");
				$("#nodeOperation").css("display","none");
			},
			error: function(data) {
				alert("error!");
			}
		});
	}
}

//display node ports info when click the loadbalancer tab
function initLoadBalancer(){
	var nodePorts = $("#infoNodePorts").val();
	var disPlayNodePort = {};
	if(nodePorts != null && nodePorts.length>0){
		disPlayNodePort = nodePorts[0];
		$("input[name='iNodePort']").val(disPlayNodePort==null?"":disPlayNodePort["nodePort"]);
		$("input[name='iTargetPort']").val(disPlayNodePort==null?"":disPlayNodePort["targetPort"]);
		$("input[name='description']").val(disPlayNodePort==null?"":disPlayNodePort["description"]);
		
		//make select2 default select
		var iprotocal = $("#pros").select2();
		iprotocal.val(disPlayNodePort["protocol"]).trigger("change");
	}
}

//edit single port mapped div
function editPortMapped(obj){
	$(obj).find("input").attr("disabled",false);
	
	var iprotocal = $(obj).find("select").select2();
	iprotocal.val("TCP").trigger("change");
}
