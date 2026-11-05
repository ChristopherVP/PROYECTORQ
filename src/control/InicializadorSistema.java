package control;

import excepciones.PersistenciaException;
import fachadas.FachadaArchivos;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import objetosNegocio.Bus;
import objetosNegocio.Conductor;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import objetosServicio.Fecha;
import persistencia.Buses;
import persistencia.Conductores;
import persistencia.Empresas;
import persistencia.MigradorDatos;
import persistencia.Movimientos;
import persistencia.Novedades;
import persistencia.Pasajeros;
import persistencia.Rutas;
import persistencia.Usuarios;
import persistencia.Viajes;

/**
 * Prepara los datos al arrancar. NUNCA borra información existente: crea los archivos que falten,
 * migra las cuentas del formato anterior, hace un respaldo y deja viajes futuros de ejemplo la primera vez.
 */
public final class InicializadorSistema {
    private static final String MARCA_VIAJES = "viajes-ejemplo.generado";
    private static final int VIAJES_DE_EJEMPLO = 18;
    private static final String[] HORAS = {"06:00", "08:30", "13:00", "16:30", "21:00"};

    public interface Progreso {
        void avance(int porcentaje, String mensaje);
    }

    public static class Resumen {
        public int usuariosMigrados;
        public int viajesCreados;
        public String carpetaRespaldo;
        public final List<String> advertencias = new ArrayList<>();
    }

    private InicializadorSistema() {
    }

    public static Resumen preparar(Progreso progreso) {
        Resumen resumen = new Resumen();
        notificar(progreso, 5, "Verificando archivos de datos...");
        new Empresas("empresas.dat");
        new Pasajeros("pasajeros.dat");
        new Conductores("conductores.dat");
        new Movimientos("movimientos.dat");
        new Novedades("novedades.dat");
        FachadaArchivos.getInstance();

        File usuarios = new File("usuarios.dat");
        try {
            if (MigradorDatos.esFormatoV1(usuarios)) {
                notificar(progreso, 15, "Protegiendo las contraseñas de las cuentas existentes...");
                resumen.usuariosMigrados = MigradorDatos.migrarUsuariosV1("usuarios.dat",
                        (hechos, total) -> notificar(progreso, 15 + (hechos * 40 / Math.max(1, total)), "Protegiendo cuentas (" + hechos + "/" + total + ")..."));
            } else {
                new Usuarios("usuarios.dat");
            }
        } catch (PersistenciaException e) {
            resumen.advertencias.add("Migración de usuarios: " + e.getMessage());
        }

        notificar(progreso, 60, "Creando respaldo de seguridad de tus datos...");
        try {
            File carpeta = RespaldoDatos.respaldar(new File("."));
            resumen.carpetaRespaldo = carpeta == null ? null : carpeta.getPath();
        } catch (IOException e) {
            resumen.advertencias.add("Respaldo: " + e.getMessage());
        }

        notificar(progreso, 75, "Preparando viajes disponibles...");
        try {
            resumen.viajesCreados = generarViajesDeEjemploSiHaceFalta();
        } catch (PersistenciaException | IOException e) {
            resumen.advertencias.add("Viajes de ejemplo: " + e.getMessage());
        }
        notificar(progreso, 100, "Todo listo");
        return resumen;
    }

    /**
     * Los viajes sembrados originalmente tienen fechas pasadas. La primera vez que no haya viajes futuros
     * comprables, se programan algunos con los buses, rutas y conductores que ya existen. Se hace una sola vez.
     */
    static int generarViajesDeEjemploSiHaceFalta() throws PersistenciaException, IOException {
        File marca = new File(MARCA_VIAJES);
        if (marca.exists()) {
            return 0;
        }
        Viajes viajesDao = new Viajes("viajes.dat");
        long comprables = viajesDao.lista().stream().filter(ControladorCompras::esComprable).count();
        if (comprables >= 6) {
            Files.write(marca.toPath(), "omitido: ya habia viajes futuros".getBytes());
            return 0;
        }
        List<Bus> buses = new ArrayList<>();
        for (Bus b : new Buses("buses.dat").lista()) {
            String estado = b.getEstado() == null ? "" : b.getEstado();
            if (!"Mantenimiento".equalsIgnoreCase(estado) && !"INACTIVO".equalsIgnoreCase(estado) && b.getEmpresa() != null) {
                buses.add(b);
            }
        }
        List<Ruta> rutas = new Rutas("rutas.dat").lista();
        List<Conductor> conductores = new Conductores("conductores.dat").lista();
        ControladorViajes controlador = new ControladorViajes();
        FachadaArchivos fachada = FachadaArchivos.getInstance();
        int creados = 0;
        for (int intento = 0; intento < buses.size() * 3 && creados < VIAJES_DE_EJEMPLO && !buses.isEmpty(); intento++) {
            Bus bus = buses.get(intento % buses.size());
            String idEmpresa = bus.getEmpresa().getIdEmpresa();
            Ruta ruta = null;
            int vuelta = intento / buses.size();
            int vistas = 0;
            for (Ruta r : rutas) {
                if (r.getEmpresa() != null && idEmpresa.equals(r.getEmpresa().getIdEmpresa()) && r.getPrecio() > 0
                        && !"INACTIVA".equalsIgnoreCase(r.getEstado()) && !"INACTIVO".equalsIgnoreCase(r.getEstado())) {
                    if (vistas++ == vuelta) {
                        ruta = r;
                        break;
                    }
                }
            }
            Conductor conductor = null;
            for (Conductor c : conductores) {
                if (c.getEmpresa() != null && idEmpresa.equals(c.getEmpresa().getIdEmpresa()) && "ACTIVO".equalsIgnoreCase(c.getEstado())) {
                    conductor = c;
                    break;
                }
            }
            if (ruta == null || conductor == null) {
                continue;
            }
            int offsetDias = 1 + (creados % 12);
            String hora = HORAS[creados % HORAS.length];
            LocalDateTime salida = LocalDate.now().plusDays(offsetDias).atTime(Integer.parseInt(hora.substring(0, 2)), Integer.parseInt(hora.substring(3)));
            double horas = ruta.getTiempoEstimado() > 0 ? ruta.getTiempoEstimado() : 4;
            LocalDateTime llegada = salida.plusMinutes(Math.round(horas * 60));
            ResultadoOperacion r = controlador.programarViaje("", bus.getIdBus(), ruta.getIdRuta(),
                    new Fecha(salida.getDayOfMonth(), salida.getMonthValue(), salida.getYear()),
                    new Fecha(llegada.getDayOfMonth(), llegada.getMonthValue(), llegada.getYear()),
                    idEmpresa, "Programado", false, hora,
                    String.format("%02d:%02d", llegada.getHour(), llegada.getMinute()), ruta.getPrecio(), conductor.getIdConductor(), "");
            if (r.isExito()) {
                Viaje v = r.getDatosComoTipo(Viaje.class);
                try {
                    fachada.generarAsientosParaViaje(v.getIdViaje(), bus.getCapacidad());
                    creados++;
                } catch (Exception e) {
                    throw new PersistenciaException("No se pudieron generar los asientos de " + v.getIdViaje(), e);
                }
            }
        }
        Files.write(marca.toPath(), ("creados: " + creados).getBytes());
        return creados;
    }

    private static void notificar(Progreso progreso, int porcentaje, String mensaje) {
        if (progreso != null) {
            progreso.avance(Math.min(100, porcentaje), mensaje);
        }
    }
}
