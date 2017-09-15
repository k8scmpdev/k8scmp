
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
//    request({
//        method: 'POST',
//        url: '/overview/opelog',
//        data: data,
//        onload: function (resp) {
//          var data = JSON.parse(resp.responseText);
//          if (data.resultCode === 200) loginSuccess();
//          else password_wrong.style.display = 'block';
//        },
//    });
    $.getJSON("highcharts/jsonData/chart-app.json",function(data){//获取Json文件,并创建Json对象
        $.each(data,function(i, field){     //遍历json数组
            chart.addSeries({
                type:field.type,
                name:field.name,
                data:field.data
            });
        })
    })
})

