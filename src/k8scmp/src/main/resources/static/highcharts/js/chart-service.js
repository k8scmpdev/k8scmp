
$(function(){
    var chart=null;
    var options={
        chart:{
            backgroundColor: 'dark',
        },
        title:{
            text:"服务",
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
    chart = new Highcharts.Chart('chart-service',options)
    $.getJSON("/overview/serviceinfo",function(data){//获取Json文件,并创建Json对象
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

