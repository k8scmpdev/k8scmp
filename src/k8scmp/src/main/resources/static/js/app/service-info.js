function showPortMapped(){
    var childdiv=$('<div></div>');  
	childdiv.attr("id","id" + new Date().getTime());
	$("#portMapped").append(childdiv);
	childdiv.load("/js/statichtml/app/portMappedTemplate.html #portMappedTemplate");
}

$(".radioItem").change(function(event){
	var selectValue=$("input[name='publishRadios']:checked").val();
	if(selectValue == 1){
		$(".portMappes").css("display","block");
	}else{
		$(".portMappes").css("display","none");
	}
});

var oldContainers = []; 

var volumeDrafts = [];
var volumeDraftsMap = {};

var containerDrafts = [];
var currentContainerMap = {};
$(document).ready(function(){
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
	
	
	
	
	var paramData={};
	paramData["serviceId"] = "7623bcc8b3734e71a91604db5a66f96b";
	$.ajax({
		type: "GET",
		dataType: "json",
		url: "/app/version/getMaxVersion?serviceId=7623bcc8b3734e71a91604db5a66f96b",
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				var newestVersion = data.result;
				if(newestVersion!=null){
					oldContainers = getContainerVolumes(newestVersion);
					loadsUpgradeDatas(oldContainers);
					//console.log(oldContainers);
				}
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	$("#dirs").change(function(){
		alert(11);
		var currentSelect = $(this).val();
		if(currentSelect == "HOSTPATH"){
			$(this).parent().children("input[name='insPath']").hide();
			$(this).parent().children("input[name='conPath']").show();
			$(this).parent().children("input[name='hostPath']").show();
		//instance
		}else if(currentSelect == "EMPTYDIR"){
			$(this).parent().children("input[name='insPath']").show();
			$(this).parent().children("input[name='conPath']").hide();
			$(this).parent().children("input[name='hostPath']").hide();
		}
	});
});

$("#btnnew2pre").bind("click",function(event){
	//show port mapped detail into sPort tab
	
	//close this window
	$("#sPortMappedModel").modal("hide");
});












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
					"<td class='i10' style='width: 30%;min-width:30%;max-width:30%;'>"+image+"</td>" +
					"<td class='i20' style='width: 30%;min-width:30%;max-width:30%;'>"+registry+"</td>" +
					"<td class='i30' style='width: 10%;min-width:10%;max-width:10%;'>"+tag+"</td>" +
					"<td class='i40' style='width: 30%;min-width:30%;max-width:30%;'><a title='配置' class='tip-bottom iconOperation' data-target='#service-new3' data-toggle='modal' onclick='showImageConfiguration(\""+id+"\");'><i class='icon-edit'></i><span>配置</span></a>&nbsp;&nbsp;"+
					"<a title='删除' class='tip-bottom iconOperation' href='javascript:void(0);' onclick='deleteImage(this);'><i class='icon-trash'></i><span>删除</span></a></td>" +
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
	$(".image-table span[class='checked'] input[type='checkbox']").each(function(){
		tmpObj = $(this).parent().parent().parent().parent().parent().parent();
		var tr = {};
		tr["image"] = tmpObj.children("td[id='id']").html()==null?"":tmpObj.children("td[id='id']").html();
		tr["registry"] = tmpObj.children("td[id='registry']").html()==null?"":tmpObj.children("td[id='registry']").html();
		tr["tag"] = tmpObj.children("td[id='versions']").children("select[name='versions']").val();
		
		if(currentContainerMap[tr["registry"]+tr["image"]]!=null){
			alert("镜像["+tr["image"]+"]已存在!");
			return;
		}
		returnObj.push(tr);
	});
	return returnObj;
}

//remove image tr from image list table
function deleteImage(obj){
	var tr=obj.parentNode.parentNode;
	var tbody=tr.parentNode;
	tbody.removeChild(tr);
}

function showImageConfiguration(id){
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
			for(var k=0;k<volumeMountDrafts.length;k++){
				var tmpDraft = volumeMountDrafts[k];
				var volumeType= volumeMountDrafts["volumeType"];
				var childdiv=$('<div></div>');  
				$("#storages").append(childdiv);
				childdiv.load("/js/statichtml/app/storageTemplate.html #storageTemplate",
						function(responseTxt,statusTxt,xhr){
					
					//set value
					if(volumeType == "EMPTYDIR"){
						$(this).find("select[name='dirs']").find("option[value='EMPTYDIR']").attr("selected","selected");
						$(this).find("select[name='writeOrRead']").find("option[value=']"+tmpDraft["readOnly"]+"'").attr("selected","selected");
						$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
						$(this).find("input[name='insPath']").val(tmpDraft["mountPath"]);
						
						//hide hostpath and show emptydirs info
						$(this).find("input[name='mountPath']").css("display","none");
						$(this).find("input[name='hostPath']").css("display","none");
						$(this).find("input[name='insPath']").css("display","block");
					}else if(volumeType == "HOSTPATH"){
						$(this).find("select[name='dirs']").find("option[value='HOSTPATH']").attr("selected","selected");
						$(this).find("select[name='writeOrRead']").find("option[value=']"+tmpDraft["readOnly"]+"'").attr("selected","selected");
						$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
						$(this).find("input[name='mountPath']").val(tmpDraft["mountPath"]);
						$(this).find("input[name='hostPath']").val(tmpDraft["hostPath"]);
						
						//hide emptydirs and show hostpath info
						$(this).find("input[name='mountPath']").css("display","block");
						$(this).find("input[name='hostPath']").css("display","block");
						$(this).find("input[name='insPath']").css("display","none");
					}
					
					//bind change event
					$($(this).find("select[name='dirs']")).bind("change",function(){
						var currentSelect = $(this).val();
						//host
						if(currentSelect == "1"){
							$(this).parent().children("input[name='insPath']").hide();
							$(this).parent().children("input[name='conPath']").show();
							$(this).parent().children("input[name='hostPath']").show();
						//instance
						}else if(currentSelect == "2"){
							$(this).parent().children("input[name='insPath']").show();
							$(this).parent().children("input[name='conPath']").hide();
							$(this).parent().children("input[name='hostPath']").hide();
						}
					});
					
				});
			}
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

function getEnvsToString(envs){
	if(envs==null || envs.length==0){
		return "";
	}
	var arr=[];
	for(var map in envs){
		arr.push(map.key+"="+map.value);
	}
	return arr.join(",");
}

function getEnvsToArray(envsStr){
	if(envsStr==null || envsStr.trim()==""){
		return null;
	}
	var envs=[];
	for(var envStr in envsStr.split(",")){
		var env={};
		env.key=envStr.split("=")[0];
		env.key=envStr.split("=")[1];
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
	var commands = $("#startCommands").val().split(",");
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
	$("#service-new3").modal("hide");
});

//save single image configuration 
$("#versionUpdate").bind("click", function(event) {
	var id="7623bcc8b3734e71a91604db5a66f96b";
	console.log(currentContainerMap);
	var version = getVersion(currentContainerMap);
	version.serviceId = id;
	var paramData={};
	paramData = version;
	console.log(paramData);
	console.log(JSON.stringify(paramData));
	var AjaxURL= "/app/version/createVersion?serviceId="+id;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			
		},
		error: function(data) {
			alert("error!");
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

