package com.forja.gimnasio.service;

import com.forja.gimnasio.model.Asistencia;
import com.forja.gimnasio.model.EstadoMembresia;
import com.forja.gimnasio.model.Membresia;
import com.forja.gimnasio.model.Pago;
import com.forja.gimnasio.repository.EstadisticasRepository;
import com.forja.gimnasio.util.Fechas;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Arma los datos que dibujan los gráficos del inicio. */
@Service
public class DashboardService {

    private static final String[] MESES = {"ene", "feb", "mar", "abr", "may", "jun",
            "jul", "ago", "sep", "oct", "nov", "dic"};
    private static final String[] DIAS = {"lun", "mar", "mié", "jue", "vie", "sáb", "dom"};

    private final EstadisticasRepository estadisticas;
    private final MembresiaService membresias;

    public DashboardService(EstadisticasRepository estadisticas, MembresiaService membresias) {
        this.estadisticas = estadisticas;
        this.membresias = membresias;
    }

    public Map<String, Object> graficos() {
        List<Membresia> todas = membresias.listar();

        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("ingresosMensuales", ingresosMensuales());
        datos.put("visitasSemana", visitasSemana());
        datos.put("estados", estadosDeMembresias(todas));
        datos.put("planes", planesMasVendidos(todas));
        return datos;
    }

    /** Dinero cobrado en cada uno de los últimos 6 meses. */
    private Map<String, Object> ingresosMensuales() {
        YearMonth actual = YearMonth.from(Fechas.hoy());
        List<Pago> pagos = estadisticas.pagosDesde(actual.minusMonths(5).atDay(1).atStartOfDay());

        Map<YearMonth, BigDecimal> totales = new HashMap<>();
        for (Pago p : pagos) {
            totales.merge(YearMonth.from(p.getFechaPago()), p.getMonto(), BigDecimal::add);
        }

        List<String> etiquetas = new ArrayList<>();
        List<BigDecimal> valores = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = actual.minusMonths(i);
            etiquetas.add(MESES[mes.getMonthValue() - 1] + " " + (mes.getYear() % 100));
            valores.add(totales.getOrDefault(mes, BigDecimal.ZERO));
        }
        return serie(etiquetas, valores);
    }

    /** Clientes que ingresaron cada día de la última semana. */
    private Map<String, Object> visitasSemana() {
        LocalDate hoy = Fechas.hoy();
        List<Asistencia> ingresos = estadisticas.ingresosDesde(hoy.minusDays(6).atStartOfDay());

        Map<LocalDate, Integer> porDia = new HashMap<>();
        for (Asistencia a : ingresos) {
            porDia.merge(a.getFechaHora().toLocalDate(), 1, Integer::sum);
        }

        List<String> etiquetas = new ArrayList<>();
        List<Integer> valores = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate dia = hoy.minusDays(i);
            etiquetas.add(DIAS[dia.getDayOfWeek().getValue() - 1] + " " + dia.getDayOfMonth());
            valores.add(porDia.getOrDefault(dia, 0));
        }
        return serie(etiquetas, valores);
    }

    /** Cuántas membresías hay en cada estado. */
    private Map<String, Object> estadosDeMembresias(List<Membresia> todas) {
        Map<EstadoMembresia, Integer> conteo = new HashMap<>();
        for (Membresia m : todas) {
            conteo.merge(m.getEstado(), 1, Integer::sum);
        }

        List<String> etiquetas = new ArrayList<>();
        List<Integer> valores = new ArrayList<>();
        for (EstadoMembresia estado : EstadoMembresia.values()) {
            etiquetas.add(estado.getLabel());
            valores.add(conteo.getOrDefault(estado, 0));
        }
        return serie(etiquetas, valores);
    }

    /** Los 5 planes que más se han contratado (sin contar membresías canceladas). */
    private Map<String, Object> planesMasVendidos(List<Membresia> todas) {
        Map<String, Integer> conteo = new HashMap<>();
        for (Membresia m : todas) {
            if (!m.isCancelada()) {
                conteo.merge(m.getPlan().getNombre(), 1, Integer::sum);
            }
        }

        List<Map.Entry<String, Integer>> ordenados = new ArrayList<>(conteo.entrySet());
        ordenados.sort((a, b) -> b.getValue() - a.getValue());

        List<String> etiquetas = new ArrayList<>();
        List<Integer> valores = new ArrayList<>();
        for (Map.Entry<String, Integer> e : ordenados.subList(0, Math.min(5, ordenados.size()))) {
            etiquetas.add(e.getKey());
            valores.add(e.getValue());
        }
        return serie(etiquetas, valores);
    }

    private Map<String, Object> serie(List<String> etiquetas, List<? extends Number> valores) {
        Map<String, Object> serie = new LinkedHashMap<>();
        serie.put("etiquetas", etiquetas);
        serie.put("valores", valores);
        return serie;
    }
}
