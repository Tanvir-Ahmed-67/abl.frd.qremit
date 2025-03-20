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
                backgroundColor: 'rgba(235, 69, 18, 0.6)',
                borderColor: 'rgb(10, 131, 243)',
                borderWidth: 1
              },
              {
                label: 'Achievement',
                data: response.achievements,
                backgroundColor: 'rgba(54, 190, 134, 0.79)',
                borderColor: 'rgb(14, 226, 49)',
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

//document.addEventListener('DOMContentLoaded', function () {
  // Fetch remittance data for Agrani Bank
  $.ajax({
    url: '/getAgraniBankRemittance',
    type: 'GET',
    success: function (response) {
      renderCards(response);
    },
    error: function (error) {
      console.error("Error fetching data:", error);
    }
  });

  // Function to render cards with random colors
  function renderCards(data) {
    const container = document.getElementById('remittanceCards');
    container.innerHTML = ''; // Clear any existing content

    // Define a list of random background colors
    const colors = [
      'bg-primary', // Blue
  
      'bg-success', // Green
      'bg-danger', // Red
      'bg-warning', // Yellow
      'bg-info', // Cyan
      'bg-light', // Light Gray
    ];

    data.forEach(item => {
      // Pick a random color for each card
      const randomColor = colors[Math.floor(Math.random() * colors.length)];

      const card = `
        <div class="col-md-2 mb-1">
          <div class="card ${randomColor} text-white">
            <div class="card-body text-center">
              <h5 class="card-title">Year: ${item.year}</h5>
              <p class="card-text">
                Total Remittance: <strong> $${item.totalRemittance.toLocaleString()}  Million</strong>
              </p>
            </div>
          </div>
        </div>
      `;
      container.innerHTML += card;
    });
  }





   // Fetch data
   $.ajax({
    url: '/getTTComparisonData',
    type: 'GET',
    success: function (response) {

      console.log("Response received:", response);

        // Check if response is already a JSON object
        if (typeof response === 'string') {
            response = JSON.parse(response); // Parse only if necessary
        }
      console.log("Response received:", response);
      renderLineGraph(response);
    },
    error: function (error) {
      console.error("Error fetching data:", error);
    }
  });
  //console.log("Response received:", response);
  // Render the line graph
  function renderLineGraph(data) {
    const ctx = document.getElementById('yearlyComparisonChart').getContext('2d');

    // Extract months for X-axis
    const months = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];

    // Extract data for each year
    const datasets = [];
    Object.entries(data).forEach(([year, monthlyData]) => {
      const spotCashData = months.map(month => monthlyData[month]?.spot_cash || 0);
      const accountPayeeData = months.map(month => monthlyData[month]?.account_payee || 0);
      const cocData = months.map(month => monthlyData[month]?.coc || 0);
      const beftnData = months.map(month => monthlyData[month]?.beftn || 0);

      // Add dataset for each transaction type
      datasets.push(
        {
          label: `Spot Cash (${year})`,
          data: spotCashData,
          borderColor: 'rgba(255, 99, 132, 1)',
          backgroundColor: 'rgba(255, 99, 132, 0.2)',
          borderWidth: 3,
          fill: false
        },
        {
          label: `Account Payee (${year})`,
          data: accountPayeeData,
          borderColor: 'rgba(54, 162, 235, 1)',
          backgroundColor: 'rgba(54, 162, 235, 0.2)',
          borderWidth: 3,
          fill: false
        },
        {
          label: `COC (${year})`,
          data: cocData,
          borderColor: 'rgba(255, 206, 86, 1)',
          backgroundColor: 'rgba(255, 206, 86, 0.2)',
          borderWidth: 3,
          fill: false
        },
        {
          label: `BEFTN (${year})`,
          data: beftnData,
          borderColor: 'rgba(75, 192, 192, 1)',
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          borderWidth: 3,
          fill: false
        }
      );
    });

    // Render the chart
    new Chart(ctx, {
      type: 'line',
      data: {
        labels: months, // X-axis: Months
        datasets: datasets
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Comparison of Transaction Types by Year'
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Month'
            }
          },
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Transaction Amount'
            }
          }
        }
      }
    });
  }

  // Fetch data from the backend
  $.ajax({
    url: '/getCountryPieChartData',
    type: 'GET',
    success: function (response) {
      renderCountryPieChart(response);
    },
    error: function (error) {
      console.error("Error fetching data:", error);
    }
  });

  // Render the pie chart
  function renderCountryPieChart(data) {
    const ctx = document.getElementById('countryPieChart').getContext('2d');

    // Sort the data by amount (descending)
    const sortedData = data.sort((a, b) => b.amount - a.amount);

    // Prepare labels, data, and total amount for percentage calculation
    const labels = sortedData.map(item => item.country);
    const values = sortedData.map(item => item.amount);
    const totalAmount = values.reduce((sum, value) => sum + value, 0);

    // Vibrant colors for the chart
    const vibrantColors = [
      "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF",
      "#FF9F40", "#FFCD56", "#C9CBCF", "#8A9A5B", "#FF6F61"
    ];

    // Create the chart
    new Chart(ctx, {
      type: 'pie',
      data: {
        labels: labels,
        datasets: [{
          data: values,
          backgroundColor: vibrantColors,
          borderColor: vibrantColors.map(color => color.replace("0.5", "1")), // More vibrant borders
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            display: true,
            position: 'right', // Legend on the right
            labels: {
              generateLabels: function (chart) {
                const data = chart.data;
                return data.labels.map((label, index) => {
                  const value = data.datasets[0].data[index];
                  const percentage = ((value / totalAmount) * 100).toFixed(2);
                  return {
                    text: `${label}: ${value.toFixed(2)} M USD (${percentage}%)`,
                    fillStyle: data.datasets[0].backgroundColor[index],
                    strokeStyle: data.datasets[0].borderColor[index],
                    lineWidth: data.datasets[0].borderWidth,
                    hidden: false,
                    index: index
                  };
                });
              }
            }
          },
          tooltip: {
            callbacks: {
              label: function (context) {
                const label = context.label || '';
                const value = context.raw || 0;
                const percentage = ((value / totalAmount) * 100).toFixed(2);
                return `${label}: ${value.toFixed(2)} M USD (${percentage}%)`;
              }
            }
          },
          datalabels: {
            color: '#fff', // White text on slices
            formatter: (value, context) => {
              const percentage = ((value / totalAmount) * 100).toFixed(1);
              return `${percentage}%`; // Show percentage
            },
            font: {
              weight: 'bold',
              size: 10
            }
          }
        }
      },
      plugins: [ChartDataLabels] // Activate the Data Labels plugin
    });
  }

   // Fetch data from the backend
   $.ajax({
    url: '/getExchangeWiseData',
    type: 'GET',
    success: function (response) {
      renderExchangeChart(response);
    },
    error: function (error) {
      console.error("Error fetching data:", error);
    }
  });

  // Render the chart
  function renderExchangeChart(data) {
    const ctx = document.getElementById('exchangeChart').getContext('2d');

    // Extract data for the chart
    const labels = data.map(item => item.ex_name); // Exchange names
    const usdAmounts = data.map(item => item.usd_amount_million); // USD amounts in millions
    const transactionCounts = data.map(item => item.no_of_tt); // Number of transactions

    // Create the chart
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'USD Amount (Million)',
            data: usdAmounts,
            backgroundColor: 'rgba(54, 162, 235, 0.6)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1,
            yAxisID: 'y'
          },
          {
            label: 'Number of Transactions',
            data: transactionCounts,
            backgroundColor: 'rgba(255, 99, 132, 0.6)',
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 1,
            type: 'line', // Line chart for transaction count
            yAxisID: 'y1'
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          tooltip: {
            callbacks: {
              label: function (context) {
                if (context.dataset.label === 'USD Amount (Million)') {
                  return `${context.dataset.label}: $${context.raw.toFixed(2)}M`;
                } else if (context.dataset.label === 'Number of Transactions') {
                  return `${context.dataset.label}: ${context.raw}`;
                }
              }
            }
          },
          legend: {
            display: true,
            position: 'top'
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Exchange Name'
            }
          },
          y: {
            title: {
              display: true,
              text: 'USD Amount (Million)'
            },
            position: 'left'
          },
          y1: {
            title: {
              display: true,
              text: 'Number of Transactions'
            },
            position: 'right',
            grid: {
              drawOnChartArea: false // Prevent grid lines from overlapping
            }
          }
        }
      }
    });
  }

    // Fetch data from the backend
  // $.ajax({
  //   url: '/getCountryBubbleData',
  //   type: 'GET',
  //   success: function (response) {
  //     renderCountryBubbleMap(response);
  //   },
  //   error: function (error) {
  //     console.error("Error fetching data:", error);
  //   }
  // });

  // Render the bubble map
  // function renderCountryBubbleMap(data) {
  //   const ctx = document.getElementById('countryBubbleMap').getContext('2d');

  //   // Fetch world map GeoJSON
  //   fetch('https://cdn.jsdelivr.net/npm/world-atlas@2.0.2/countries-110m.json')
  //     .then(response => response.json())
  //     .then(worldMap => {
  //       // Convert TopoJSON to GeoJSON
  //       const countries = topojson.feature(worldMap, worldMap.objects.countries).features;

  //       // Prepare bubble data
  //       const bubbles = data.map(item => ({
  //         latitude: item.latitude,
  //         longitude: item.longitude,
  //         radius: Math.sqrt(item.amount) / 1000, // Adjust the size of the bubbles
  //         value: item.amount,
  //         backgroundColor: 'rgba(54, 162, 235, 0.5)',
  //         borderColor: 'rgba(54, 162, 235, 1)',
  //         borderWidth: 1,
  //         label: item.country
  //       }));

  //       // Create the map
  //       new Chart(ctx, {
  //         type: 'bubbleMap',
  //         data: {
  //           labels: countries.map(c => c.properties.name),
  //           datasets: [{
  //             label: 'Country Amount',
  //             data: bubbles
  //           }]
  //         },
  //         options: {
  //           responsive: true,
  //           maintainAspectRatio: false,
  //           scales: {
  //             xy: {
  //               projection: 'equalEarth'
  //             }
  //           },
  //           plugins: {
  //             tooltip: {
  //               callbacks: {
  //                 label: function (context) {
  //                   const bubble = context.raw;
  //                   return `${bubble.label}: $${bubble.value.toLocaleString()}`;
  //                 }
  //               }
  //             }
  //           }
  //         }
  //       });
  //     })
  //     .catch(error => console.error("Error loading map:", error));
  // }


  // fetch('https://cdn.jsdelivr.net/npm/us-atlas/states-10m.json')
  // .then((r) => r.json())
  // .then((states10m) => {
  //   const nation = ChartGeo.topojson.feature(states10m, states10m.objects.nation).features[0];
  //   const states = ChartGeo.topojson.feature(states10m, states10m.objects.states).features;
  //   const chart = new Chart(document.getElementById('canvas').getContext('2d'), {
  //     type: 'choropleth',
  //     data: {
  //       labels: states.map((d) => d.properties.name),
  //       datasets: [
  //         {
  //           label: 'States',
  //           outline: nation,
  //           data: states.map((d) => ({
  //             feature: d,
  //             value: Math.random() * 11,
  //           })),
  //         },
  //       ],
  //     },
  //     options: {
  //       lscales: {
  //         projection: {
  //           axis: 'x',
  //           projection: 'albersUsa',
  //         },
  //         color: {
  //           axis: 'x',
  //           quantize: 5,
  //           legend: {
  //             position: 'bottom-right',
  //             align: 'right',
  //           },
  //         },
  //       },
  //     },
  //   });
  // });
  
//});


