//import React from 'react';
import Highcharts from 'highcharts';
//import HighchartsReact from 'highcharts-react-official';


export const drugChart = {
  chart: {
    plotBackgroundColor: null,
    plotBorderWidth: 0,
    plotShadow: false
},
title: {
    text: 'Drug<br>Chart<br>',
    align: 'center',
    verticalAlign: 'middle',
    y: 60
},
tooltip: {
    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
},
accessibility: {
    point: {
        valueSuffix: '%'
    }
},
plotOptions: {
    pie: {
        dataLabels: {
            enabled: true,
            distance: -50,
            style: {
                fontWeight: 'bold',
                color: 'white'
            }
        },
        startAngle: -90,
        endAngle: 90,
        center: ['50%', '75%'],
        size: '110%'
    }
},
series: [{
    type: 'pie',
    name: 'Drug Chart',
    innerSize: '50%',
    data: [
        ['Prescribed', 58.9],
        ['Dispensed', 13.29],
        
    ]
}]
}
