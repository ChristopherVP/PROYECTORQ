package objetosNegocio;

/** Roles del sistema. El texto (etiqueta) es el que se guarda en el campo "tipo" del usuario. */
public enum Rol {
    ADMINISTRADOR("Administrador"),
    OPERADOR("Operador"),
    CONDUCTOR("Conductor"),
    PASAJERO("Pasajero");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean esAdministrativo() {
        return this == ADMINISTRADOR || this == OPERADOR;
    }

    /** Devuelve el rol o null si el texto no corresponde a ninguno. */
    public static Rol desdeEtiqueta(String texto) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim();
        for (Rol r : values()) {
            if (r.etiqueta.equalsIgnoreCase(t) || r.name().equalsIgnoreCase(t)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
