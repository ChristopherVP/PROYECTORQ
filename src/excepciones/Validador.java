//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package excepciones;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import objetosServicio.Fecha;

public class Validador {
    /** Proveedores de correo aceptados en las cuentas de usuario (misma lista que el sistema Correo). */
    private static final List<String> DOMINIOS_CORREO = Arrays.asList(
            "@gmail.com", "@outlook.com", "@hotmail.com", "@live.com", "@msn.com",
            "@yahoo.com", "@yahoo.es", "@yahoo.mx", "@icloud.com", "@me.com", "@mac.com",
            "@proton.me", "@protonmail.com", "@zoho.com", "@zohomail.com",
            "@gmx.com", "@gmx.net", "@aol.com", "@uce.edu.ec");

    /** Índice = código de provincia de la cédula (01-24). */
    private static final String[] PROVINCIAS = {
            "Desconocida", "Azuay", "Bolívar", "Cañar", "Carchi", "Cotopaxi", "Chimborazo", "El Oro",
            "Esmeraldas", "Guayas", "Imbabura", "Loja", "Los Ríos", "Manabí", "Morona Santiago", "Napo",
            "Pastaza", "Pichincha", "Tungurahua", "Zamora Chinchipe", "Galápagos", "Sucumbíos", "Orellana",
            "Santo Domingo de los Tsáchilas", "Santa Elena"};

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

    /** Email opcional: vacío es válido; si hay texto debe ser minúsculas, con formato y dominio permitido. */
    public static String errorEmailOpcional(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return errorEmailCuenta(email);
    }

    /** Email obligatorio de una cuenta: formato, minúsculas, máximo 50 caracteres y dominio permitido. */
    public static String errorEmailCuenta(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "El correo es obligatorio";
        }
        String e = email.trim();
        if (!validarEmail(e)) {
            return mensajeErrorEmail;
        }
        if (e.length() > 50) {
            return "El correo no puede superar los 50 caracteres";
        }
        for (String dominio : DOMINIOS_CORREO) {
            if (e.endsWith(dominio)) {
                return null;
            }
        }
        return "Dominio de correo no permitido. Usa Gmail, Outlook, Hotmail, Yahoo, iCloud, Proton, Zoho, GMX, AOL o @uce.edu.ec";
    }

    /** Teléfono opcional: vacío es válido; si hay texto debe ser un celular ecuatoriano (09 + 8 dígitos). */
    public static String errorTelefonoOpcional(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        return errorTelefonoCelular(telefono);
    }

    /** Celular ecuatoriano obligatorio: exactamente 10 dígitos y empieza con 09. */
    public static String errorTelefonoCelular(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return "El teléfono es obligatorio";
        }
        String t = telefono.trim();
        if (!t.matches("\\d{10}")) {
            return "El teléfono debe tener exactamente 10 dígitos numéricos";
        }
        if (!t.startsWith("09")) {
            return "El teléfono debe comenzar con 09";
        }
        return null;
    }

    /** Nombre completo: solo letras y espacios, al menos nombre y apellido, entre 5 y 50 caracteres. */
    public static String errorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio";
        }
        String n = nombre.trim();
        if (!n.matches("[\\p{L} ]+")) {
            return "El nombre solo puede contener letras y espacios";
        }
        if (n.length() < 5 || n.length() > 50) {
            return "El nombre debe tener entre 5 y 50 caracteres";
        }
        if (!n.matches("\\p{L}+( +\\p{L}+)+")) {
            return "Escribe tu nombre y apellido";
        }
        return null;
    }

    /** Nombre de la provincia según los dos primeros dígitos de la cédula (o vacío si aún no se puede saber). */
    public static String provinciaPorCedula(String cedula) {
        if (cedula == null || cedula.length() < 2 || !cedula.substring(0, 2).matches("\\d{2}")) {
            return "";
        }
        int codigo = Integer.parseInt(cedula.substring(0, 2));
        if (codigo >= 1 && codigo <= 24) {
            return PROVINCIAS[codigo];
        }
        return codigo == 30 ? "Ecuatoriano en el exterior" : "Código de provincia inválido";
    }

    /**
     * Valida una fecha escrita como dd/mm/aaaa (día real del mes, años bisiestos incluidos).
     * Devuelve null si es válida o el mensaje del problema.
     */
    public static String errorFormatoFecha(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "La fecha de nacimiento es obligatoria";
        }
        if (!texto.trim().matches("\\d{2}/\\d{2}/\\d{4}")) {
            return "Usa el formato dd/mm/aaaa (ejemplo: 15/03/2000)";
        }
        String[] p = texto.trim().split("/");
        int dia = Integer.parseInt(p[0]);
        int mes = Integer.parseInt(p[1]);
        int anio = Integer.parseInt(p[2]);
        if (mes < 1 || mes > 12) {
            return "Mes inválido: debe estar entre 1 y 12";
        }
        try {
            LocalDate.of(anio, mes, dia);
        } catch (DateTimeException e) {
            return "Ese día no existe en el mes indicado";
        }
        return null;
    }

    /** Fecha dd/mm/aaaa de una persona mayor de edad (18 a 120 años, no futura). Devuelve null si es válida. */
    public static String errorMayorDeEdad(String texto) {
        String problema = errorFormatoFecha(texto);
        if (problema != null) {
            return problema;
        }
        String[] p = texto.trim().split("/");
        LocalDate nacimiento = LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]), Integer.parseInt(p[0]));
        if (nacimiento.isAfter(LocalDate.now())) {
            return "La fecha de nacimiento no puede ser futura";
        }
        int edad = java.time.Period.between(nacimiento, LocalDate.now()).getYears();
        if (edad < 18) {
            return "Debes ser mayor de edad (18 años) para crear una cuenta";
        }
        if (edad > 120) {
            return "La fecha de nacimiento no es válida";
        }
        return null;
    }

    /** Convierte "dd/mm/aaaa" en Fecha; devuelve null si el texto no es una fecha válida. */
    public static Fecha fechaDesdeTexto(String texto) {
        if (errorFormatoFecha(texto) != null) {
            return null;
        }
        String[] p = texto.trim().split("/");
        return new Fecha(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
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