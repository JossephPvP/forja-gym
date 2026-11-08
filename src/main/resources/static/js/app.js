// Pide confirmación antes de enviar formularios marcados con data-confirm
document.addEventListener('submit', function (e) {
    var mensaje = e.target.getAttribute('data-confirm');
    if (mensaje && !window.confirm(mensaje)) {
        e.preventDefault();
    }
});

// Formulario de membresía: muestra el total y la fecha de término según el plan elegido
(function () {
    var plan = document.getElementById('planId');
    var inicio = document.getElementById('fechaInicio');
    var monto = document.getElementById('resumenMonto');
    var fin = document.getElementById('resumenFin');
    if (!plan || !inicio || !monto || !fin) return;

    function actualizar() {
        var opcion = plan.options[plan.selectedIndex];
        if (!opcion || !opcion.getAttribute('data-dias')) {
            monto.textContent = '—';
            fin.textContent = '—';
            return;
        }
        var precio = Number(opcion.getAttribute('data-precio'));
        var dias = Number(opcion.getAttribute('data-dias'));
        monto.textContent = 'S/ ' + precio.toFixed(2);

        if (inicio.value) {
            var fecha = new Date(inicio.value + 'T00:00:00');
            fecha.setDate(fecha.getDate() + dias - 1);
            fin.textContent = fecha.toLocaleDateString('es-PE');
        } else {
            fin.textContent = '—';
        }
    }

    plan.addEventListener('change', actualizar);
    inicio.addEventListener('change', actualizar);
    actualizar();
})();
