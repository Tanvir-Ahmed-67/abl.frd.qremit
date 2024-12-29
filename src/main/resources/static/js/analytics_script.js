$(document).ready(function(){
  window.targetAchievementChart = null;
    get_loading();
    
    
    $.ajax({
        url: '/getChartData', // Endpoint defined in your Spring Controller
        type: 'GET',
        success: function (response) {
          renderMultiYearChart(response);
        },
        error: function (error) {
          console.error("Error fetching chart data", error);
        }
      });

      $.ajax({
        url: '/getTargetAchievementData',
        type: 'GET',
        success: function (response) {
            renderTargetAchievementChart(response);
        },
        error: function (error) {
            console.error("Error fetching chart data", error);
        }
    });

    $.ajax({
        url: '/getBankRemittanceData',
        type: 'GET',
        success: function (response) {
            renderBankRemittanceChart(response);
        },
        error: function (error) {
            console.error("Error fetching chart data", error);
        }
    });

    
    $.ajax({
      url: '/getBankRemittanceDataByYear',
      type: 'GET',
      success: function (response) {
          renderBankRemittanceChartByYear(response);
      },
      error: function (error) {
          console.error("Error fetching chart data", error);
      }
  });

    
      function renderMultiYearChart(response) {
        const ctx = document.getElementById('multiYearChart').getContext('2d');
      
        const datasets = response.datasets.map(dataset => ({
          label: dataset.year, // Each year's label
          data: dataset.data, // Data for that year
          backgroundColor: dataset.backgroundColor, // Bar/line color
          borderColor: dataset.borderColor, // Line border color
          borderWidth: 2,
          fill: false // Disable filling for line charts
        }));
      
        new Chart(ctx, {
          type: 'line', // Use 'bar' for grouped bar charts
          data: {
            labels: response.labels, // Months (X-axis)
            datasets: datasets
          },
          options: {
            responsive: true,
            plugins: {
              title: {
                display: true,
                text: 'Analytics Growth (2008 - 2024)'
              }
            },
            scales: {
              x: {
                title: {
                  display: true,
                  text: 'Months'
                }
              },
              y: {
                beginAtZero: true,
                title: {
                  display: true,
                  text: 'Amount'
                }
              }
            }
          }
        });
      }


      function renderTargetAchievementChart(response) {

        //console.log("Response Data:", response);
        // Validate response
        if (!response || !response.labels || !response.targets || !response.achievements || !response.percentages) {
          console.error("Invalid response data", response);
          return;
        }
      
        const canvas = document.getElementById('targetAchievementChart');
        if (!canvas) {
          console.error("Canvas element not found");
          return;
        }
      
        //const ctx = canvas.getContext('2d');
      
        // Destroy existing chart
        // if (window.targetAchievementChart && typeof window.targetAchievementChart.destroy === 'function') {
        //   window.targetAchievementChart.destroy();
        // }
      
        const ctx = document.getElementById('targetAchievementChart').getContext('2d');

        window.targetAchievementChart = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: response.labels,
            datasets: [
              {
                label: 'Target',
                data: response.targets,
                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
              },
              {
                label: 'Achievement',
                data: response.achievements,
                backgroundColor: 'rgba(255, 99, 132, 0.6)',
                borderColor: 'rgba(255, 99, 132, 1)',
                borderWidth: 1
              }
            ]
          },
          options: {
            responsive: true,
            plugins: {
              title: {
                display: true,
                text: 'Target vs Achievement with Percentage'
              },
              legend: {
                position: 'top'
              }
            },
            scales: {
              x: {
                title: {
                  display: true,
                  text: 'Year'
                }
              },
              y: {
                title: {
                  display: true,
                  text: 'Values'
                },
                beginAtZero: true
              }
              
            }
          }
        });
      }

      function renderBankRemittanceChart(response) {
        const ctx = document.getElementById('bankRemittanceChart').getContext('2d');
      
        new Chart(ctx, {
          type: 'bar',
          data: {
            labels: response.labels, // X-axis (years)
            datasets: response.datasets // Bank datasets
          },
          options: {
            responsive: true,
            plugins: {
              title: {
                display: true,
                text: 'Bank-Wise Remittance by Year'
              },
              tooltip: {
                mode: 'index',
                intersect: false
              }
            },
            scales: {
              x: {
                title: {
                  display: true,
                  text: 'Year'
                }
              },
              y: {
                title: {
                  display: true,
                  text: 'Remittance Amount'
                },
                beginAtZero: true
              }
            }
          }
        });
      }

      function renderBankRemittanceChartByYear(response) {
        const ctx = document.getElementById('bankRemittanceChartByYear').getContext('2d');

        new Chart(ctx, {
          type: 'bar',
          data: {
            labels: response.labels, // X-axis labels (Bank Names)
            datasets: response.datasets // Year datasets
          },
          options: {
            responsive: true,
            plugins: {
              title: {
                display: true,
                text: 'Bank-Wise Remittance with Yearly Contribution'
              },
              tooltip: {
                mode: 'index',
                intersect: false
              },
              legend: {
                position: 'top'
              }
            },
            scales: {
              x: {
                title: {
                  display: true,
                  text: 'Banks'
                },
                stacked: true // Enable stacking
              },
              y: {
                title: {
                  display: true,
                  text: 'Remittance Amount'
                },
                stacked: true // Enable stacking
              }
            }
          }
        });
      
        // new Chart(ctx, {
        //   type: 'bar',
        //   data: {
        //     labels: response.labels, // X-axis (Bank Names)
        //     datasets: response.datasets // Year datasets
        //   },
        //   options: {
        //     responsive: true,
        //     plugins: {
        //       title: {
        //         display: true,
        //         text: 'Bank-Wise Remittance Grouped by Year'
        //       },
        //       tooltip: {
        //         mode: 'index',
        //         intersect: false
        //       },
        //       legend: {
        //         position: 'top'
        //       }
        //     },
        //     scales: {
        //       x: {
        //         title: {
        //           display: true,
        //           text: 'Banks'
        //         }
        //       },
        //       y: {
        //         title: {
        //           display: true,
        //           text: 'Remittance Amount'
        //         },
        //         beginAtZero: true
        //       }
        //     }
        //   }
        // });
      }


});

$(document).ready(function () {
  // Fetch data for the table
  const table = $('#remittanceTable').DataTable({
    ajax: {
      url: '/getBankRemittanceDataForTable', // Backend endpoint
      dataSrc: ''
    },
    columns: [
      { data: 'bankName' },
      { data: 'year' },
      { data: 'amount' }
    ]
  });

  // Initialize the chart
  const ctx = document.getElementById('remittanceChart').getContext('2d');
  let chartInstance = null;

  // Function to render chart
  function renderChart(data) {
    // Destroy previous chart instance if it exists
    if (chartInstance) {
      chartInstance.destroy();
    }

    // Group data for chart rendering
    const groupedData = groupDataForChart(data);

    // Render new chart instance
    chartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: groupedData.labels, // X-axis labels (Bank Names)
        datasets: groupedData.datasets // Yearly datasets
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Bank-Wise Remittance'
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Bank Names'
            }
          },
          y: {
            title: {
              display: true,
              text: 'Remittance Amount'
            },
            beginAtZero: true
          }
        }
      }
    });
  }

  // Group data by bank name for chart rendering
  function groupDataForChart(data) {
    const grouped = {};
    const years = new Set();

    // Group data by bank name
    data.forEach(row => {
      const bankName = row.bankName;
      const year = row.year;
      const amount = row.amount;

      if (!grouped[bankName]) {
        grouped[bankName] = {};
      }
      grouped[bankName][year] = amount;
      years.add(year);
    });

    // Prepare datasets for Chart.js
    const labels = Object.keys(grouped);
    const yearList = Array.from(years).sort();
    const datasets = yearList.map((year, index) => {
      return {
        label: year,
        data: labels.map(bank => grouped[bank][year] || 0),
        backgroundColor: `rgba(${(index * 50) % 255}, ${(index * 80) % 255}, ${(index * 100) % 255}, 0.6)`,
        borderColor: `rgba(${(index * 50) % 255}, ${(index * 80) % 255}, ${(index * 100) % 255}, 1)`,
        borderWidth: 1
      };
    });

    return { labels, datasets };
  }

  // Update chart when table data is filtered
  table.on('search.dt', function () {
    const filteredData = table.rows({ search: 'applied' }).data().toArray();
    renderChart(filteredData);
  });

  // Initial render
  table.on('init.dt', function () {
    renderChart(table.rows().data().toArray());
  });
});

$.ajax({
  url: '/getMonthlyAnalytics',
  type: 'GET',
  success: function (response) {
    const ctx = document.getElementById('monthlyAnalyticsChart').getContext('2d');

    // Render the chart dynamically
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: response.labels, // Y-axis labels (Months)
        datasets: [
          {
            label: 'Target',
            data: response.targets, // Target data
            backgroundColor: 'rgba(54, 162, 235, 0.6)', // Blue
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          },
          {
            label: 'Achievement',
            data: response.achievements, // Achievement data
            backgroundColor: 'rgba(255, 99, 132, 0.6)', // Red
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 1
          }
        ]
      },
      options: {
        indexAxis: 'y', // Horizontal bar chart
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Monthly Analytics: Target vs Achievement'
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Amount'
            },
            beginAtZero: true
          },
          y: {
            title: {
              display: true,
              text: 'Month'
            }
          }
        }
      }
    });
  },
  error: function (error) {
    console.error("Error fetching data:", error);
  }
});


