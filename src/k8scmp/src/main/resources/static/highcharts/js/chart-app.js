
$(function(){
    var chart=null;
    var options={
        chart:{
            backgroundColor: 'dark',
        },
        title:{
            text:"应用",
            style:{ "color": "white", "fontSize": "18px" }
        },
        legend:{
            style:{ "color": "white", "fontSize": "14px" },
            align:'right',
            y:-25,
            layout: 'vertical',
            itemStyle: {
                color: 'white',
            }
        },
        tooltip: {
            headerFormat: '{series.name}<br>',
            pointFormat: '{point.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series:  []
    }
    chart = new Highcharts.Chart('chart-app',options)
//    $.ajax({
//    	type: 'GET',
//        url: '/overview/appinfo',
//        data: '',
//        dataType: 'json',
//        success: function (data) {
//        	
//        	chart.addSeries({
//                type:data.type,
//                name:data.name,
//                data:data.data
//            });
//          $.each(data.data,function(i, field){     //遍历json数组
//        	  alert(field)
//        	  alert(i)
//              chart.addSeries({
//                  type:data.type,
//                  name:i,
//                  data:field
//              });
//          })
//        },
//    });
//    $.getJSON("highcharts/jsonData/chart-app.json",function(data){//获取Json文件,并创建Json对象
    $.getJSON("/overview/appinfo",function(data){//获取Json文件,并创建Json对象
    	var arr = [];
    	$.each(data.datalist,function(i, field){
    		arr.push([i,   field]);
    	});
    	
    	chart.addSeries({
	        type:data.type,
	        name:data.name,
	        data:arr
	    });
    })
})

