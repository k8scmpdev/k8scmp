$(document).ready(function(){
	var resultStr = $('#resultStr').val();
	if(resultStr!=''){
		alert(resultStr);
	}
});

$("#cluster").bind(
		"change",
		function(event) {
			var clustername = $('#cluster option:selected').html();
			var apiserver = $('#cluster option:selected').val();
//			var namespace = $('#ns option:selected').val();
			location.href="/cluster/logic/create?clustername=" + clustername +"&apiserver=" + apiserver;
			
//			var clustername = $('#cluster option:selected').val();
//			var apiserver = $('#cluster option:selected').html();
//			var url = "/cluster/allNsNamesByCluster?clustername=" + clustername +"&apiserver=" + apiserver;
//			alert(url)
//			$.ajax({
//				type : "GET",
//				dataType : "json",
//				url : url,
//				contentType : "application/json",
//				success : function(data) {
//					alert(data)
//
//				},
//				error : function(data) {
//					alert("版本配置升级失败!");
//				}
//			});
		});

function changeNodes(){
	var nodelist = $('#nodelist').val();
	$('#nodelist_real').val(nodelist);
}