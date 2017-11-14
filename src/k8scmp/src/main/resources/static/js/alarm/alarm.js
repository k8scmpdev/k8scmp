$(document).ready(function(){ 
　　$.get("/alarm/templatelist",function(data){ 
		$('#myTabContent').html(data);
	}); 

	$(function(){
		$(".nav-tabs li").click(function(){
			var $this= $(this);
			_clickTab = $this.find('a').attr('target');
			 $.get(_clickTab,function(data){
				$('#myTabContent').html(data);
				
			}); 
		});
	});
});  

//规则
//删除规则
 function deleteTemplate(id){
	 var r = confirm("您确定删除该规则吗？");
	 if(r == true){
		 $.ajax({
			 url:"/alarm/delete/"+id,
			 data:{
				 id:id
			 },
		 	 type:"delete",
		 	 dataType:"json",
		 	 success:function(){
		 		//var redata = eval(data);
				$.get("/alarm/templatelist",function(data){
					$('#myTabContent').html(data);
				});
		 	 }
		 });
	}
 }
 //搜索规则
 function getTemplatByName(){
	var templateName = $("#searchTemplate").val();
	 $.ajax({
		url:"/alarm/search",
		data:{
			templateName : templateName
		},
		type:"post",
		dataType:"json",
		success:function(data){
			var templateList = eval(data.result);
			//alert(templateList);
			$("#hostTbody").empty();
			$.each(templateList,function(k,v){
				var tr = '<tr>'
					+'<td>'+v.id+'</td>'
					+'<td>'+v.templateName+'</td>'
					+'<td>'+v.creatorName+'</td>'
					+'<td>'+v.templateType+'</td>'
					+'<td>'+v.createTime+'</td>	'
					+'<td><a title="单击查看规则" href="/alarm/edit?templateType='+v.templateType+'&templateId='+v.id+'" class="tip-top">编辑</a>&nbsp;&nbsp;'
					+'<a title="单击删除规则" onclick="deleteTemplate('+v.id+')" class="tip-top">删除</a></td>'
					+'</tr>';
				$("#hostTbody").append(tr);	
			})
	 	 }
	})  
 }


 //主机组
 //删除主机组
 function deleteHostGroup(id){
	 var r = confirm("您确定删除该组吗？");
	 if(r == true){
		 $.ajax({
			 url:"/hostgroup/delete/"+id,
			 data:{
				 id:id
			 },
		 	 type:"delete",
		 	 dataType:"json",
		 	 success:function(){
		 		//var redata = eval(data);
				$.get("/hostgroup/list",function(data){
					$('#myTabContent').html(data);
				});
		 	 }
		 }); 
	 }
 }
 //修改主机组名
 function editHostGroup(id){
	 bootpopup.prompt("主机组名", function(data) {
		 //alert(data);
		 var hostGroupInfoBasic = new Object();
		 hostGroupInfoBasic.id = id;
		 hostGroupInfoBasic.hostGroupName = data;
		 $.ajax({
			 url:"/hostgroup/modify",
		 	data:JSON.stringify(hostGroupInfoBasic),
		 	contentType : 'application/json;charset=utf-8',
		 	type:"post",
		 	dataType:"json",
		 	success:function(res){
		 		$.get("/hostgroup/list",function(data){
					$('#myTabContent').html(data);
				});
		 	}
		 });
	 });
 }
 //创建主机组
 function postHostGroupName(){
	 $.ajax({
		 url:"/hostgroup/create", 
		 data:{
			 hostGropName : $('#hostGroupNameInput').val()
			 }, 
		 type: "post",
		 dataType: "json",
		 success: function(data) {
			var redata = eval(data);
			$.get("/hostgroup/list",function(data){
				$('#myTabContent').html(data);
			});
		 }
	 })
 }
 //搜索主机组
  function searchHostGroup(){
	  var hostGroupName = $("#searchHostGroup").val();
		 $.ajax({
			url:"/hostgroup/search",
			data:{
				hostGroupName : hostGroupName
			},
			type:"post",
			dataType:"json",
			success:function(data){
				var hostGroupList = eval(data.result);
				//alert(templateList);
				$("#hostGroupTbody").empty();
				$.each(hostGroupList,function(k,v){
					var $id = 'htbody'+k
					var tr = '<tr>'
					+'<td>'+v.id+'</td>'
					+'<td>'+v.hostGroupName+'</td>'
					+'<td>'+v.creatorName+'</td>'
					+'<td>'+v.createTime+'</td>'
					+'<td >'
						+'<div style="width: 200px">'
							+'<table>'
								+'<thead>'
								+'</thead>'
								+'<tbody id="'+$id+'">'
									
								+'</tbody>'
							+'</table>'
						+'</div>'
					+'</td>'
					+'<td>'
						+'<a title="单击添加主机" href="/hostgroup/bindHostGroup?id='+v.id+'" class="tip-top" >添加主机</a>&nbsp;&nbsp;'
						+'<a title="单击修改组名" onclick="editHostGroup('+v.id+')" class="tip-top">修改</a>&nbsp;&nbsp;'
						+'<a title="单击删除组" onclick="deleteHostGroup('+v.id+')" class="tip-top">删除</a>'
					+'</td>'
				+'</tr>';
					$("#hostGroupTbody").append(tr);	
					
					$("#"+$id).empty();
					$.each(v.hostList,function(t,host){
						var trr = '<tr>'
							+'<td>'+host.id+'</td>'
							+'<td>'+host.hostname+'</td>'
							+'<td>'+host.ip+'</td>'
						+'</tr>'
						$("#"+$id).append(trr);
					});
					
				});
		 	 }
		}) 
 }
 
 //未恢复报警
 //搜索
 function getAlarmByName(){
	  var alarmName = $("#alarmName").val();
		 $.ajax({
			url:"/alarmEvent/search",
			data:{
				eventName : alarmName
			},
			type:"post",
			dataType:"json",
			success:function(data){
				var alarmEventList = eval(data.result);
				$("#hostEventTbody").empty();
				var total = 0;
				$.each(alarmEventList,function(k,v){
					total++;
					var td1;
					if(v.templateType == 'host'){
						td1 = '<td>'+ v.hostInfo.hostname +'</td>';
					}else{
						td1 = '<td>'+ v.deploymentAlarmInfo.instanceName +'</td>'
					}
					var td2 = '<td>'+ v.templateType +'</td>'+
					'<td>'+ v.metric +'</td>'+
					'<td>'+ v.currentStep +'</td>'+
					'<td>'+ v.timeStamp +'</td>'+
					'<td>'+
						'<a title="单击删除组" onclick="ignoreAlarm('+ v.id +')" class="tip-top"><i class="icon-trash"></i><span>删除</span></a>'+
					'</td>';
					
					var tr = '<tr>'+ td1 + td2 + '</tr>';;
					$("#hostEventTbody").append(tr);
				});
				$("#total").text(total);
		 	 }
		}) 
 }
 //忽略告警
 function ignoreAlarm(eventId){
	 var r = confirm("忽略该报警？");
	 if(r == true){
		 $.ajax({
			 url:"/alarmEvent/event/ignore",
			 data:{
				 alarmString:eventId
			 },
		 	 type:"post",
		 	 dataType:"json",
		 	 success:function(){
		 		//var redata = eval(data);
				$.get("/alarmEvent/list",function(data){
					$('#myTabContent').html(data);
				});
		 	 }
		 }); 
	 } 
 }
 
//用户组
//创建用户组
 function postUserGroupName(){
	var userGroupDraft = new Object();
	userGroupDraft.userGroupName = $('#userGroupNameInput').val();
	$.ajax({
		 url:"/api/alarm/usergroup", 
		 data:JSON.stringify(userGroupDraft), 
		 type: "post",
		 dataType: "json",
		 contentType : "application/json;charset=utf-8",
		 success: function(data) {
			var redata = eval(data);
			$.get("/api/list",function(data){
				$('#myTabContent').html(data);
			});
		 }
	 })
 }
 //搜索用户组
  function searchUserGroup(){
	  var userGroupName = $("#searchUserGroup").val();
		 $.ajax({
			url:"/api/search",
			data:{
				userGroupName : userGroupName
			},
			type:"post",
			dataType:"json",
			success:function(data){
				var userGroupList = eval(data.result);
				$("#userGroupTbody").empty();
				$.each(userGroupList,function(k,v){
					var $id = 'utbody'+k
					var tr = '<tr>'
					+'<td>'+v.id+'</td>'
					+'<td>'+v.userGroupName+'</td>'
					+'<td>'+v.creatorName+'</td>'
					+'<td>'+v.createTime+'</td>'
					+'<td >'+v.updateTime+'</td>'
					+'<td>'
						+'<a title="单击添加组成员" onclick="addUsers('+v.id+')" class="tip-top">添加组成员</a>&nbsp;&nbsp;'
						+'<a title="单击删除组" onclick="deleteUserGroup('+v.id+')" class="tip-top">删除</a>'
					+'</td>'
				+'</tr>';
					$("#userGroupTbody").append(tr);	
					
				});
		 	 }
		}) 
 }
  //删除用户组
 function deleteUserGroup(id){
	 var r = confirm("您确定删除该组吗？");
	 if(r == true){
		 $.ajax({
			 url:"/api/alarm/usergroup/"+id,
		 	 type:"delete",
		 	 dataType:"json",
		 	 success:function(){
				$.get("/api/list",function(data){
					$('#myTabContent').html(data);
				});
		 	 }
		 }); 
	 }
 }
 //添加组成员
function addUsers(id){
	window.location.href = "/api/addUsers/"+id;
 }