//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package excepciones;

public class Validador {
    private static String mensajeErrorCedula = "";
    private static String mensajeErrorEmail = "";
    private static String mensajeErrorRuc = "";

    public static boolean validarCedulaEcuatoriana(String cedula) {
        if (cedula != null && !cedula.trim().isEmpty()) {
            cedula = cedula.trim();
            if (cedula.length() != 10) {
                mensajeErrorCedula = "La cédula debe tener exactamente 10 dígitos";
                return false;
            } else if (!cedula.matches("\\d+")) {
                mensajeErrorCedula = "La cédula debe contener solo números";
                return false;
            } else {
                mensajeErrorCedula = "";
                return true;
            }
        } else {
            mensajeErrorCedula = "La cédula no puede estar vacía";
            return false;
        }
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