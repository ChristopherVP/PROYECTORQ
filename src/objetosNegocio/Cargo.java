package objetosNegocio;

import java.util.ArrayList;
import java.util.List;

/**
 * Cargos que puede tener una cuenta administrativa. El cargo es el título del colaborador
 * (por ejemplo "Jefe de finanzas"); el rol sigue decidiendo qué puede hacer en el sistema.
 */
public enum Cargo {
    ADMINISTRADOR_PRINCIPAL("Administrador principal", Rol.ADMINISTRADOR),
    GERENTE_GENERAL("Gerente general", Rol.ADMINISTRADOR),
    SUPERVISOR_OPERACIONES("Supervisor de operaciones", Rol.ADMINISTRADOR),
    COORDINADOR_RUTAS("Coordinador de rutas y viajes", Rol.ADMINISTRADOR),
    JEFE_FINANZAS("Jefe de finanzas y recargas", Rol.ADMINISTRADOR),
    ATENCION_CLIENTE("Atención al cliente", Rol.ADMINISTRADOR),
    OPERADOR_EMPRESA("Operador de empresa", Rol.OPERADOR),
    SUPERVISOR_EMPRESA("Supervisor de empresa", Rol.OPERADOR),
    ASISTENTE_EMPRESA("Asistente de empresa", Rol.OPERADOR);

    /** Longitud máxima que se guarda en el archivo de usuarios. */
    public static final int LONGITUD_MAXIMA = 40;

    private final String etiqueta;
    private final Rol rol;

    Cargo(String etiqueta, Rol rol) {
        this.etiqueta = etiqueta;
        this.rol = rol;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public Rol getRol() {
        return rol;
    }

    /** Devuelve el cargo o null si el texto no corresponde a ninguno. */
    public static Cargo desdeEtiqueta(String texto) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim();
        for (Cargo c : values()) {
            if (c.etiqueta.equalsIgnoreCase(t) || c.name().equalsIgnoreCase(t)) {
                return c;
            }
        }
        return null;
    }

    /** Cargos que se pueden asignar a un colaborador nuevo de ese rol (el principal no se asigna a nadie más). */
    public static List<Cargo> asignablesA(Rol rol) {
        List<Cargo> lista = new ArrayList<>();
        for (Cargo c : values()) {
            if (c.rol == rol && c != ADMINISTRADOR_PRINCIPAL) {
                lista.add(c);
            }
        }
        return lista;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}