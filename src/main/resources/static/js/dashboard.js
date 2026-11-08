// Gráficos del panel general (Chart.js)
(function () {
    var datos = window.DATOS_DASHBOARD;
    if (!datos || typeof Chart === 'undefined') {
        return;
    }

    var ACERO = '#1E2730';
    var BRASA = '#C93A12';
    var TEXTO = '#56636F';
    var LINEA = '#E3E7EB';

    Chart.defaults.font.family = "'Barlow', 'Segoe UI', system-ui, sans-serif";
    Chart.defaults.font.size = 13;
    Chart.defaults.color = TEXTO;

    function hayDatos(serie) {
        return serie.valores.some(function (v) { return v > 0; });
    }

    // Si no hay nada que mostrar, reemplaza el gráfico por un mensaje
    function vacio(idCanvas) {
        var caja = document.getElementById(idCanvas).parentNode;
        caja.innerHTML = '<p class="vacio">Aún no hay datos para mostrar.</p>';
    }

    function dinero(valor) {
        return 'S/ ' + Number(valor).toFixed(2);
    }

    var ejes = {
        x: { grid: { display: false } },
        y: { beginAtZero: true, grid: { color: LINEA }, ticks: { precision: 0 } }
    };

    // 1. Ingresos por mes
    var ingresos = datos.ingresosMensuales;
    if (hayDatos(ingresos)) {
        new Chart(document.getElementById('graficoIngresos'), {
            type: 'bar',
            data: {
                labels: ingresos.etiquetas,
                datasets: [{ label: 'Cobrado', data: ingresos.valores, backgroundColor: BRASA, borderRadius: 3 }]
            },
            options: {
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: { callbacks: { label: function (c) { return dinero(c.parsed.y); } } }
                },
                scales: {
                    x: ejes.x,
                    y: { beginAtZero: true, grid: { color: LINEA }, ticks: { callback: function (v) { return 'S/ ' + v; } } }
                }
            }
        });
    } else {
        vacio('graficoIngresos');
    }

    // 2. Visitas de la última semana
    var visitas = datos.visitasSemana;
    if (hayDatos(visitas)) {
        new Chart(document.getElementById('graficoVisitas'), {
            type: 'line',
            data: {
                labels: visitas.etiquetas,
                datasets: [{
                    label: 'Ingresos',
                    data: visitas.valores,
                    borderColor: ACERO,
                    backgroundColor: ACERO,
                    tension: 0.25,
                    pointRadius: 4
                }]
            },
            options: {
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: ejes
            }
        });
    } else {
        vacio('graficoVisitas');
    }

    // 3. Membresías por estado (mismo orden que el sistema: activa, por vencer, programada, vencida, cancelada)
    var estados = datos.estados;
    if (hayDatos(estados)) {
        new Chart(document.getElementById('graficoEstados'), {
            type: 'doughnut',
            data: {
                labels: estados.etiquetas,
                datasets: [{
                    data: estados.valores,
                    backgroundColor: ['#1D6F43', '#C98A12', '#2F6F94', '#A62A22', '#8A96A1'],
                    borderWidth: 2,
                    borderColor: '#FFFFFF'
                }]
            },
            options: {
                maintainAspectRatio: false,
                cutout: '58%',
                plugins: { legend: { position: 'right' } }
            }
        });
    } else {
        vacio('graficoEstados');
    }

    // 4. Planes más contratados
    var planes = datos.planes;
    if (hayDatos(planes)) {
        new Chart(document.getElementById('graficoPlanes'), {
            type: 'bar',
            data: {
                labels: planes.etiquetas,
                datasets: [{ label: 'Membresías', data: planes.valores, backgroundColor: ACERO, borderRadius: 3 }]
            },
            options: {
                indexAxis: 'y',
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    x: { beginAtZero: true, grid: { color: LINEA }, ticks: { precision: 0 } },
                    y: { grid: { display: false } }
                }
            }
        });
    } else {
        vacio('graficoPlanes');
    }
})();
