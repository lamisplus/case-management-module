export const basicColumn = {
  chart: {
      type: 'column'
  },
  title: {
      text: 'Monthly Average Drugs'
  },
  subtitle: {
      text: 'Source: Hospital'
  },
  xAxis: {
      categories: [
          'Jan',
          'Feb',
          'Mar',
          'Apr',
          'May',
          'Jun',
          
      ],
      crosshair: true
  },
  yAxis: {
      min: 0,
      title: {
          text: ' '
      }
  },
  tooltip: {
      headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
      pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
          '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
      footerFormat: '</table>',
      shared: true,
      useHTML: true
  },
  plotOptions: {
      column: {
          pointPadding: 0.2,
          borderWidth: 0
      }
  },
  series: [{
      name: 'Prescribed',
      data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0]

  }, {
      name: 'Dispensed',
      data: [83.6, 78.8, 98.5, 93.4, 106.0, 84.5]

  }]
};
