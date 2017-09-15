//service-new2 service-new3 service-new4 hide onload

//bind dynamic hide and display
//next
$("#btnnew1next").bind("click", function(event) {
	$("#service-new1").css("display","none");
	$("#service-new2").css("display","block");
	$("#service-new3").css("display","none");
	$("#service-new4").css("display","none"); 
});
$("#btnnew2next").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","none");
	$("#service-new3").css("display","block");
	$("#service-new4").css("display","none"); 
});
$("#btnnew3next").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","none");
	$("#service-new3").css("display","none"); 
	$("#service-new4").css("display","block"); 
});
//pre
$("#btnnew2pre").bind("click", function(event) {
	$("#service-new1").css("display","block"); 
	$("#service-new2").css("display","none"); 
	$("#service-new3").css("display","none");
	$("#service-new4").css("display","none"); 
});
$("#btnnew3pre").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","block"); 
	$("#service-new3").css("display","none");
	$("#service-new4").css("display","none"); 
});
$("#btnnew4pre").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","none");
	$("#service-new3").css("display","block");
	$("#service-new4").css("display","none"); 
});
$("#cancleService").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","none");
	$("#service-new3").css("display","none"); 
	$("#service-new4").css("display","none"); 
	window.location.href="/app/service/service-mgmt";
});

function showStorage(){
    var childdiv=$('<div></div>');  
	childdiv.attr("id","id" + new Date().getTime());
	$("#storages").append(childdiv);
	childdiv.load("/js/common.html #storageTemplate");
}