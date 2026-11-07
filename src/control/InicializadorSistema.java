package control;

import excepciones.PersistenciaException;
import fachadas.FachadaArchivos;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import persistencia.Conductores;
import persistencia.Empresas;
import persistencia.Movimientos;
import persistencia.Novedades;
import persistencia.Pasajeros;
import persistencia.Usuarios;

/**
 * Prepara los datos al arrancar. NUNCA borra información existente: crea los archivos que falten, asegura la cuenta
 * del administrador principal, hace un respaldo de lo que ya existía y, solo si el sistema está vacío, carga los
 * datos de demostración definidos en {@link DatosIniciales}.
 */
public final class InicializadorSistema {

    public interface Progreso {
        void avance(int porcentaje, String mensaje);
    }

    public static class Resumen {
        public boolean administradorCreado;
        public int viajesCreados;
        public String carpetaRespaldo;
        public final List<String> advertencias = new ArrayList<>();
    }

    private InicializadorSistema() {
    }

    public static Resumen preparar(Progreso progreso) {
        Resumen resumen = new Resumen();
        notificar(progreso, 10, "Verificando archivos de datos...");
        new Empresas("empresas.dat");
        new Pasajeros("pasajeros.dat");
        new Conductores("conductores.dat");
        new Movimientos("movimientos.dat");
        new Novedades("novedades.dat");
        new Usuarios("usuarios.dat");
        FachadaArchivos.getInstance();

        notificar(progreso, 30, "Creando respaldo de seguridad de tus datos...");
        try {
            File carpeta = hayDatosGuardados() ? RespaldoDatos.respaldar(new File(".")) : null;
            resumen.carpetaRespaldo = carpeta == null ? null : carpeta.getPath();
        } catch (IOException e) {
            resumen.advertencias.add("Respaldo: " + e.getMessage());
        }

        notificar(progreso, 50, "Preparando la cuenta del administrador principal...");
        ResultadoOperacion admin = new ControladorUsuarios().asegurarAdministradorPrincipal();
        if (!admin.isExito()) {
            resumen.advertencias.add("Administrador principal: " + admin.getMensaje());
        } else {
            resumen.administradorCreado = admin.getMensaje().startsWith("Administrador principal creado");
        }

        notificar(progreso, 70, "Cargando datos iniciales...");
        try {
            resumen.viajesCreados = DatosIniciales.sembrarSiHaceFalta();
        } catch (PersistenciaException e) {
            resumen.advertencias.add("Datos iniciales: " + e.getMessage());
        }
        notificar(progreso, 100, "Todo listo");
        return resumen;
    }

    /** true si algún archivo .dat ya tiene información (no tiene sentido respaldar archivos vacíos). */
    private static boolean hayDatosGuardados() {
        File[] datos = new File(".").listFiles((d, n) -> n.endsWith(".dat"));
        if (datos == null) {
            return false;
        }
        for (File f : datos) {
            if (f.length() > 0) {
                return true;
            }
        }
        return false;
    }

    private static void notificar(Progreso progreso, int porcentaje, String mensaje) {
        if (progreso != null) {
            progreso.avance(Math.min(100, porcentaje), mensaje);
        }
    }
}