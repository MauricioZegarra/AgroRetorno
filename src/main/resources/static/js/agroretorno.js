/*
 * AgroRetorno - validación en el navegador (RNF05) y confirmaciones.
 * La validación del frontend es una ayuda: el backend vuelve a validar todo.
 */
(function () {
    'use strict';

    // 1) Formularios con validación HTML5 + estilos de Bootstrap
    document.querySelectorAll('form.needs-validation').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopImmediatePropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // 2) Confirmación para acciones importantes: <form data-confirm="mensaje">
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (event.defaultPrevented) {
                return;
            }
            if (!window.confirm(form.getAttribute('data-confirm'))) {
                event.preventDefault();
            }
        });
    });

    // 3) Evita envíos dobles deshabilitando el botón después del envío válido
    document.querySelectorAll('form').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (event.defaultPrevented) {
                return;
            }
            var boton = form.querySelector('button[type="submit"]');
            if (boton) {
                setTimeout(function () { boton.disabled = true; }, 0);
            }
        });
    });

    // 4) Devolución: suma en vivo y control contra unidades pendientes
    var formDevolucion = document.getElementById('form-devolucion');
    if (formDevolucion) {
        var pendiente = parseInt(formDevolucion.getAttribute('data-pendiente'), 10);
        var campos = ['cantidadBuenEstado', 'cantidadDanada', 'cantidadMantenimiento']
            .map(function (id) { return document.getElementById(id); });
        var total = document.getElementById('total-devolucion');
        var observaciones = document.getElementById('observaciones');
        var actualizar = function () {
            var suma = campos.reduce(function (acc, c) { return acc + (parseInt(c.value, 10) || 0); }, 0);
            total.textContent = suma;
            var invalido = suma <= 0 || suma > pendiente;
            campos[0].setCustomValidity(invalido
                ? 'La suma debe estar entre 1 y ' + pendiente + ' unidades' : '');
            var requiereObs = (parseInt(campos[1].value, 10) || 0) + (parseInt(campos[2].value, 10) || 0) > 0;
            observaciones.required = requiereObs;
        };
        campos.forEach(function (c) { c.addEventListener('input', actualizar); });
        actualizar();
    }

    // 5) Formulario de solicitud: muestra la disponibilidad del material elegido
    var selectMaterial = document.getElementById('materialId');
    var infoDisponible = document.getElementById('info-disponible');
    if (selectMaterial && infoDisponible) {
        var mostrar = function () {
            var opcion = selectMaterial.options[selectMaterial.selectedIndex];
            var disp = opcion ? opcion.getAttribute('data-disponible') : null;
            infoDisponible.textContent = disp !== null ? disp + ' unidades disponibles' : '';
        };
        selectMaterial.addEventListener('change', mostrar);
        mostrar();
    }
})();
