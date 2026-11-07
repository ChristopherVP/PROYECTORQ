package objetosServicio;

/**
 * Datos de la cuenta del administrador principal que el sistema crea por sí solo la primera vez.
 * Se pueden cambiar con las variables de entorno RQ_ADMIN_USUARIO y RQ_ADMIN_PASSWORD.
 * Si no existen, se usa una contraseña TEMPORAL que obliga a cambiarla en el primer ingreso.
 */
public final class ConfiguracionAdmin {
    public static final String VARIABLE_USUARIO = "RQ_ADMIN_USUARIO";
    public static final String VARIABLE_PASSWORD = "RQ_ADMIN_PASSWORD";

    public static final String USUARIO_POR_DEFECTO = "admin";
    public static final String NOMBRE_ADMIN = "Administrador Principal";
    /** Cédula reservada (no es una cédula real válida, por eso nadie puede registrarse con ella). */
    public static final String CEDULA_RESERVADA = "0000000000";

    private static final String PASSWORD_TEMPORAL = "CambiarAdmin1";

    private ConfiguracionAdmin() {
    }

    public static String usuarioInicial() {
        String valor = System.getenv(VARIABLE_USUARIO);
        return valor == null || valor.isBlank() ? USUARIO_POR_DEFECTO : valor.trim();
    }

    public static String passwordInicial() {
        String valor = System.getenv(VARIABLE_PASSWORD);
        return valor == null || valor.isBlank() ? PASSWORD_TEMPORAL : valor;
    }

    public static boolean usaPasswordPorDefecto() {
        String valor = System.getenv(VARIABLE_PASSWORD);
        return valor == null || valor.isBlank();
    }

    /** Contraseña temporal conocida; solo sirve para el primer ingreso. */
    public static String passwordTemporal() {
        return PASSWORD_TEMPORAL;
    }
}