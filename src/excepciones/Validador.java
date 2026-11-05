//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package excepciones;

public class Validador {
    private static String mensajeErrorCedula = "";
    private static String mensajeErrorEmail = "";
    private static String mensajeErrorRuc = "";

    /**
     * Valida una cédula ecuatoriana: 10 dígitos, provincia 01-24 (o 30 = ecuatorianos en el exterior),
     * tercer dígito menor a 6 (persona natural) y dígito verificador por módulo 10.
     * Devuelve null si es válida o el mensaje del problema.
     */
    public static String errorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return "La cédula no puede estar vacía";
        }
        String c = cedula.trim();
        if (c.length() != 10) {
            return "La cédula debe tener exactamente 10 dígitos";
        }
        if (!c.matches("\\d+")) {
            return "La cédula debe contener solo números";
        }
        int provincia = Integer.parseInt(c.substring(0, 2));
        if (!((provincia >= 1 && provincia <= 24) || provincia == 30)) {
            return "Código de provincia inválido (debe ser 01-24 o 30)";
        }
        if (c.charAt(2) - '0' > 5) {
            return "El tercer dígito de la cédula debe ser menor a 6";
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int producto = (c.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += producto > 9 ? producto - 9 : producto;
        }
        int verificador = (10 - (suma % 10)) % 10;
        if (verificador != c.charAt(9) - '0') {
            return "La cédula no es válida (dígito verificador incorrecto)";
        }
        return null;
    }

    public static boolean validarCedulaEcuatoriana(String cedula) {
        String error = errorCedula(cedula);
        mensajeErrorCedula = error == null ? "" : error;
        return error == null;
    }

    /** Email opcional: vacío es válido; si hay texto debe tener formato correcto. Devuelve null o el mensaje. */
    public static String errorEmailOpcional(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        if (!validarEmail(email.trim())) {
            return mensajeErrorEmail;
        }
        if (email.trim().length() > 50) {
            return "El email no puede superar los 50 caracteres";
        }
        return null;
    }

    /** Teléfono opcional: vacío es válido; si hay texto, 9 o 10 dígitos. */
    public static String errorTelefonoOpcional(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        return validarTelefono(telefono) ? null : "El teléfono debe tener 9 o 10 dígitos";
    }

    public static String errorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio";
        }
        if (!nombre.trim().matches("^\\p{L}[\\p{L} .'-]{2,49}$")) {
            return "El nombre debe tener entre 3 y 50 caracteres y solo letras o espacios";
        }
        return null;
    }

    public static String errorLogin(String login) {
        if (login == null || !login.matches("^[A-Za-z0-9][A-Za-z0-9._-]{3,19}$")) {
            return "El usuario debe tener entre 4 y 20 caracteres (letras, números, punto, guion o guion bajo)";
        }
        return null;
    }

    /** Política para contraseñas nuevas: 8-40 caracteres, mayúscula, minúscula, número y sin espacios. */
    public static String errorContrasena(String clave) {
        if (clave == null || clave.isEmpty()) {
            return "La contraseña es obligatoria";
        }
        if (clave.length() < 8 || clave.length() > 40) {
            return "La contraseña debe tener entre 8 y 40 caracteres";
        }
        if (clave.contains(" ")) {
            return "La contraseña no puede contener espacios";
        }
        if (clave.equals(clave.toLowerCase()) || clave.equals(clave.toUpperCase()) || !clave.matches(".*\\d.*")) {
            return "La contraseña debe incluir mayúscula, minúscula y número";
        }
        return null;
    }

    /** 0 = débil, 1 = media, 2 = fuerte (para el indicador visual). */
    public static int fortaleza(String clave) {
        if (clave == null || clave.length() < 8) {
            return 0;
        }
        int puntos = 0;
        if (!clave.equals(clave.toLowerCase())) puntos++;
        if (!clave.equals(clave.toUpperCase())) puntos++;
        if (clave.matches(".*\\d.*")) puntos++;
        if (clave.matches(".*[^A-Za-z0-9].*")) puntos++;
        if (clave.length() >= 12) puntos++;
        return puntos <= 2 ? 0 : (puntos == 3 ? 1 : 2);
    }

    public static String errorPreguntaSeguridad(String pregunta) {
        if (pregunta == null || pregunta.trim().length() < 10 || pregunta.trim().length() > 80) {
            return "La pregunta de seguridad debe tener entre 10 y 80 caracteres";
        }
        return null;
    }

    public static String errorRespuestaSeguridad(String respuesta) {
        String r = objetosServicio.Seguridad.normalizarRespuesta(respuesta);
        if (r.length() < 3 || r.length() > 40) {
            return "La respuesta de seguridad debe tener entre 3 y 40 caracteres";
        }
        return null;
    }

    /** Algoritmo de Luhn para números de tarjeta (13 a 19 dígitos). */
    public static boolean esTarjetaValida(String numero) {
        if (numero == null || !numero.matches("\\d{13,19}")) {
            return false;
        }
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int d = numero.charAt(i) - '0';
            if (duplicar) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            suma += d;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }

    public static boolean validarEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (email.contains(" ")) {
                mensajeErrorEmail = "El email no puede contener espacios";
                return false;
            } else if (!email.equals(email.toLowerCase())) {
                mensajeErrorEmail = "El email no puede contener mayúsculas";
                return false;
            } else if (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                mensajeErrorEmail = "El formato del email no es válido";
                return false;
            } else {
                mensajeErrorEmail = "";
                return true;
            }
        } else {
            mensajeErrorEmail = "El email no puede estar vacío";
            return false;
        }
    }

    public static boolean validarTelefono(String telefono) {
        if (telefono != null && !telefono.trim().isEmpty()) {
            telefono = telefono.trim();
            if (!telefono.matches("\\d+")) {
                return false;
            } else {
                return telefono.length() >= 9 && telefono.length() <= 10;
            }
        } else {
            return false;
        }
    }

    public static boolean validarNumeroPositivo(double numero) {
        return numero > (double)0.0F;
    }

    public static boolean validarNumeroPositivo(int numero) {
        return numero > 0;
    }

    public static boolean validarNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static boolean validarRuc(String ruc) {
        if (ruc != null && !ruc.trim().isEmpty()) {
            if (ruc.length() != 13) {
                mensajeErrorRuc = "El RUC debe tener exactamente 13 dígitos";
                return false;
            } else if (!ruc.matches("\\d+")) {
                mensajeErrorRuc = "El RUC debe contener solo números";
                return false;
            } else {
                int provincia = Integer.parseInt(ruc.substring(0, 2));
                if (provincia >= 1 && provincia <= 24) {
                    if (ruc.substring(10).equals("000")) {
                        mensajeErrorRuc = "El número de establecimiento no puede ser 000";
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    mensajeErrorRuc = "Código de provincia inválido (debe ser entre 01 y 24)";
                    return false;
                }
            }
        } else {
            mensajeErrorRuc = "El RUC no puede estar vacío";
            return false;
        }
    }

    public static String getMensajeErrorCedula() {
        return mensajeErrorCedula;
    }

    public static String getMensajeErrorEmail() {
        return mensajeErrorEmail;
    }

    public static String getMensajeErrorRuc() {
        return mensajeErrorRuc;
    }

    public static String getMensajeErrorNumeroPositivo(String campo) {
        return campo + " debe ser un número positivo mayor a cero";
    }
}