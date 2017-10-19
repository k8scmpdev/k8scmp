/*$(document).ready(function(){
	//set href
	$("#addService").attr("th:href","@{/app/service/service-new(appCode=${#httpServletRequest.getParameter('appCode')})}");
});*/

/*$("#example-select-all").on("click",function(){
	var rows = table.rows({'search':'applied'}).nodes();
	$('input[type="checkbox"]', rows).prop('checked', this.checked); 
});

$(".scroll-table tbody").on("change","input[type='checkbox']",function(){
	if(!this.checked){
		var el=$("#example-select-all").get(0);
	}
	if(el && el.chekced && ("inderterminate" in el)){
		el.inderterminate=true;
	}
});
*/

function deleteService(){
	
	
}

function deleteSingleService(serviceId){
	var AjaxURL= "/app/service/delete/"+serviceId;
	$.ajax({
		type: "GET",
		dataType: "html",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			refreshServices();
		},
		error: function(data) {
			alert("error!");
		}
	});
}


function refreshServices(){
	var appId = $("#").val();
	var AjaxURL= "/app/service/";
	$.ajax({
		type: "GET",
		dataType: "html",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			refreshServices(appId);
		},
		error: function(data) {
			alert("error!");
		}
	});
} 
