package control;

import excepciones.PersistenciaException;
import fachadas.FachadaArchivos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import objetosNegocio.Bus;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import objetosServicio.Fecha;
import persistencia.Buses;
import persistencia.Conductores;
import persistencia.Empresas;
import persistencia.Rutas;
import persistencia.Viajes;

/**
 * Datos de demostración escritos en el propio código: empresas, buses, rutas, conductores y viajes.
 * Se cargan UNA sola vez, cuando el archivo de empresas está vacío (primer arranque o tras borrar los .dat).
 * Si ya hay empresas registradas no se toca nada, así nunca se pisan los datos del usuario.
 */
public final class DatosIniciales {
    private static final String[] HORAS = {"06:00", "08:30", "13:00", "16:30", "21:00"};
    private static final int VIAJES_POR_EMPRESA = 8;

    private record DatosEmpresa(String nombre, String ruc, String direccion, String telefono, String email) {
    }

    private record DatosRuta(String origen, String destino, double km, double precio, double horas, String paradas) {
    }

    private record DatosBus(String placa, String marca, int anio, int capacidad) {
    }

    private record DatosConductor(String nombre, String baseCedula, String telefono, String licencia) {
    }

    private static final DatosEmpresa[] EMPRESAS = {
            new DatosEmpresa("Flota Andina RQ", "1791234567001", "Av. Amazonas N24-15 y Colón, Quito", "022345678", "contacto@flotaandina.com"),
            new DatosEmpresa("Costa Express RQ", "0991234567001", "Av. de las Américas 450, Guayaquil", "042567890", "info@costaexpress.com"),
            new DatosEmpresa("Transportes Cumbre RQ", "0190123456001", "Calle Larga 6-45, Cuenca", "072841234", "reservas@transcumbre.com")
    };

    private static final DatosRuta[][] RUTAS = {
            {new DatosRuta("Quito", "Ambato", 135, 4.50, 2.5, "Latacunga, Salcedo"),
                    new DatosRuta("Quito", "Cuenca", 450, 15.00, 9, "Ambato, Riobamba, Alausí"),
                    new DatosRuta("Quito", "Loja", 650, 20.00, 13, "Ambato, Riobamba, Cuenca"),
                    new DatosRuta("Quito", "Riobamba", 190, 6.00, 3.5, "Latacunga, Ambato")},
            {new DatosRuta("Guayaquil", "Quito", 420, 11.00, 8, "Babahoyo, Quevedo, Santo Domingo"),
                    new DatosRuta("Guayaquil", "Cuenca", 200, 8.00, 4, "Cañar"),
                    new DatosRuta("Guayaquil", "Manta", 190, 7.00, 3.5, "Montecristi"),
                    new DatosRuta("Guayaquil", "Machala", 190, 6.00, 3.5, "Naranjal")},
            {new DatosRuta("Cuenca", "Guayaquil", 200, 8.00, 4, "Cañar"),
                    new DatosRuta("Cuenca", "Quito", 450, 15.00, 9, "Alausí, Riobamba, Ambato"),
                    new DatosRuta("Cuenca", "Loja", 210, 9.00, 4.5, "Saraguro"),
                    new DatosRuta("Cuenca", "Machala", 150, 6.00, 3, "Pasaje")}
    };

    private static final DatosBus[][] BUSES = {
            {new DatosBus("PBA-1021", "Mercedes-Benz", 2022, 40), new DatosBus("PBA-1022", "Volvo", 2021, 44), new DatosBus("PBA-1023", "Scania", 2023, 48)},
            {new DatosBus("GSA-2031", "Hino", 2020, 40), new DatosBus("GSA-2032", "Mercedes-Benz", 2022, 44), new DatosBus("GSA-2033", "Volvo", 2023, 48)},
            {new DatosBus("ABC-3041", "Scania", 2021, 40), new DatosBus("ABC-3042", "Hino", 2022, 44), new DatosBus("ABC-3043", "Mercedes-Benz", 2023, 48)}
    };

    private static final DatosConductor[][] CONDUCTORES = {
            {new DatosConductor("Carlos Mendoza Paredes", "171000001", "0991110001", "E-170001"),
                    new DatosConductor("Luis Andrade Cevallos", "171000002", "0991110002", "E-170002")},
            {new DatosConductor("Jorge Intriago Vera", "091000001", "0992220001", "E-090001"),
                    new DatosConductor("Marco Zambrano Loor", "091000002", "0992220002", "E-090002")},
            {new DatosConductor("Pablo Ordóñez Cárdenas", "011000001", "0993330001", "E-010001"),
                    new DatosConductor("Diego Calle Idrovo", "011000002", "0993330002", "E-010002")}
    };

    private DatosIniciales() {
    }

    /** Devuelve la cantidad de viajes creados (0 si ya había empresas y no se hizo nada). */
    public static int sembrarSiHaceFalta() throws PersistenciaException {
        Empresas empresasDao = new Empresas("empresas.dat");
        if (!empresasDao.lista().isEmpty()) {
            return 0;
        }
        Buses busesDao = new Buses("buses.dat");
        Rutas rutasDao = new Rutas("rutas.dat");
        Conductores conductoresDao = new Conductores("conductores.dat");

        ControladorViajes viajes = new ControladorViajes();
        FachadaArchivos fachada = FachadaArchivos.getInstance();
        int creados = 0;
        for (int e = 0; e < EMPRESAS.length; e++) {
            DatosEmpresa de = EMPRESAS[e];
            Empresa empresa = new Empresa("", de.nombre(), de.ruc(), de.direccion(), de.telefono(), de.email());
            empresasDao.agrega(empresa);

            List<Bus> buses = new ArrayList<>();
            for (DatosBus db : BUSES[e]) {
                Bus bus = new Bus("", db.placa(), "CHS" + db.placa().replace("-", ""), "MOT" + db.placa().replace("-", ""),
                        db.marca(), db.anio(), db.capacidad(), "ACTIVO", empresa);
                busesDao.agrega(bus);
                buses.add(bus);
            }
            List<Ruta> rutas = new ArrayList<>();
            for (DatosRuta dr : RUTAS[e]) {
                Ruta ruta = new Ruta("", dr.origen(), dr.destino(), dr.km(), dr.precio(), empresa, dr.horas(), dr.paradas(), 10, "Ninguna", "ACTIVA");
                rutasDao.agrega(ruta);
                rutas.add(ruta);
            }
            List<Conductor> conductores = new ArrayList<>();
            for (DatosConductor dc : CONDUCTORES[e]) {
                Conductor conductor = new Conductor("", dc.nombre(), cedulaConVerificador(dc.baseCedula()), dc.telefono(), dc.licencia(), "ACTIVO", empresa);
                conductoresDao.agrega(conductor);
                conductores.add(conductor);
            }
            creados += programarViajes(viajes, fachada, empresa, buses, rutas, conductores, e);
        }
        return creados;
    }

    /** Un viaje por día (así ningún bus se cruza consigo mismo), rotando buses, rutas, conductores y horarios. */
    private static int programarViajes(ControladorViajes controlador, FachadaArchivos fachada, Empresa empresa, List<Bus> buses,
                                       List<Ruta> rutas, List<Conductor> conductores, int desplazamientoHora) throws PersistenciaException {
        int creados = 0;
        for (int i = 0; i < VIAJES_POR_EMPRESA; i++) {
            Bus bus = buses.get(i % buses.size());
            Ruta ruta = rutas.get(i % rutas.size());
            Conductor conductor = conductores.get(i % conductores.size());
            String hora = HORAS[(i + desplazamientoHora) % HORAS.length];
            LocalDateTime salida = LocalDate.now().plusDays(1L + i).atTime(Integer.parseInt(hora.substring(0, 2)), Integer.parseInt(hora.substring(3)));
            LocalDateTime llegada = salida.plusMinutes(Math.round(ruta.getTiempoEstimado() * 60));
            ResultadoOperacion r = controlador.programarViaje("", bus.getIdBus(), ruta.getIdRuta(),
                    new Fecha(salida.getDayOfMonth(), salida.getMonthValue(), salida.getYear()),
                    new Fecha(llegada.getDayOfMonth(), llegada.getMonthValue(), llegada.getYear()),
                    empresa.getIdEmpresa(), "Programado", false, hora,
                    String.format("%02d:%02d", llegada.getHour(), llegada.getMinute()), ruta.getPrecio(), conductor.getIdConductor(), "");
            if (!r.isExito()) {
                throw new PersistenciaException("No se pudo programar un viaje de ejemplo: " + r.getMensaje());
            }
            Viaje viaje = r.getDatosComoTipo(Viaje.class);
            try {
                fachada.generarAsientosParaViaje(viaje.getIdViaje(), bus.getCapacidad());
            } catch (Exception e) {
                throw new PersistenciaException("No se pudieron generar los asientos de " + viaje.getIdViaje(), e);
            }
            creados++;
        }
        return creados;
    }

    /** Completa 9 dígitos con el dígito verificador de la cédula ecuatoriana (módulo 10), para que los datos de ejemplo sean válidos. */
    static String cedulaConVerificador(String base9) {
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int producto = (base9.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += producto > 9 ? producto - 9 : producto;
        }
        return base9 + (10 - suma % 10) % 10;
    }
}